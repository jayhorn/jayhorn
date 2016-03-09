package benchtop.spi;

import benchtop.Classpath;
import benchtop.utils.Strings;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author Huascar Sanchez
 */
public abstract class JavacConfiguration extends AbstractConfiguration {

  private final Classpath classpath;
  private final File      destination;

  /**
   * Constructs a JavacConfiguration for a given classpath and destination directory.
   *
   * @param classpath the current classpath
   * @param destination the directory containing compiled Java files.
   */
  public JavacConfiguration(Classpath classpath, File destination){
    this.classpath    = Objects.requireNonNull(classpath);
    this.destination  = Objects.requireNonNull(destination);
  }

  /**
   * Creates a new Javac configuration.
   *
   * @param classpath Java program's needed classpath.
   * @param destination Java program's compiling destination
   * @param sourceFiles files to compile.
   * @return a new Javac configuration.
   */
  public static JavacConfiguration newJavacConfiguration(Classpath classpath, File destination, final File... sourceFiles){
    return new JavacConfiguration(classpath, destination) {
      @Override protected void javac() {
        compile(sourceFiles);
      }
    };
  }

  @Override protected void configure() {
    tool();
    debug();
    classpath(classpath);
    destination(destination);
    javac();
  }

  /**
   * Configures which files to compile. options' ORDER really matters.
   */
  protected abstract void javac();

  private void debug() {
    arguments("-g");
  }

  private void destination(File directory) {
    arguments("-d", directory.toString());
  }

  private void tool(){
    arguments("javac");
  }

  private void bootClasspath(Classpath classpath) {
    arguments("-bootclasspath", classpath.toString());
  }

  private void sourcepath(File... path) {
    arguments("-sourcepath", Classpath.of(path).toString());
  }


  private void classpath(Classpath classpath) {
    arguments("-classpath", classpath.toString());
  }

  private void compile(Collection<File> files) {
    if(files.contains(null)) {
      throw new IllegalArgumentException("null values in collection");
    }

    arguments((Object[]) Strings.generateArrayOfStrings(files));
  }

  protected void compile(File... files) {
    compile(Arrays.asList(files));
  }
}
