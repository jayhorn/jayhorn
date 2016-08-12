/**
 * 
 */
package jayhorn.old_inconsistency_check;

import com.google.common.base.Preconditions;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;

/**
 * @author schaef
 *
 */
public class Inconsistency {
	
	private final Method method;
	private final CfgBlock inconsistentRoot;
	
	/**
	 * Constructor that takes a method m and a block b s.t., the block
	 * b is inconsistent in m and b is not dominated by another inconsistent
	 * block.
	 * @param m
	 * @param b
	 */
	public Inconsistency(Method m, CfgBlock b) {
		Preconditions.checkArgument(m.vertexSet().contains(b));
		method = m;
		inconsistentRoot = b;
	}

	/**
	 * Returns the method
	 * @return
	 */
	public Method getMethod() {
		return method;
	}
	
	/**
	 * Returns the root of the inconsistency. I.e., a block that
	 * is inconsistent and not dominated by any other inconsistent
	 * block.
	 * @return
	 */
	public CfgBlock getRootOfInconsistency() {
		return inconsistentRoot;
	}
}
