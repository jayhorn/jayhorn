package soottocfg.randoop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Huascar Sanchez
 */
public class Randoop {

  private static final String TOOL = "randoop.jar";
  private static final String MAIN = "randoop.main.Main";
  private static final String GENERATE = "gentests";

  private final Command.Builder builder;

  /**
   * Constructs a Randoop command.
   */
  Randoop(){
   this(new BasicExecutionLog(System.out));
  }

  /**
   * Constructs a new Randoop command.
   *
   * @param log the execution log that monitors our actions.
   * @param java java command
   */
  Randoop(ExecutionLog log, String java){
    this.builder = Command.of(log)
      .console(System.out);

    builder.arguments(java);
  }

  /**
   * Constructs a new Randoop command.
   *
   * @param log the execution log that monitors our actions.
   */
  Randoop(ExecutionLog log){
    this(log, "java");
  }

  /**
   * @return randoop.jar file.
   */
  public static File randoopJar(){
    return Randoop.of(System.getProperty("user.dir") + "/soottocfg/lib/randoop-2.1.1.jar");
  }

  /**
   * @return the location of dynamic tests
   */
  public static File defaultOutput(){
    return Randoop.of(System.getProperty("user.dir") + "src/test/resources/");
  }

  /**
   * Creates a File object for an object located at a given path,
   * where object is either a single file or a directory.
   *
   * @param path the path of the object.
   * @return new file object
   */
  public static File of(String path){
    return new File(path);
  }

  /**
   * Starts the randoop configuration process.
   *
   * @return a new RandoopBuilder object.
   */
  public static RandoopBuilder configure(){
    return configure(Randoop.defaultOutput());
  }

  /**
   * Starts the randoop configuration process.
   *
   * @param destination the destination where generated files will be placed.
   * @return a new RandoopBuilder object.
   */
  public static RandoopBuilder configure(File destination){
    return new Randoop()
      .enableAssertions()
      .classpath(Randoop.randoopJar())
      .destination(destination);
  }

  public Randoop enableAssertions(){
    builder.arguments("-ea");
    return this;
  }

  public RandoopBuilder classpath(String... path) {
    final List<File> paths = new ArrayList<>();
    for(String eachPath : path){
      paths.add(new File(eachPath));
    }

    return classpath(paths);
  }


  public RandoopBuilder classpath(File... path) {
    return classpath(Arrays.asList(path));
  }


  private RandoopBuilder classpath(List<File> paths) {
    final Classpath classpath = Classpath.of(paths);

    if(classpath.isEmpty()){
      builder.arguments(
        "-classpath",
        TOOL, MAIN, GENERATE
      );
    } else {
      classpath(classpath)
        .extraArgs(Arrays.asList(MAIN, GENERATE));
    }

    return new RandoopBuilder(builder);
  }

  private Randoop classpath(Classpath classpath) {
    builder.arguments("-classpath", classpath.toString());
    return this;
  }

  private Randoop extraArgs(List<String> extra) {
    builder.arguments(extra);
    return this;
  }


  /**
   * Randoop's DSL or at least something that is close to that.
   */
  static class RandoopBuilder {
    private Command.Builder builder;

    RandoopBuilder(Command.Builder builder){
      this.builder = builder;
    }

    public RandoopBuilder testClass(String fullyQualifiedClassName){
      builder.arguments("--testclass=" + Objects.requireNonNull(fullyQualifiedClassName));
      return this;
    }

    public RandoopBuilder classList(File classListFile){
      builder.arguments(
        "--classlist=" +
        Classpath.of(
          Objects.requireNonNull(classListFile)
        ).toString()
      );
      return this;
    }

    public RandoopBuilder omitmethods(String regex){

      builder.arguments(
        "--omitmethods=" + Objects.requireNonNull(regex)
      );

      return this;
    }

    public RandoopBuilder destination(File directory) {
      builder.arguments("--junit-output-dir=" + directory.toString());
      return this;
    }

    public RandoopBuilder timeLimit(int seconds){
      builder.arguments("--timelimit=" + seconds);
      return this;
    }

    public RandoopBuilder silentlyIgnoreBadClassNames(){
      builder.arguments("--silently-ignore-bad-class-names=true");
      return this;
    }


    public List<String> execute(){
      return builder.execute();
    }
  }

  public static void main(String[] args) {

    Randoop.configure(Randoop.of("/Users/hsanchez/dev/throwaway/garbage/"))
      .testClass("java.util.TreeSet")
      .silentlyIgnoreBadClassNames()
      .omitmethods("subSet\\|addAll")
      .timeLimit(60)
      .execute();

  }
}
