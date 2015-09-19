/**
 * 
 */
package jayhorn.soot.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jayhorn.cfg.Variable;
import jayhorn.cfg.expression.Expression;
import jayhorn.cfg.expression.IdentifierExpression;
import jayhorn.cfg.method.CfgBlock;
import jayhorn.soot.SootTranslationHelpers;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.jimple.ParameterRef;

/**
 * @author schaef
 *
 */
public class MethodInfo {

	private final String sourceFileName;

	private static final String parameterPrefix = "$in_";
	private static final String returnVariableName = "$ret_";
	private static final String exceptionVariableName = "$exc_";
	private static final String thisVariableName = "$this_";

	private CfgBlock source = null, sink = null;
	private Map<Unit, CfgBlock> unitToBlockMap = new HashMap<Unit, CfgBlock>();
	private Map<Local, Variable> localsMap = new HashMap<Local, Variable>();
	private List<Variable> parameterList = new LinkedList<Variable>();

	private Variable returnVariable, exceptionalReturnVariable, thisVariable;

	public MethodInfo(SootMethod sm, String sourceFileName) {
		sink = new CfgBlock();
		this.sourceFileName = sourceFileName;

		// create a return variable if the method does not
		// return void.
		if (!(sm.getReturnType() instanceof VoidType)) {
			this.returnVariable = new Variable(returnVariableName,
					SootTranslationHelpers.v()
							.lookupType(sm.getReturnType()));
		} else {
			this.returnVariable = null;
		}

		// create a return variable for exceptional returns.
		this.exceptionalReturnVariable = new Variable(exceptionVariableName,
				SootTranslationHelpers.v()
						.lookupType(
								Scene.v().getSootClass("java.lang.Throwable")
										.getType()));

		// If the method is not static, create a this-variable which is
		// passed as the first parameter to the method.
		if (!sm.isStatic()) {
			this.thisVariable = new Variable(thisVariableName,
					SootTranslationHelpers.v().lookupType(
							sm.getDeclaringClass().getType()));
		}

		for (int i = 0; i < sm.getParameterCount(); i++) {
			String param_name = parameterPrefix + i;
			parameterList.add(new Variable(param_name, SootTranslationHelpers
					.v().lookupType(sm.getParameterType(i))));

			// assumeParameterType(id, sm.getParameterType(i));

		}
	}

	public String getSourceFileName() {
		return this.sourceFileName;
	}

	public Expression getReturnVariable() {
		return new IdentifierExpression(this.returnVariable);
	}

	public Expression getExceptionVariable() {
		return new IdentifierExpression(this.exceptionalReturnVariable);
	}

	public Expression getThisVariable() {
		return new IdentifierExpression(this.thisVariable);
	}	
	
	public Expression lookupParameterRef(ParameterRef arg0) {
		return new IdentifierExpression(parameterList.get(arg0.getIndex()));
	}

	public Expression lookupLocal(Local arg0) {
		if (!localsMap.containsKey(arg0)) {
			localsMap.put(arg0, createLocalVariable(arg0));
		}
		return new IdentifierExpression(localsMap.get(arg0));
	}

	private Variable createLocalVariable(Local arg0) {
		// TODO
		return new Variable(arg0.getName(), SootTranslationHelpers.v()
				.lookupType(arg0.getType()));
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
