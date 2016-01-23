package soottocfg.randoop;

import java.io.PrintStream;

/**
 * @author Huascar Sanchez
 */
public class BasicExecutionLog implements ExecutionLog {
  private PrintStream out;

  /**
   * Basic ExecutionLog object.
   * @param out the current print stream
   */
  public BasicExecutionLog(PrintStream out){
    this.out        = out;
  }

  @Override public void info(String s) {
    out.println("INFO: " + s);
  }

  @Override public void error(String s, Throwable throwable) {
    out.println("ERROR: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override public void warn(String s) {
    out.println("WARN: " + s);
  }
}
