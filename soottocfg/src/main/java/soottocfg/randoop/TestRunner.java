package soottocfg.randoop;

/**
 * @author Huascar Sanchez
 */
public interface TestRunner {
  /**
   * Checks if we are dealing with a JUnit4 test class.
   *
   * @param klass the test class
   * @return true if it is a test class; false otherwise.
   */
  boolean isClassSupported(Class<?> klass);

  /**
   * Runs a test using an array of arguments.
   *
   * @param args arguments needed for execution
   * @return true if it ran; false otherwise.
   */
  boolean run(String... args);
}
