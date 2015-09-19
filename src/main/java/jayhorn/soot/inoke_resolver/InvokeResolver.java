/**
 * 
 */
package jayhorn.soot.inoke_resolver;

import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.shimple.ShimpleBody;

/**
 * @author schaef
 *
 */
public abstract class InvokeResolver {

	/**
	 * 
	 */
	public InvokeResolver() {
		// TODO Auto-generated constructor stub
	}

	public abstract List<SootMethod> resolveVirtualCall(ShimpleBody body, Unit u, InstanceInvokeExpr call);
	
}
