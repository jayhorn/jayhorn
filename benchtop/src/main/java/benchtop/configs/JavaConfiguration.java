package benchtop.configs;

import benchtop.AbstractConfiguration;
import benchtop.Classpath;
import java.util.Arrays;

import java.util.List;
import java.util.Objects;

/**
 * @author Huascar Sanchez
 */
public abstract class JavaConfiguration extends AbstractConfiguration {

  private final Classpath classpath;

  /**
   * Constructs a JavaConfiguration for a given classpath.
   *
   * @param classpath the current classpath
   */
  public JavaConfiguration(Classpath classpath){
    this.classpath    = Classpath.union(
      Classpath.environmentClasspath(),
      Objects.requireNonNull(classpath)
    );
  }

  /**
   * Creates a new Java configuration.
   */
  public static JavaConfiguration newJavaConfiguration(Classpath classpath, String clazz, final String... args){
    return new JavaConfiguration(classpath) {
      @Override protected void execute() {

        final List<String> argsList = Arrays.asList(Objects.requireNonNull(args));

        mainClass(clazz);
        mainArgs(argsList);
      }
    };
  }

  @Override protected void configure() {
    tool();
    classpath(classpath);
    execute();
  }

  public void mainClass(String clazz){
    arguments(Objects.requireNonNull(clazz));
  }

  public void mainArgs(List<String> args){
    arguments(Objects.requireNonNull(args).toArray(new String[args.size()]));
  }

  private void classpath(Classpath classpath) {
    arguments("-classpath", classpath.toString());
  }

  private void tool(){
    arguments("java");
  }

  /**
   * Executes a given class using some input
   */
  protected abstract void execute();


}
