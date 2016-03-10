package benchtop.spi;

import benchtop.Classpath;
import benchtop.utils.Strings;
import com.google.common.base.Preconditions;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;

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
      Preconditions.checkNotNull(classpath)
    );
  }

  /**
   * Creates a new Java configuration.
   *
   * @param classpath Java program's classpath
   * @param clazz main class
   * @param args arguments of main class
   * @return a new Java configuration object.
   */
  public static JavaConfiguration newJavaConfiguration(Classpath classpath, final String clazz, final String... args){
    return new JavaConfiguration(classpath) {
      @Override protected void execute() {

        final List<String> argsList = Arrays.asList(Preconditions.checkNotNull(args));

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

  /**
   * Sets the Java program's main class.
   *
   * @param clazz the main class.
   */
  public void mainClass(String clazz){
    arguments(Preconditions.checkNotNull(clazz));
  }

  /**
   * Sets the arguments of the main class.
   *
   * @param args array of arguments.
   */
  public void mainArgs(List<String> args){

    final Object[] objects = Strings.generateArrayOfObjects(
      Preconditions.checkNotNull(args).toArray(new String[args.size()])
    );

    arguments(objects);
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
