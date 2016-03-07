package benchtop.configs;

import benchtop.AbstractConfiguration;
import benchtop.Classpath;
import benchtop.utils.IO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Huascar Sanchez
 */
public abstract class RandoopConfiguration extends AbstractConfiguration {
  private static final String TOOL      = "randoop.jar";
  private static final String MAIN      = "randoop.main.Main";
  private static final String GENERATE  = "gentests";


  private final File          destination;
  private final int           timeLimit;
  private final Classpath     classpath;
  private final List<String>  classList;


  /**
   * Creates a new {@link RandoopConfiguration configuration} object.
   *
   * @param classpath the environment's classpath.
   * @param destination the output directory.
   * @param timeLimit halts test generation by this limit.
   * @param classList the test generation seed.
   */
  public RandoopConfiguration(Classpath classpath, File destination,
                              int timeLimit, String... classList){

    this.classpath    = Classpath.union(randoopClasspath(), classpath);
    this.destination  = Objects.requireNonNull(destination);

    this.timeLimit    = timeLimit;
    this.classList    = Arrays.asList(classList);

    if(this.classList.isEmpty()){
      throw new IllegalArgumentException("Need at list one target class.");
    }
  }


  /**
   * Returns Randoop's default configuration.
   *
   * @param classpath the environment's classpath.
   * @param destination the output directory.
   * @param timeLimit halts test generation by this limit.
   * @param classList the test generation seed.
   * @return a new {@code RandoopConfiguration} object.
   */
  public static RandoopConfiguration defaultConfiguration(Classpath classpath, File destination,
                                                          int timeLimit, String... classList){
    return new RandoopConfiguration(classpath, destination, timeLimit, classList) {
      @Override protected void configureRandoop() {
        applyDefaultOptionsOnce();
      }
    };
  }


  @Override protected void configure() {
    configureRequiredOptions();
    configureRandoop();
  }

  /**
   * Out-of-the-box randoop's configuration. Intended to be speed up Randoop's configuration.
   *
   * <p>
   *   Options include:
   *   <ul>
   *     <li>OnlyTestPublicMembers</li>
   *     <li>SilentlyIgnoreBadClassNames</li>
   *     <li>DiscardOutOfMemoryErrors</li>
   *     <li>60 seconds time limit</li>
   *   </ul>
   * </p>
   *
   *
   * Re-adding any of the above configuration options will lead to runtime errors due
   * to duplication of options.
   */
  protected void applyDefaultOptionsOnce(){
    onlyTestPublicMembers();
    silentlyIgnoreBadClassNames();
    discardOutOfMemoryErrors();
    timeLimit(timeLimit);
  }

  /**
   * Configures Randoop. Expected to be implemented by clients of this API.
   */
  protected abstract void configureRandoop();

  private void configureRequiredOptions(){
    arguments("java");
    arguments("-ea"); // enable assertions
    arguments("-classpath", classpath.toString(), MAIN, GENERATE); // randoop's classpath
    arguments("--junit-output-dir=" + destination.toString()); // output
    testClasses(classList);
    permitNonZeroExitStatus();
  }

  /**
   * @return Jayhorn's current classpath.
   */
  public static Classpath randoopClasspath(){
    return Classpath.of(randoopJar());
  }

  /**
   * @return randoop.jar file.
   */
  public static File randoopJar(){	  
	  //TODO: that's not good ... load from resources or sth. 
    return of("lib/" + TOOL);
  }

  private void testClasses(List<String> fullyQualifiedClassNames){
    //noinspection Convert2streamapi
    for(String qualifiedName : fullyQualifiedClassNames){ // unchecked warning
      testClass(qualifiedName);
    }
  }

  protected void testClass(String fullyQualifiedClassName){
    // this option can be repeated as many times as one wishes
    arguments("--testclass="+ Objects.requireNonNull(fullyQualifiedClassName));
  }


  protected void silentlyIgnoreBadClassNames(){
    arguments("--silently-ignore-bad-class-names=true");
  }

  protected void disallowJUnitReflection(){
    arguments("--junit-reflection-allowed=false");
  }


  protected void onlyTestPublicMembers(){
    arguments("--only-test-public-members=true");
  }

  protected void discardOutOfMemoryErrors(){
    arguments("--oom-exception=INVALID");
  }

  protected void includeOutOfMemoryErrorsInErrorRevealingTests(){
    arguments("--oom-exception=ERROR");
  }

  protected void includeOutOfMemoryErrorsInRegressionTests(){
    arguments("--oom-exception=EXPECTED");
  }

  protected void timeLimit(int seconds){
    arguments("--timelimit=" + seconds);
  }

  /**
   * @return the location of randoop tests
   */
  public static File randoopOutput(){
    final File directory = of(
      System.getProperty("user.dir")
        + "/build/test/resources/randoop_tests/"
    );

    if(!directory.exists()){
      make(directory);
    } else {
      delete(directory);
      make(directory);
    }

    return directory;
  }

  private static void delete(File directory){
    try {
      IO.deleteDirectoryContent(directory);
    } catch (IOException e) {
      System.out.println("unable to delete " + directory.toString());
      e.printStackTrace(System.err);
    }
  }

  private static void make(File directory){
    if(!directory.mkdirs()){
      System.out.println("Unable to create " + directory.toString());
    }
  }

}
