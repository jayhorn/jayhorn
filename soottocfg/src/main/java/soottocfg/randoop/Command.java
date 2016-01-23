package soottocfg.randoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author Huascar Sanchez
 */
public class Command {

  private static final ScheduledExecutorService TIMER =
    Executors.newSingleThreadScheduledExecutor();

  private ExecutionLog log;
  private List<String> args;

  private final Map<String, String> env;
  private final File workingDirectory;
  private final boolean permitNonZeroExitStatus;
  private final PrintStream tee;


  private volatile Process process;
  private volatile boolean destroyed;
  private volatile long timeoutNanoTime;

  /**
   * Constructs a new command.
   *
   * @param log the execution log
   * @param args the list of arguments needed by the command
   */
  public Command(ExecutionLog log, List<String> args){
    this.log  = log;
    this.args = new ArrayList<>(args);
    this.env  = Collections.emptyMap();

    this.workingDirectory         = null;
    this.permitNonZeroExitStatus  = false;
    this.tee                      = null;
  }

  /**
   * Constructs a new Command using elements specified in its builder.
   *
   * @param builder the command builder.
   */
  private Command(Builder builder){
    final Builder nonNullBuilder = Objects.requireNonNull(builder);

    this.log  = nonNullBuilder.log;
    this.args = new ArrayList<>(nonNullBuilder.args);
    this.env  = nonNullBuilder.env;

    this.workingDirectory         = nonNullBuilder.workingDirectory;
    this.permitNonZeroExitStatus  = nonNullBuilder.permitNonZeroExitStatus;
    this.tee = nonNullBuilder.tee;

    // checks if we maxed out the number of budgeted arguments
    if (nonNullBuilder.maxLength != -1) {
      final String string = toString();
      if (string.length() > nonNullBuilder.maxLength) {
        throw new IllegalStateException("Maximum command length " + nonNullBuilder.maxLength
          + " exceeded by: " + string);
      }
    }
  }


  /**
   * Creates a Command.Builder object
   *
   * @param log the execution log
   * @return a new command builder object
   */
  public static Builder of(ExecutionLog log){
    return new Builder(log);
  }

  /**
   * starts the command
   *
   * @throws IOException if unable to start command.
   */
  public void start() throws IOException {
    if(isStarted()){
      throw new IllegalStateException("Already started!");
    }

    log.info("starting command");

    final ProcessBuilder processBuilder = new ProcessBuilder()
      .command(args)
      .redirectErrorStream(true);

    if(workingDirectory != null){
      processBuilder.directory(workingDirectory);
    }

    processBuilder.environment().putAll(env);

    process = processBuilder.start();
  }

  public boolean isStarted() {
    return process != null;
  }

  public InputStream getInputStream() {
    if (!isStarted()) {
      throw new IllegalStateException("Not started!");
    }

    return process.getInputStream();
  }


  public List<String> gatherOutput()
    throws IOException, InterruptedException {
    if (!isStarted()) {
      throw new IllegalStateException("Not started!");
    }

    BufferedReader in = new BufferedReader(
      new InputStreamReader(getInputStream(), "UTF-8"));
    List<String> outputLines = new ArrayList<String>();
    String outputLine;
    while ((outputLine = in.readLine()) != null) {
      if (tee != null) {
        tee.println(outputLine);
      }
      outputLines.add(outputLine);
    }

    int exitValue = process.waitFor();
    destroyed = true;

    if (exitValue != 0 && !permitNonZeroExitStatus) {
      throw new CommandFailedException(args, outputLines);
    }

    return outputLines;
  }


  /**
   * @return true if the command's execution has timed out; false otherwise.
   */
  public boolean timedOut() {
    return System.nanoTime() >= timeoutNanoTime;
  }

  /**
   * @return the output displayed on the terminal.
   */
  public List<String> execute() {
    try {
      start();
      return gatherOutput();
    } catch (IOException e) {
      throw new RuntimeException("Failed to execute process: " + args, e);
    } catch (InterruptedException e) {
      throw new RuntimeException("Interrupted while executing process: " + args, e);
    }
  }


  /**
   * Executes a command with a specified timeout. If the process does not
   * complete normally before the timeout has elapsed, it will be destroyed.
   *
   * @param timeoutSeconds how long to wait, or 0 to wait indefinitely
   * @return the command's output, or null if the command timed out
   */
  public List<String> executeWithTimeout(int timeoutSeconds) throws TimeoutException {
    if (timeoutSeconds == 0) {
      return execute();
    }

    scheduleTimeout(timeoutSeconds);
    return execute();
  }


  /**
   * Destroys the underlying process and closes its associated streams.
   */
  public void destroy() {
    Process process = this.process;
    if (process == null) {
      throw new IllegalStateException();
    }
    if (destroyed) {
      return;
    }

    destroyed = true;
    process.destroy();

    try {
      process.waitFor();
      int exitValue = process.exitValue();
      log.info("received exit value " + exitValue + " from destroyed command " + this);
    } catch (IllegalThreadStateException | InterruptedException destroyUnsuccessful) {
      log.warn("couldn't destroy " + this);
    }
  }

