package benchtop;

import benchtop.spi.Configuration;
import benchtop.spi.JavaConfiguration;
import benchtop.spi.JavacConfiguration;
import benchtop.spi.RandoopConfiguration;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * A facade for running commands. These commands are configured via classes that implement
 * the {@link Configuration} interface.
 *
 * @author Huascar Sanchez
 */
public class Benchtop {
  /**
   * Benchtop's private constructor.
   */
  private Benchtop(){
    throw new Error("Cannot be instantiated!");
  }

  /**
   * Creates a command based on a given configuration.
   *
   * @param configuration The configuration of a command.
   * @return a new and configured command.
   */
  public static Command createCommand(Configuration configuration){
    final Command command = createCommand(configuration, newBasicExecutionLog());
    System.out.println(command);
    return command;
  }

  /**
   * Creates a command based on a given configuration and some execution log.
   *
   * @param configuration The configuration of a command.
   * @param executionLog A log that monitors command's execution.
   * @return a new and configured command.
   */
  public static Command createCommand(Configuration configuration, ExecutionLog executionLog){
    final Configuration nonNullConfiguration = Preconditions.checkNotNull(configuration);
    final Command.Builder builder = Command.of(Preconditions.checkNotNull(executionLog))
      .console(System.out);
    nonNullConfiguration.configure(builder);

    return builder.build();
  }

  /**
   * Consumes an non-empty array of bundles. See {@link ExecutionBundle} to understand
   * how bundles are constructed.
   *
   * @param bundles an array of bundle objects.
   * @return Benchtop's output monitor.
   * @throws BundleCreationError unexpected errors have occurred.
   */
  public static Result consumes(ExecutionBundle... bundles) throws BundleCreationError {
    final DefaultEnvironment host = new DefaultEnvironment();
    Result monitor = null;
    for(ExecutionBundle each : bundles){
      host.install(each);

      if(monitor == null){ monitor = host.getMonitor(); } else {
        monitor = monitor.combines(host.getMonitor());
      }

    }

    host.throwCachedErrors();

    return monitor;
  }

  /**
   * Executes a configured command.
   *
   * @param command the command to run
   * @return the command's output on terminal
   */
  public static List<String> run(Command command){
    final List<String> output = Preconditions.checkNotNull(command).execute();

    //noinspection Convert2streamapi
    for(String each : output){ // unchecked warning
      System.out.println(each);
    }

    return output;
  }


  /**
   * Creates a Javac command; ready to be executed. The classpath object is made of the project's
   * content; including classes, managed dependencies, and other dependencies under the
   * /lib directory. If one needs to update this classpath or have more control of what it is in
   * that classpath, then use the {@link Benchtop#java(Classpath, String, String...)} method
   * instead.
   *
   * @param destination the directory where compiled classes will be placed.
   * @param sourceFiles the array of source files to compile.
   * @return the command's output on terminal
   */
  public static List<String> javac(File destination, File... sourceFiles){
    return javac(Classpath.environmentClasspath() /*classpath is resolved automatically*/, destination, sourceFiles);
  }

  /**
   * Creates a Javac command; ready to be executed.
   *
   * @param classpath the required classpath to compile source files.
   * @param destination the directory where compiled classes will be placed.
   * @param sourceFiles the array of source files to compile.
   * @return the command's output on terminal
   */
  public static List<String> javac(Classpath classpath, File destination, File... sourceFiles){
    return javac(classpath, destination, ImmutableList.copyOf(sourceFiles));
  }


  /**
   * Creates a Javac command; ready to be executed.
   *
   * @param classpath the required classpath to compile source files.
   * @param destination the directory where compiled classes will be placed.
   * @param sourceFiles the array of source files to compile.
   * @return the command's output on terminal
   */
  public static List<String> javac(Classpath classpath, File destination, Collection<File> sourceFiles){
    return run(createCommand(
      JavacConfiguration.newJavacConfiguration(
        classpath,
        destination,
        sourceFiles
      )
    ));
  }

  /**
   * Creates a Junit command; ready to be executed.
   *
   * @param classpath the required classpath to run JUnit tests
   * @param args the array of parameters needed by JUnit to run. e.g., test class.
   * @return the command's output on terminal.
   */
  public static List<String> junit(Classpath classpath, String... args){
    return java(classpath, "org.junit.runner.JUnitCore", args);
  }

  /**
   * Creates a Java command; ready to be executed.
   *
   * @param classpath the required classpath to run Java program
   * @param mainClass the main class or Java program
   * @param args the args taken by the main class.
   * @return the command's output on terminal
   */
  public static List<String> java(Classpath classpath, String mainClass, String... args){
    return run(createCommand(
      JavaConfiguration.newJavaConfiguration(classpath, mainClass, args)
    ));
  }

  /**
   * Executes a Randoop command.
   *
   * @param classList list of classes needed by Randoop to generate tests.
   */
  public static List<String> randoop(String... classList){
    return randoop(Classpath.environmentClasspath(), classList);
  }

  /**
   * Executes a Randoop command.
   *
   * @param classpath required classpath by Randoop
   * @param classList list of classes needed by Randoop to generate tests.
   * @return the command's output on terminal
   */
  public static List<String> randoop(Classpath classpath, String... classList){
    return randoop(classpath, RandoopConfiguration.randoopOutput(), classList);
  }

  /**
   * Executes a Randoop command.
   *
   * @param classpath required classpath by Randoop
   * @param destination the location where these Randoop tests will be placed
   * @param classList list of classes needed by Randoop to generate tests.
   * @return the command's output on terminal
   */
  public static List<String> randoop(Classpath classpath, File destination, String... classList){
    return randoop(classpath, destination, 60, classList);
  }

  /**
   * Creates a Randoop command.
   *
   * @param classpath required classpath by Randoop
   * @param destination the location where these Randoop tests will be placed
   * @param timeLimit Randoop's time limit
   * @param classList list of classes needed by Randoop to generate tests.
   * @return the command's output on terminal
   */
  public static List<String> randoop(Classpath classpath, File destination,
                             int timeLimit, String... classList){
    return run(createCommand(
      RandoopConfiguration.defaultConfiguration(classpath, destination, timeLimit, classList)
    ));
  }


  private static ExecutionLog newBasicExecutionLog(){
    return new BasicExecutionLog(System.out);
  }


}
