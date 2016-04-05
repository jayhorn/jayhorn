/**
 * 
 */
package dynamic_tests;

/**
 * @author schaef
 *
 */
public class DynamicTest02 {
	
    private void log(String s, int i) {}
    
    public String callEval01() {
    	return callEvalWithArgs("www.google.com", 80);
    }

    public String callEval02() {
     	return callEvalWithArgs(null, 10);
    }

    public String callEval03() {
    	return callEvalWithArgs("foo", 65537);
    }

    private String callEvalWithArgs(String server, int port) {
      String s = "";
      try {
        s += eval(server, port);
      } catch (Throwable e) {
        s += e;
      }
      return s;
    }


    /**
     * Taken from org.apache.tools.ant.taskdefs.condition.Socket
     * because it caused an exception in:
     * Body of method <dynamic_tests.DynamicTest02: boolean eval()> contains a caught exception reference, but not a corresponding trap using this statement as handler
		at soot.jimple.validation.JimpleTrapValidator.validate(JimpleTrapValidator.java:73)
		at soot.jimple.JimpleBody.validate(JimpleBody.java:125)
		at soot.jimple.JimpleBody.validate(JimpleBody.java:110)
		at soottocfg.soot.transformers.ExceptionTransformer.internalTransform(ExceptionTransformer.java:189)
		
		The exception is triggered because the exception remover 
		misses some CaughtExceptionRef in the bytecode if soot inserts extra
		bytecode of the form:
		
        r21 = new java.lang.NullPointerException;
        specialinvoke r21.<java.lang.NullPointerException: void <init>(java.lang.String)>("This statement would have triggered an Exception: virtualinvoke s#1.<java.net.Socket: void close()>()");
        throw r21;
        $r18 := @caughtexception;		
     */
     protected boolean eval(String server, int port) throws RuntimeException {
        if (server == null) {
            throw new RuntimeException("No server specified in socket "
                                     + "condition");
        }
        if (port == 0) {
            throw new RuntimeException("No port specified in socket condition");
        }
        log("Checking for listener at " + server + ":" + port,
            42);
//        Socket s = null;
//        try {
//            s = new Socket(server, port);
//        } catch (IOException e) {
//            return false;
//        } finally {
//          if (s != null) {
//            try {
//              s.close();
//            } catch (IOException ioe) {
//              // Intentionally left blank
//            }
//          }
//        }
        return true;
    }

	
}
