/**
 * 
 */
package jayhorn.checker;

import jayhorn.cfg.Program;
import jayhorn.cfg.method.Method;
import jayhorn.util.Log;

/**
 * @author schaef
 *
 */
public class Checker {

	public boolean checkProgram(Program program) {
		Log.info("Starting verification for "+program.getEntryPoints().length + " entry points.");
		for (Method method : program.getEntryPoints()) {
			checkEntryPoint(method); //TODO give this one a return value.
		}
		
		return true;
	}
	
	private void checkEntryPoint(Method method) {
		Log.info("\tVerification from entry " + method.getMethodName());
	}
}
