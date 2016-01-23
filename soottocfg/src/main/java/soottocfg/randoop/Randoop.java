package soottocfg.randoop;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
  public Randoop(){
   this(new BasicExecutionLog(System.out));
  }

  /**
   * Constructs a new Randoop command.
   *
   * @param log the execution log that monitors our actions.
   * @param java java command
   */
  Randoop(ExecutionLog log, String java){
    this.builder = Command.of(log);
    builder.args(java);
  }

  /**
   * Constructs a new Randoop command.
   *
   * @param log the execution log that monitors our actions.
   */
  Randoop(ExecutionLog log){
    this(log, "java");
  }

  public Randoop enableAssertions(){
    builder.args("-ea");
    return this;
  }

  public RandoopBuilder classpath(String... path) {
    final List<File> paths = Arrays.asList(path)
      .stream()
      .map(File::new)
      .collect(Collectors.toList());

    return classpath(paths);
  }


  public RandoopBuilder classpath(File... path) {
    return classpath(Arrays.asList(path));
  }


  private RandoopBuilder classpath(List<File> paths) {
    final Classpath classpath = Classpath.of(paths);

    if(classpath.isEmpty()){
      builder.args(
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
    builder.args("-classpath", classpath.toString());
    return this;
  }

  private Randoop extraArgs(List<String> extra) {
    builder.args(extra);
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
      builder.args("--testclass=" + Objects.requireNonNull(fullyQualifiedClassName));
      return this;
    }

    public RandoopBuilder classList(File classListFile){
      builder.args(
        "--classlist=" +
        Classpath.of(
          Objects.requireNonNull(classListFile)
        ).toString()
      );
      return this;
    }

    public RandoopBuilder destination(File directory) {
      builder.args("--junit-output-dir=" + directory.toString());
      return this;
    }

    public RandoopBuilder timeLimit(int seconds){
      builder.args("--timelimit=" + seconds);
      return this;
    }

    public RandoopBuilder silentlyIgnoreBadClassNames(){
      builder.args("--silently-ignore-bad-class-names=true");
      return this;
    }


    public List<String> execute(){
      return builder.execute();
    }
  }

  public static void main(String[] args) {
    final File file = Paths.get(
      System.getProperty("user.dir") + "/soottocfg/lib/randoop-2.1.0.jar"
    ).toFile();

    final Randoop randoop = new Randoop().enableAssertions();
    randoop.classpath(file)
      .destination(new File("/Users/hsanchez/dev/throwaway/garbage/"))
      .testClass("java.util.TreeSet")
      .silentlyIgnoreBadClassNames()
      .timeLimit(60)
      .execute();
  }
}
