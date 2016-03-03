package benchtop;

/**
 * @author Huascar Sanchez
 */
public interface ExecutionLog {
  /**
   * Logs some important information
   *
   * @param s the important information
   */
  void info(String s);

  /**
   * Logs an error caused by some throwable.
   *
   * @param s the error label
   * @param exception the actual error
   */
  void error(String s, Throwable exception);

  /**
   * Logs a warning message
   *
   * @param s the warning message
   */
  void warn(String s);
}
