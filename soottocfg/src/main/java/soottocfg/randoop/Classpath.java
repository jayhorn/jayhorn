package soottocfg.randoop;

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
  private final List<File> elements = new ArrayList<>();

  public static Classpath of(File... files) {
    return of(Arrays.asList(files));
  }

  public static Classpath of(Collection<File> files) {
    Classpath result = new Classpath();
    result.addAll(files);
    return result;
  }

  public void addAll(File... elements) {
    addAll(Arrays.asList(elements));
  }

  public void addAll(Collection<File> elements) {
    this.elements.addAll(elements);
  }

  public void addAll(Classpath anotherClasspath) {
    this.elements.addAll(anotherClasspath.elements);
  }

  public Collection<File> getElements() {
    return elements;
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public boolean contains(File file) {
    return elements.contains(file);
  }

  @Override public String toString() {
    return Strings.joinCollection(":", getElements());
  }
}
