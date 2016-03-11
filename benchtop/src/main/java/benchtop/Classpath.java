package benchtop;

import benchtop.utils.Strings;
import com.google.common.base.Preconditions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A list of jar files and directories needed by randoop.
 *
 * @author Huascar Sanchez
 */
public class Classpath {
  private final List<File> elements;

  /**
   * Constructs a new Classpath object.
   */
  public Classpath(){
    this.elements = new ArrayList<>();
  }

  /**
   * @return an empty classpath.
   */
  public static Classpath empty(){
    return new Classpath();
  }

  /**
   * @return Benchtop's current classpath.
   */
  public static Classpath environmentClasspath(Classpath... otherPaths){
    Classpath env = Classpath.of(System.getProperty("java.class.path").split(":"));

    for( Classpath each : otherPaths){
      if(null != each){
        env = union(env, each);
      }
    }

    return env;
  }

  /**
   * Creates a new classpath by combining two classpaths.
   *
   * @param a first classpath
   * @param b second classpath
   * @return the result of two classpaths.
   */
  public static Classpath union(Classpath a, Classpath b){
    final Classpath nonNullA = Preconditions.checkNotNull(a);
    final Classpath nonNullB = Preconditions.checkNotNull(b);

    final Classpath newCp = Classpath.empty();
    newCp.addAll(nonNullA);
    newCp.addAll(nonNullB);

    return newCp;
  }

  /**
   * Creates a new classpath from an array of file paths.
   *
   * @param files array of file paths.
   * @return a non-empty classpath.
   */
  public static Classpath of(String... files){
    final List<File> content = new ArrayList<>();
    for(String eachPath : files){
      if(null == eachPath || eachPath.isEmpty()) return empty();

      final String massagedPath = eachPath.contains(" ") ? ("\"" + eachPath +  "\"") : eachPath;

      final File fileObj = new File(massagedPath);

      if(fileObj.exists()){
        content.add(fileObj);
      }

    }

    return of(content);
  }

  /**
   * Creates a new classpath from an array of files.
   *
   * @param files the array of files to fill the classpath.
   * @return a non-empty classpath.
   */
  public static Classpath of(File... files) {
    return of(Arrays.asList(files));
  }

  /**
   * Creates a new classpath from a collection of files.
   *
   * @param files the collection of files to fill the classpath.
   * @return a non-empty classpath.
   */
  public static Classpath of(Collection<File> files) {

    final Collection<File> nonNullCollection = Preconditions.checkNotNull(files);
    if(nonNullCollection.contains(null)) {
      throw new IllegalArgumentException("Collection contains null elements");
    }

    Classpath result = new Classpath();
    result.addAll(nonNullCollection);
    return result;
  }

  /**
   * Adds the entire array of elements to the classpath.
   *
   * @param elements array of elements to add.
   */
  public void addAll(File... elements) {
    addAll(Arrays.asList(elements));
  }

  /**
   * Adds an entire collection of elements to the classpath.
   *
   * @param elements the collection of elements.
   */
  public void addAll(Collection<File> elements) {
    this.elements.addAll(elements);
  }

  /**
   * Adds a secondary classpath to this classpath.
   *
   * @param anotherClasspath the secondary classpath.
   */
  public void addAll(Classpath anotherClasspath) {
    this.elements.addAll(anotherClasspath.elements);
  }

  /**
   * @return the collection of files stored in this classpath.
   */
  public Collection<File> getElements() {
    return elements;
  }

  /**
   * @return true if the classpath is empty; false otherwise.
   */
  public boolean isEmpty() {
    return elements.isEmpty();
  }

  /**
   * Checks if a file is in the classpath.
   *
   * @param file the file to check
   * @return true if the file is in the classpath; false otherwise.
   */
  public boolean contains(File file) {
    return elements.contains(file);
  }

  @Override public String toString() {
    return Strings.joinCollection(":", getElements());
  }
}
