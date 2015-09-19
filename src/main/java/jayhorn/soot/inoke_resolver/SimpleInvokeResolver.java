/**
 * 
 */
package jayhorn.soot.inoke_resolver;

import java.util.LinkedList;
import java.util.List;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.shimple.ShimpleBody;

/**
 * @author schaef
 * Does not resolve virtual calls but just returns the virtual method.
 * This should only be used when abstracting function calls without actually
 * caring about what they do (e.g., inconsistent code detection).
 */
public class SimpleInvokeResolver extends InvokeResolver {

	/* (non-Javadoc)
	 * @see jayhorn.soot.inoke_translation.InvokeTranslation#resolveVirtualCall(soot.jimple.InstanceInvokeExpr)
	 */
	@Override
	public List<SootMethod> resolveVirtualCall(ShimpleBody body, Unit u, InstanceInvokeExpr call) {
		List<SootMethod> res = new LinkedList<SootMethod>();
		res.add(call.getMethod());
		return res;
	}

}