  /**
   * Sets the time at which this process will be killed. If a timeout has
   * already been scheduled, it will be rescheduled.
   */
  public void scheduleTimeout(int timeoutSeconds) {
    timeoutNanoTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSeconds);

    new TimeoutTask() {
      @Override protected void onTimeout(Process process) {
        // send a quit signal immediately
        log.info("sending quit signal to command " + Command.this);
        sendQuitSignal(process);

        // hard kill in 2 seconds
        timeoutNanoTime = System.nanoTime() + TimeUnit.SECONDS.toNanos(2);
        new TimeoutTask() {
          @Override protected void onTimeout(Process process) {
            log.info("killing timed out command " + Command.this);
            destroy();
          }
        }.schedule();
      }
    }.schedule();
  }

  private void sendQuitSignal(Process process) {
    // sends a signal to kill the given process
    new Command(
      log,
      Arrays.asList(
        "kill",
        "-3",
        Integer.toString(getPid(process)))
    ).execute();
  }

  /**
   * Returns the PID of this command's process.
   */
  private int getPid(Process process) {
    try {
      // See org.openqa.selenium.ProcessUtils.getProcessId()
      Field field = process.getClass().getDeclaredField("pid");
      field.setAccessible(true);
      return (Integer) field.get(process);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Override public String toString() {
    String envString = !env.isEmpty() ? (Strings.joinCollection(" ", env.entrySet()) + " ") : "";
    return envString + String.join(" ", args);
  }


  /**
   * Command builder
   */
  public static class Builder {
    private final ExecutionLog        log;
    private final List<String>        args;
    private final Map<String, String> env;

    private File        workingDirectory;
    private boolean     permitNonZeroExitStatus;
    private PrintStream tee;
    private int         maxLength;


    /**
     * Creates a command builder.
     *
     * @param log the execution log that monitors builder actions.
     */
    Builder(ExecutionLog log){
      this.log = Objects.requireNonNull(log);

      this.workingDirectory         = null;
      this.permitNonZeroExitStatus  = false;

      this.tee        = null;
      this.maxLength  = -1;

      this.args = new ArrayList<>();
      this.env  = new LinkedHashMap<>();
    }


    public Builder args(Object... args){
      return args(Arrays.asList(args));
    }

    public Builder args(List<?> args){
      this.args.addAll(args.stream()
        .map(Object::toString)
        .collect(Collectors.toList()));

      return this;
    }

    public Builder env(String key, String value){
      env.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
      return this;
    }

    public Builder workingDirectory(File localWorkingDirectory){
      this.workingDirectory = Objects.requireNonNull(localWorkingDirectory);
      return this;
    }

    /**
     * Prevents execute() from throwing if the invoked process returns a
     * nonzero exit code.
     */
    public Builder permitNonZeroExitStatus() {
      this.permitNonZeroExitStatus = true;
      return this;
    }

    public Builder tee(PrintStream printStream) {
      tee = printStream;
      return this;
    }

    public Builder maxArgsLength(int maxLength) {
      this.maxLength = maxLength;
      return this;
    }

    public Command build(){
      return new Command(this);
    }

    public List<String> execute() {
      return build().execute();
    }

    public List<String> executeWithTimeout(int timeoutSeconds) {
      try {
        return build().executeWithTimeout(timeoutSeconds);
      } catch (TimeoutException e) {
        return Collections.singletonList("Process timed out!");
      }
    }


  }

  /**
   * Command failed to execute exception.
   */
  public static class CommandFailedException extends RuntimeException {

    public CommandFailedException(List<String> args, List<String> outputLines) {
      super(formatMessage(args, outputLines));
    }

    static String formatMessage(List<String> args, List<String> outputLines) {
      StringBuilder result = new StringBuilder();
      result.append("Command failed:");

      for (String arg : args) {
        result.append(" ").append(arg);
      }

      for (String outputLine : outputLines) {
        result.append("\n  ").append(outputLine);
      }

      return result.toString();
    }
  }


  /**
   * Runs some code when the command times out.
   */
  private abstract class TimeoutTask implements Runnable {
    public final void schedule() {
      TIMER.schedule(this, System.nanoTime() - timeoutNanoTime, TimeUnit.NANOSECONDS);
    }

    protected abstract void onTimeout(Process process);

    @Override public final void run() {
      // don't destroy commands that have already been destroyed
      Process process = Command.this.process;
      if (destroyed) {
        return;
      }

      if (timedOut()) {
        onTimeout(process);
      } else {
        // if the kill time has been pushed back, reschedule
        TIMER.schedule(this, System.nanoTime() - timeoutNanoTime, TimeUnit.NANOSECONDS);
      }
    }
  }
}
