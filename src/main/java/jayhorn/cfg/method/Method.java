/**
 * 
 */
package jayhorn.cfg.method;

import jayhorn.cfg.Node;
import soot.SootMethod;

/**
 * @author schaef
 *
 */
public class Method implements Node {

	private final String methodName;

	public Method(SootMethod m) {
		methodName = m.getDeclaringClass().getName() + "." + m.getName();
	}

	public String getMethodName() {
		return this.methodName;
	}
}
