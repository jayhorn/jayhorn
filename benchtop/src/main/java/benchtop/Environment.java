package benchtop;

import java.io.File;

/**
 * @author Huascar Sanchez
 */
public interface Environment {

  /**
   * Caches an error.
   * @param cause thrown exception.
   */
  void addError(Throwable cause);

  /**
   * Installs a new Bundle.
   *
   * @param bundle bundle to be installed.
   */
  void install(ExecutionBundle bundle);

  /**
   * Auto resolves needed classpath to both generate tests
   * and execute tests. Client can provide additional classpaths
   * if needed.
   *
   * Note: Make sure that {@link #bundleTarget(File)} and {@link #bundleOutput(File)} methods
   * have been previously called, before calling this method.
   *
   * @param paths additional classpaths
   * @return self
   */
  Environment bundleClasspath(Classpath... paths);

  /**
   * Sets the tests Benchtop should focus on executing.
   *
   * If no prefixes are given, then the default focus is on
   * Regression tests.
   *
   * @param testPrefixes test prefixes to go after
   * @return self
   */
  Environment bundleFocus(String... testPrefixes);

  /**
   * Sets Benchtop operations' output directory.
   *
   * @param directory output directory.
   * @return self
   */
  Environment bundleOutput(File directory);

  /**
   * Sets the directory containing all compiled
   * classes of a Java project.
   *
   * @param directory classes directory.
   * @return self
   */
  Environment bundleTarget(File directory);

  /**
   * Sets an Randoop's execution timeout.
   *
   * @param timeoutInSecs timeout value in seconds.
   * @return self
   */
  Environment bundleTimeout(int timeoutInSecs);

  /**
   * Soot transformations are applied to project's classes. Then Randoop tests are
   * executed against the transformed classes.
   *
   * @return self
   */
  Environment bundleTransformations();
}
