/**
 * 
 */
package jayhorn.soot.util;

import java.util.HashMap;
import java.util.Map;

import jayhorn.cfg.expression.Expression;
import jayhorn.cfg.method.CfgBlock;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.ParameterRef;

/**
 * @author schaef
 *
 */
public class MethodInfo {
	
	private CfgBlock source = null, sink = null;
	private Map<Unit, CfgBlock> unitToBlockMap = new HashMap<Unit, CfgBlock>();
	
	public MethodInfo(SootMethod sm) {
		sink = new CfgBlock();
	}

	public Expression getReturnVariable() {
		return null;
	}
	
	public Expression getExceptionVariable() {
		return null;
	}
	
	public Expression lookupParameterRef(ParameterRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression lookupLocal(Local arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public CfgBlock lookupCfgBlock(Unit u) {
		if (!unitToBlockMap.containsKey(u)) {
			unitToBlockMap.put(u, new CfgBlock());
		}
		return unitToBlockMap.get(u);
	}
	
	public CfgBlock findBlock(Unit u) {
		if (!unitToBlockMap.containsKey(u)) {
			return unitToBlockMap.get(u);
		}
		return null;
	}
	
	public CfgBlock getSource() {
		return this.source;
	}
	
	public void setSource(CfgBlock source) {
		this.source = source;
	}
	
	public CfgBlock getSink() {
		return sink;
	}
	
}
