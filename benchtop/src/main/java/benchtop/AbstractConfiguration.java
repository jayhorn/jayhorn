package benchtop;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author Huascar Sanchez
 */
public abstract class AbstractConfiguration implements Configuration {
  protected Command.Builder builder;

  /**
   * Configures some internal command builder. Clients of this API could
   * chain multiple configurations added by subclasses of this class.
   */
  protected abstract void configure();

  @Override public final synchronized void configure(Command.Builder builder) {
    Tracker.reset();
    try {
      if (!Objects.isNull(this.builder)) {
        throw new IllegalStateException("Re-entry is not allowed.");
      }

      if(Objects.isNull(builder)){
        throw new IllegalArgumentException("configure() was given a null builder.");
      }

      this.builder = builder;

      configure();

    } finally {
      this.builder = null;
    }
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

  // keep track of used options
  private static String trackOption(String option){
    final String nonNullOption = Objects.requireNonNull(option);
    if(Tracker.monitor().contains(nonNullOption)) {
      throw new IllegalArgumentException(
        "Option " + nonNullOption + " already been set"
      );
    }

    Tracker.monitor().add(Objects.requireNonNull(option));
    return option;
  }

  protected void arguments(Object... args){

    final List<Object> asList = Arrays.asList(args);

    asList.stream()
      .filter(a -> a.toString().startsWith("--")).map(s -> s.toString().substring(0, s.toString().lastIndexOf("=")))
      .forEach(n -> trackOption((String)n));

    this.builder.arguments(asList);
  }

  protected void environment(String key, String value){
    final String nonNullKey   = Objects.requireNonNull(key);
    final String nonNullValue = Objects.requireNonNull(value);
    this.builder.environment(nonNullKey, nonNullValue);
  }

  protected void maxCommandLength(int maxLength){
    this.builder.maxCommandLength(maxLength);
  }

  protected void workingDirectory(File localWorkingDirectory){
    this.builder.workingDirectory(localWorkingDirectory);
  }

  protected void permitNonZeroExitStatus(){
    builder().permitNonZeroExitStatus();
  }

  protected Command.Builder builder(){
    return this.builder;
  }

  private static class Tracker {
    static Set<String> optionMonitor = new LinkedHashSet<>();

    static void reset(){
      optionMonitor.clear();
    }

    static Set<String> monitor(){
      return optionMonitor;
    }
  }

}
