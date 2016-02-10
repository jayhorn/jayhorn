package soottocfg.randoop;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Huascar Sanchez
 */
public class Randoop {

  private static final String TOOL = "randoop.jar";
  private static final String MAIN = "randoop.main.Main";
  private static final String GENERATE = "gentests";

  private final Command.Builder builder;

  private static Set<String> optionMonitor = new LinkedHashSet<>();

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
    optionMonitor = new LinkedHashSet<>();

    this.builder = Command.of(log)
      .console(System.out);

    builder.arguments(ensureSingleUsage(java));
  }

  /**
   * Constructs a new Randoop command.
   *
   * @param log the execution log that monitors our actions.
   */
  Randoop(ExecutionLog log){
    this(log, "java");
  }

  // keep track of used options
  private static String ensureSingleUsage(String option){
    final String nonNullOption = Objects.requireNonNull(option);
    if(optionMonitor.contains(nonNullOption)) {
      throw new IllegalArgumentException(
        "Option " + nonNullOption + " already been set"
      );
    }

    optionMonitor.add(Objects.requireNonNull(option));
    return option;
  }

  // keep track of used options
  private List<String> ensureSingleUsage(List<String> args){
    final List<String> nonNullOption = Objects.requireNonNull(args);
    for(String arg : nonNullOption){
      ensureSingleUsage(arg);
    }

    return args;
  }

  /**
   * @return randoop.jar file.
   */
  public static File randoopJar(){
    return Randoop.of(System.getProperty("user.dir") + "/soottocfg/lib/randoop-2.1.1.jar");
  }

  /**
   * @return Jayhorn's classpath.
   */
  public static String jayHornPath(){

    return System.getProperty("java.class.path")
      + ":" + System.getProperty("user.dir") + "/jayhorn/build/classes/main/"
      + ":" + System.getProperty("user.dir") + "/jayhorn/build/resources/main/"
      + ":" + System.getProperty("user.dir") + "/jayhorn/lib/com.microsoft.z3.jar"
      + ":" + System.getProperty("user.dir") + "/jayhorn/lib/lazabs.jar"
      + ":" + System.getProperty("user.dir") + "/jayhorn/lib/princess.jar"
      + ":" + System.getProperty("user.dir") + "/jayhorn/native_lib/";
  }


  /**
   * Returns the classpath Jayhorn is using to run Randoop.
   *
   * @param withRandoop randoop file.
   * @return Jayhorn's path.
   */
  public static Classpath jayhornPath(File withRandoop){
    final String[] paths = Randoop.jayHornPath().split(":");

    final List<File> files = new ArrayList<>();

    for(String eachPath : paths){
      files.add(new File(eachPath));
    }

    files.add(withRandoop);

    return Classpath.of(files);
  }

  /**
   * @return Jayhorn's current classpath.
   */
  public static Collection<File> jayhornClasspath(){
    return Randoop.jayhornPath(Randoop.randoopJar()).getElements();
  }

  /**
   * @return the location of dynamic tests
   */
  public static File defaultOutput(){
    return Randoop.of(System.getProperty("user.dir") + "/soottocfg/src/test/resources/dynamic_tests/");
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
    return configure(Classpath.empty());
  }

  /**
   * Starts the randoop configuration process.
   *
   * @param path the application's classpath.
   * @return a new RandoopBuilder object.
   */
  public static RandoopBuilder configure(Classpath path){
    return configure(path, Randoop.defaultOutput());
  }

  /**
   * Starts the randoop configuration process.
   *
   * @param destination the destination directory where generated files will be placed.
   * @return a new RandoopBuilder object.
   */
  public static RandoopBuilder configure(File destination){
    return configure(Classpath.empty(), destination);
  }

  /**
   * Starts the randoop configuration process.
   *
   * @param destination the destination where generated files will be placed.
   * @return a new RandoopBuilder object.
   */
  public static RandoopBuilder configure(Classpath path, File destination){

    final Classpath nonNullPath   = Objects.requireNonNull(path);
    final File nonNullDestination = Objects.requireNonNull(destination);


    final List<File> updatedElements = Lists.newArrayList(
      Iterables.concat(
        Randoop.jayhornClasspath(),
        nonNullPath.getElements()
      )
    );

    return new Randoop()
      .enableAssertions()
      .classpath(updatedElements)
      .destination(nonNullDestination);
  }

  public Randoop enableAssertions(){
    builder.arguments(ensureSingleUsage("-ea"));
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

  private RandoopBuilder classpath(Collection<File> paths) {
    final Classpath classpath = Classpath.of(paths);

    if(classpath.isEmpty()){
      builder.arguments(
        ensureSingleUsage("-classpath"),
        TOOL, MAIN, GENERATE
      );
    } else {
      classpath(classpath)
        .extraArgs(Arrays.asList(MAIN, GENERATE));
    }

    return new RandoopBuilder(builder);
  }

  private Randoop classpath(Classpath classpath) {
    builder.arguments(
      ensureSingleUsage("-classpath"),
      classpath.toString()
    );
    return this;
  }

  private Randoop extraArgs(List<String> extra) {
    builder.arguments(ensureSingleUsage(extra));
    return this;
  }


  /**
   * Randoop's DSL or at least something that is close to that.
   */
  public static class RandoopBuilder {
    private Command.Builder builder;

    RandoopBuilder(Command.Builder builder){
      this.builder = builder;
    }

    public RandoopBuilder testClass(String fullyQualifiedClassName){
      // this option can be repeated as many times as one wishes
      builder().arguments("--testclass=" + Objects.requireNonNull(fullyQualifiedClassName));
      return this;
    }

    /**
     * Sets the classes we are interested in testing.
     *
     * @return this RandoopBuilder
     */
    public RandoopBuilder jayhorn(){
      testClasses(
        "jayhorn.Main",
        "jayhorn.Options",
        "jayhorn.Log",
        "jayhorn.util.ConvertToDiamondShape",
        "jayhorn.util.EdgeLabelToAssume",
        "jayhorn.util.LoopRemoval",
        "jayhorn.util.SimplCfgToProver",
        "jayhorn.util.SourceLocationUtil",
        "jayhorn.util.SsaPrinter",
        "jayhorn.util.SsaTransformer",
        "jayhorn.solver.Main",
        "jayhorn.solver.IntType",
        "jayhorn.solver.BoolType",
        "jayhorn.solver.ArrayType",
        "jayhorn.solver.z3.Z3ProverFactory",
        "jayhorn.solver.z3.Z3Prover",
        "jayhorn.solver.z3.Z3TermExpr",
        "jayhorn.solver.z3.Z3HornExpr",
        "jayhorn.solver.z3.Z3Fun",
        "jayhorn.solver.z3.Z3BoolExpr",
        "jayhorn.solver.z3.Z3ArrayType",
        "jayhorn.solver.princess.FormulaExpr",
        "jayhorn.solver.princess.TermExpr",
        "jayhorn.solver.princess.HornExpr",
        "jayhorn.solver.princess.PrincessFun",
        "jayhorn.solver.princess.PrincessProverExpr",
        "jayhorn.solver.princess.PrincessProverFactory",
        "jayhorn.solver.princess.PrincessProver",
        "soottocfg.cfg.ClassVariable",
        "soottocfg.cfg.LiveVars",
        "soottocfg.cfg.Program",
        "soottocfg.cfg.SourceLocation",
        "soottocfg.cfg.Variable",
        "soottocfg.cfg.util.BfsIterator",
        "soottocfg.cfg.util.CategorizeEdges",
        "soottocfg.cfg.util.DominanceFrontier",
        "soottocfg.cfg.util.Dominators",
        "soottocfg.cfg.util.EffectualSet",
        "soottocfg.cfg.util.GraphUtil",
        "soottocfg.cfg.util.LoopFinder",
        "soottocfg.cfg.util.PostDominators",
        "soottocfg.cfg.util.Tree",
        "soottocfg.cfg.util.UnreachableNodeRemover",
        "soottocfg.randoop.Classpath",
        "soottocfg.randoop.Command",
        "soottocfg.randoop.Javac",
        "soottocfg.randoop.Strings"
      );

      return this;
    }

    public RandoopBuilder testClasses(String... fullyQualifiedClassNames){
      for(String qualifiedName : fullyQualifiedClassNames){
        testClass(qualifiedName);
      }
      return this;
    }

    public RandoopBuilder permitNonZeroExitStatus(){
      builder().permitNonZeroExitStatus();
      return this;
    }

    public RandoopBuilder classList(File classListFile){
      builder().arguments(
        ensureSingleUsage("--classlist=") +
        Classpath.of(
          Objects.requireNonNull(classListFile)
        ).toString()
      );
      return this;
    }

    public RandoopBuilder omitmethods(String regex){
      builder().arguments(
        ensureSingleUsage("--omitmethods=") + Objects.requireNonNull(regex)
      );

      return this;
    }


    public RandoopBuilder onlyTestPublicMembers(){
      builder().arguments(
        ensureSingleUsage("--only-test-public-members=") + "true"
      );

      return this;
    }

    public RandoopBuilder jUnitPackageName(String packageName){

      builder().arguments(
        ensureSingleUsage("--junit-package-name=") + Objects.requireNonNull(packageName)
      );

      return this;
    }

    public RandoopBuilder destination(File directory) {
      builder().arguments(ensureSingleUsage("--junit-output-dir=") + directory.toString());
      return this;
    }

    public RandoopBuilder timeLimit(int seconds){
      builder().arguments(ensureSingleUsage("--timelimit=") + seconds);
      return this;
    }

    public RandoopBuilder discardOutOfMemoryErrors(){
      return classifyTestWithOutOfMemoryException(OutOfMemory.INVALID);
    }

    public RandoopBuilder includeOutOfMemoryErrorsInErrorRevealingTests(){
      return classifyTestWithOutOfMemoryException(OutOfMemory.ERROR);
    }

    public RandoopBuilder includeOutOfMemoryErrorsInRegressionTests(){
      return classifyTestWithOutOfMemoryException(OutOfMemory.EXPECTED);
    }

    RandoopBuilder classifyTestWithOutOfMemoryException(OutOfMemory enumValue){

      builder().arguments(
        ensureSingleUsage("--oom-exception=")
          + enumValue
      );

      return this;
    }

    public RandoopBuilder silentlyIgnoreBadClassNames(){
      builder().arguments(
        ensureSingleUsage("--silently-ignore-bad-class-names=")
          + "true"
      );

      return this;
    }

    Command.Builder builder(){
      return builder;
    }


    public List<String> execute(){
      return builder().execute();
    }


  }

  public static void main(String[] args) {

    Randoop.configure()
      .jayhorn()
      .silentlyIgnoreBadClassNames()
      .discardOutOfMemoryErrors()
      .permitNonZeroExitStatus()
      .timeLimit(60)
      .execute();
  }
}
