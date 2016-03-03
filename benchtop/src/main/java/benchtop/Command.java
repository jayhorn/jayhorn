package benchtop;

import benchtop.utils.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Huascar Sanchez
 */
public class Command {

  private ExecutionLog log;
  private List<String> args;

  private final Map<String, String> environment;
  private final File workingDirectory;
  private final boolean permitNonZeroExitStatus;
  private final PrintStream console;


  private volatile Process process;

  /**
   * Constructs a new Command object for a list of arguments.
   * @param args the list of arguments.
   */
  public Command(List<String> args){
    this(new BasicExecutionLog(System.out), args);
  }

  /**
   * Constructs a new command.
   *
   * @param log the execution log
   * @param args the list of arguments needed by the command
   */
  public Command(ExecutionLog log, List<String> args){
    this.log  = log;
    this.args = new ArrayList<>(args);
    this.environment = Collections.emptyMap();

    this.workingDirectory         = null;
    this.permitNonZeroExitStatus  = false;
    this.console = null;
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
    this.environment = nonNullBuilder.env;

    this.workingDirectory         = nonNullBuilder.workingDirectory;
    this.permitNonZeroExitStatus  = nonNullBuilder.permitNonZeroExitStatus;
    this.console = nonNullBuilder.console;

    // checks if we maxed out the number of budgeted arguments
    if (nonNullBuilder.maxCommandLength != -1) {
      final String string = toString();
      if (string.length() > nonNullBuilder.maxCommandLength) {
        throw new IllegalStateException("Maximum command length " + nonNullBuilder.maxCommandLength
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
    log.info("starting command " + this.toString());

    final ProcessBuilder processBuilder = new ProcessBuilder()
      .command(args)
      .redirectErrorStream(true);

    if(workingDirectory != null){
      processBuilder.directory(workingDirectory);
    }

    processBuilder.environment().putAll(environment);

    process = processBuilder.start();
  }

  /**
   * @return true if the process has started; false otherwise.
   */
  public boolean isStarted() {
    return process != null;
  }

  /**
   * @return the current input stream used by running process.
   */
  public InputStream getInputStream() {
    if (!isStarted()) {
      throw new IllegalStateException("Not started!");
    }

    return process.getInputStream();
  }

  /**
   * Returns the output returned by process.
   *
   * @return the output on terminal.
   *
   * @throws IOException unexpected behavior occurred.
   * @throws InterruptedException unexpected behavior occurred.
   */
  public List<String> gatherOutput()
    throws IOException, InterruptedException {
    if (!isStarted()) {
      throw new IllegalStateException("Not started!");
    }

    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getInputStream(), "UTF-8"))) {
      final List<String> outputLines = new ArrayList<>();
      String outputLine;
      while ((outputLine = bufferedReader.readLine()) != null) {
        if (console != null) {
          console.println(outputLine);
        }

        outputLines.add(outputLine);
      }

      int exitValue = process.waitFor();

      if (exitValue != 0 && !permitNonZeroExitStatus) {
        throw new CommandFailedException(args, outputLines);
      }

      return outputLines;

    }
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


  @Override public String toString() {
    String envString = !environment.isEmpty() ? (Strings.joinCollection(" ", environment.entrySet()) + " ") : "";
    return envString + Strings.joinCollection(" ", args);
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
    private PrintStream console;
    private int         maxCommandLength;


    /**
     * Creates a command builder.
     *
     * @param log the execution log that monitors builder actions.
     */
    Builder(ExecutionLog log){
      this.log = Objects.requireNonNull(log);

      this.workingDirectory         = null;
      this.permitNonZeroExitStatus  = false;

      this.console = null;
      this.maxCommandLength = Integer.MAX_VALUE;

      this.args = new ArrayList<>();
      this.env  = new LinkedHashMap<>();
    }


    /**
     * Sets the command's arguments.
     *
     * @param args the command's arguments.
     * @return self
     */
    public Builder arguments(Object... args){
      return arguments(Arrays.asList(args));
    }

    /**
     * Sets the command's list of arguments.
     *
     * @param args the command's list of arguments.
     * @return self
     */
    public Builder arguments(List<?> args){
      for (Object eachObject : args) {
        this.args.add(eachObject.toString());
      }

      return this;
    }

    /**
     * Sets an environment's variable.
     *
     * @param key key identifying the variable
     * @param value the value of the variable
     * @return self
     */
    public Builder environment(String key, String value){
      env.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
      return this;
    }

    /**
     * Sets the command's working directory.
     *
     * @param localWorkingDirectory the command's working directory.
     * @return self
     */
    public Builder workingDirectory(File localWorkingDirectory){
      this.workingDirectory = Objects.requireNonNull(localWorkingDirectory);
      return this;
    }

    /**
     * Prevents execute() from throwing if the invoked process returns a
     * nonzero exit code.
     *
     * @return self
     */
    public Builder permitNonZeroExitStatus() {
      this.permitNonZeroExitStatus = true;
      return this;
    }

    /**
     * Sets the command's print stream.
     *
     * @param printStream the used print stream; e.g., System.out or System.err.
     * @return self
     */
    public Builder console(PrintStream printStream) {
      console = printStream;
      return this;
    }

    /**
     * Sets the permitted length of a command in its String representation.
     *
     * @param maxLength the length of a command (string representation of command)
     * @return self.
     */
    public Builder maxCommandLength(int maxLength) {
      this.maxCommandLength = maxLength;
      return this;
    }

    /**
     * @return the built command.
     */
    public Command build(){
      return new Command(this);
    }

    @Override public String toString() {
      return this.args.toString() + " : " + workingDirectory.toString();
    }
  }

  /**
   * Command failed to execute exception.
   */
  @SuppressWarnings("serial")
public static class CommandFailedException extends RuntimeException {

    /**
     * Construct a new CommandFailedException object.
     *
     * @param args list of command's args.
     * @param outputLines list of output lines displayed on terminal.
     */
    public CommandFailedException(List<String> args, List<String> outputLines) {
      super(formatMessage(args, outputLines));
    }

    /**
     * Turns a list of args and output lines into a formatted message.
     *
     * @param args list of command's args.
     * @param outputLines list of output lines displayed on terminal.
     * @return formatted message.
     */
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
}
