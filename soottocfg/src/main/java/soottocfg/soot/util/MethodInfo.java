/**
 * 
 */
package soottocfg.soot.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import soot.Local;
import soot.RefType;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.jimple.ParameterRef;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class MethodInfo {

	private final String sourceFileName;

	public static final String returnVariableName = "$ret_";
	public static final String exceptionVariableName = "$ex_";
	private static final String thisVariableName = "$this_";
	private static final String constructorOutVarName = "$co_";

	private final SootMethod sootMethod;
	private final Method cfgMethod;

	private CfgBlock source = null, sink = null;
	private Map<Unit, CfgBlock> unitToBlockMap = new HashMap<Unit, CfgBlock>();
	private Map<Local, Variable> localsMap = new HashMap<Local, Variable>();

	private Variable thisVariable;
	private Variable exceptionVariable;
	private List<Variable> returnVariables;

	private final Set<Variable> freshLocals = new LinkedHashSet<Variable>();

	private SourceLocation methodLoc = null;

	public MethodInfo(SootMethod sm, String sourceFileName) {
		sootMethod = sm;
		cfgMethod = SootTranslationHelpers.v().lookupOrCreateMethod(sootMethod);
		methodLoc = SootTranslationHelpers.v().getSourceLocation(sm);
		sink = new CfgBlock(getMethod());
		getMethod().setSink(sink);
		this.sourceFileName = sourceFileName;

		this.returnVariables = new LinkedList<Variable>();
		
		this.exceptionVariable = new Variable(exceptionVariableName, SootTranslationHelpers.v().getMemoryModel()
				.lookupType(RefType.v("java.lang.Throwable")));
		this.returnVariables.add(this.exceptionVariable); 
		

		if (!(sm.getReturnType() instanceof VoidType)) {
			// create a return variable if the method does not
			// return void.
			this.returnVariables.add(new Variable(returnVariableName,
					SootTranslationHelpers.v().getMemoryModel().lookupType(sm.getReturnType())));
		} 

		if (sm.isConstructor()) {
			int i=0;
			for (SootField sf : SootTranslationHelpers.findNonStaticFieldsRecursively(sm.getDeclaringClass())) {
				this.returnVariables.add(new Variable(constructorOutVarName+(++i), SootTranslationHelpers.v().getMemoryModel().lookupType(sf.getType())));
			}
		}
		
		// If the method is not static, create a this-variable which is
		// passed as the first parameter to the method.
		if (!sm.isStatic()) {
			this.thisVariable = new Variable(thisVariableName,
					SootTranslationHelpers.v().getMemoryModel().lookupType(sm.getDeclaringClass().getType()));
		}
	}

	public Method getMethod() {
		return cfgMethod;
	}

	/**
	 * Called after the method has been translated. Looks up the corresponding
	 * Method object in Program and fills in all the information. Should only be
	 * called once per method.
	 */
	public void finalizeAndAddToProgram() {
		Method m = cfgMethod;
		Collection<Variable> locals = new LinkedHashSet<Variable>();
		locals.addAll(this.localsMap.values());
		locals.addAll(this.freshLocals);
		m.initialize(this.thisVariable, this.returnVariables, locals, source, false);
		// System.out.println("Initialized method " + m);
		CfgBlock uniqueSink = m.findOrCreateUniqueSink();
		if (sink != uniqueSink) {
			System.err.println("Something strange with the CFG. More than one sink found for " + m.getMethodName());
		}
		if (m.inDegreeOf(sink) == 0) {
			System.err.println("Method " + sootMethod.getSignature() + " has no sink! Ignoring.");
			for (CfgEdge e : new HashSet<CfgEdge>(m.edgeSet())) {
				m.removeEdge(e);
			}
			for (CfgBlock b : new HashSet<CfgBlock>(m.vertexSet())) {
				if (!b.equals(sink) && !b.equals(source)) {
					m.removeVertex(b);
				}
			}
			m.addEdge(source, sink);
		}
		sink = uniqueSink;
	}

	public String getSourceFileName() {
		return this.sourceFileName;
	}

	public Variable getReturnVariable() {
		// TODO this is a hack that assumes that we only use that if there
		// is a single return variable.
		if (this.returnVariables.size() < 2) return null;
		return this.returnVariables.get(1);
	}

	public IdentifierExpression getExceptionVariable() {
		// TODO this is a hack that assumes that we only use that if there
		// is a single return variable.
		Verify.verify(this.returnVariables.size() >= 1);
		return new IdentifierExpression(methodLoc, this.returnVariables.get(0));
	}

	public Variable getOutVariable(int i) {
		Verify.verify(this.returnVariables.size() >= 2 && i>=0 && i<this.returnVariables.size(), "Index bound 0<="+i+"<"+this.returnVariables.size());
		return this.returnVariables.get(i);
	}

	public List<Variable> getOutVariables() {
		return this.returnVariables;
	}
	
	// public Expression getExceptionVariable() {
	// return new IdentifierExpression(this.exceptionalReturnVariable);
	// }

	public Expression getThisVariable() {
		return new IdentifierExpression(methodLoc, this.thisVariable);
	}

	public Expression lookupParameterRef(ParameterRef arg0) {
		int offset = thisVariable == null ? 0 : 1;
		// offset += Options.v().passCallerIdIntoMethods() ? 1 : 0;
		return new IdentifierExpression(methodLoc, cfgMethod.getInParam(arg0.getIndex() + offset));
	}

	public Variable lookupLocalVariable(Local arg0) {
		if (!localsMap.containsKey(arg0)) {
			localsMap.put(arg0, createLocalVariable(arg0));
		}
		return localsMap.get(arg0);
	}

	public Expression lookupLocal(Local arg0) {
		return new IdentifierExpression(methodLoc, lookupLocalVariable(arg0));
	}

	private Variable createLocalVariable(Local arg0) {
		return new Variable(arg0.getName(), SootTranslationHelpers.v().getMemoryModel().lookupType(arg0.getType()),
				false, false);
	}

	public Variable createFreshLocal(String prefix, Type t, boolean constant, boolean unique) {

		// List<Type> elementTypes = new LinkedList<Type>();
		// elementTypes.add(newType);
		// TupleType ttype = new TupleType(elementTypes);
		// TupleVariable tupleVar = new TupleVariable("$new", ttype, true,
		// true);
		//

		Variable v = new Variable(prefix + this.freshLocals.size(), t, constant, unique);
		this.freshLocals.add(v);
		return v;
	}

	public CfgBlock lookupCfgBlock(Unit u) {
		if (!unitToBlockMap.containsKey(u)) {
			unitToBlockMap.put(u, new CfgBlock(getMethod()));
		}
		return unitToBlockMap.get(u);
	}

	public CfgBlock findBlock(Unit u) {
		return unitToBlockMap.get(u);
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
