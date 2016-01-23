package soottocfg.randoop;

/**
 * @author Huascar Sanchez
 */
public interface ExecutionLog {
  void info(String s);
  void error(String s, Throwable exception);
  void warn(String s);
}
