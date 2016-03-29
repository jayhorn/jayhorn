package benchtop;

import benchtop.utils.Classes;
import benchtop.utils.IO;
import benchtop.utils.Soot;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class DefaultEnvironment implements Environment {

  private static final String DEFAULT_PREFIX = "Regression";

  private static final ExecutionLog LOG = new BasicExecutionLog(System.out);

  private File target; // project's class files
  private File output; // Randoop tests
  private File nontransformed; // a copy of classes in target
  private File transformed;   // a transformed copy of classes in target

  private int  timeout;
  private boolean transformations;

  private final List<Throwable> cachedErrors;
  private final Classpath       classpath;
  private final List<String>    classList;
  private final List<String>    testPrefixes;

  /**
   * Constructs a default bundle host.
   */
  public DefaultEnvironment(){
    this.target       = null;
    this.output       = null;
    this.timeout      = 60;
    this.transformations = false;
    this.transformed    = null;
    this.nontransformed = null;
    this.cachedErrors = new ArrayList<>();
    this.classpath    = Classpath.empty();
    this.classList    = new ArrayList<>();
    this.testPrefixes = new ArrayList<>();
  }

  @Override public void addError(Throwable cause) {
    cachedErrors.add(Preconditions.checkNotNull(cause));
  }

  @Override public void install(ExecutionBundle bundle) {
    bundle.configure(this);
    // delaying the execution of the bundle until has been fully configured guarantees that
    // clients of the api can call most of Environment's methods in any order. The exception
    // to this rule is bundleClasspath(). This method requires that bundleTarget and bundleOutput
    // have already been called.
    execute();
  }

  private void execute() {
    //final File temp = Files.createTempDir();
    try {
      Preconditions.checkArgument(this.timeout > 0, "Invalid timeout value");
      Preconditions.checkNotNull(this.target, "Target directory is null");
      Preconditions.checkNotNull(this.output, "Output directory is null");
      Preconditions.checkArgument(!this.classpath.isEmpty(), "Classpath is empty");
      Preconditions.checkArgument(!this.classList.isEmpty(), "Classlist is empty");


      // copy of classpath pointing to classes that have not been transformed
      // by SOOT
      final Classpath copy = Classpath.union(
        Classpath.of(this.nontransformed),
        this.classpath
      );



      LOG.info("Generate Randoop tests for:\n" + Joiner.on("\n").join(classList));

      // runs randoop
      Benchtop.randoop(
        copy,
        this.output,
        this.timeout,
        classList.toArray(new String[classList.size()])
      );

      // compiles produced test files
      final List<File> files = IO.collectFiles(this.output, "java");
      final List<Class<?>> listOfClasses = Classes.compileJava(
        copy, 1, this.output, files.toArray(new File[files.size()])
      );

      LOG.info("Same number of (compiled) Randoop tests: " + (files.size() == listOfClasses.size()));

      runJunit(listOfClasses, copy, this.testPrefixes);


      if(transformations){
        // transforms classes under this.output directory
        final Classpath secondCopy = Classpath.union(
          Classpath.of(this.transformed),
          this.classpath
        );

        Soot.sootifyJavaClasses(secondCopy, this.transformed, classList);

        runJunit(listOfClasses, secondCopy, this.testPrefixes);
      }

    } catch (Exception e){
      addError(e);

      LOG.error("Unexpected error", e);
    }

    // deleting temp folder
    try {
      IO.deleteDirectory(this.nontransformed.toPath());
      IO.deleteDirectory(this.transformed.toPath());
      LOG.info(String.format("Content of files %s and %s has been deleted!", this.nontransformed, this.transformed));
    } catch (IOException e) {
      addError(e);
      nontransformed.deleteOnExit(); // one more time
      transformed.deleteOnExit();    // one more time
      LOG.info(String.format("Files %s and %s have been permanently deleted!", this.nontransformed, this.transformed));
    }
  }

  private static void runJunit(List<Class<?>> listOfClasses, Classpath classpath, List<String> testPrefixes){
    //noinspection Convert2streamapi
    for(Class<?> eachClass : listOfClasses){ // run the test files
      if(matches(eachClass.getName(), testPrefixes)){
        Benchtop.junit(classpath, eachClass.getCanonicalName());
      }
    }
  }

  @Override public Environment bundleTarget(File directory) {
    this.target = Preconditions.checkNotNull(directory, "Target directory is null");
    if(!this.target.exists()){
      addError(new IOException("target directory does not exist"));
    }

    // replicates a directory tree (including content)
    // we do this to make Benchtop process repeatable and clean.
    // by doing we can always come back to the target directory
    // and collect all non-transformed and transformed classes.
    try {
      IO.copyDirectoryTree(this.target.toPath(),
        (this.nontransformed = Files.createTempDir()).toPath()
      );

      IO.copyDirectoryTree(this.target.toPath(),
        (this.transformed = Files.createTempDir()).toPath()
      );

    } catch (IOException e) {
      addError(e);
    }


    return this;
  }

  @Override public Environment bundleTimeout(int timeoutInSecs) {
    try {
      Preconditions.checkArgument(timeoutInSecs > 0, "invalid timeout");
      this.timeout = timeoutInSecs;
    } catch (Exception e){
      addError(e);
    }

    return this;
  }

  @Override public Environment bundleTransformations() {
    this.transformations = true;
    return this;
  }

  @Override public Environment bundleOutput(File directory) {
    try {
      this.output = Preconditions.checkNotNull(directory, "Output directory is null");

      if(directory.exists()){
        IO.deleteDirectory(directory.toPath());
      }

    } catch (Exception e) {
      addError(e);
    }

    return this;
  }

  @Override public Environment bundleClasspath(Classpath... paths) {
    // build classpath
    try {

      Preconditions.checkNotNull(this.target, "Target directory is null");
      Preconditions.checkNotNull(this.output, "Output directory is null");


      final List<File> relocatedFiles = IO.collectFiles(this.nontransformed, "class");

      final Classpath envClasspath = Classpath.environmentClasspath(
        Classpath.of(relocatedFiles),
        Classpath.of(IO.localCaches()),
        Classpath.of(this.output)
      );

      this.classpath.addAll(envClasspath);

      for( Classpath each : paths){
        if(null != each){
          this.classpath.addAll(each);
        }
      }

      // collect Randoop's classList
      this.classList.addAll(
        IO.resolveFullyQualifiedNames(this.nontransformed.toString(), relocatedFiles)
      );

    } catch (IOException e) {
      addError(e);
    }

    return this;
  }

  @Override public Environment bundleFocus(String... testPrefixes) {

    try {
      final List<String> prefixes = Lists.newArrayList(testPrefixes);
      if(prefixes.contains(null)) {
        addError(new IllegalArgumentException("Wrong test prefix"));
        return this;
      }

      if(prefixes.isEmpty()){
        prefixes.add(DEFAULT_PREFIX);
      }

      this.testPrefixes.addAll(prefixes);
    } catch (Exception e) {
      addError(e);
    }

    return this;
  }


  public void throwCachedErrors() throws BundleCreationError {
    if(!cachedErrors.isEmpty()){
      throw new BundleCreationError("Bundle creation error", cachedErrors);
    }
  }

  private static boolean matches(String className, List<String> prefixes){

    Preconditions.checkNotNull(prefixes);
    for (String each : prefixes){
      if(className.startsWith(each)) return true;
    }

    return false;
  }
}
