/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.options.SparkOptions;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class NewMemoryModel extends BasicMemoryModel {

	private Map<Variable, Map<String, Variable>> fieldToLocalMap = new HashMap<Variable, Map<String, Variable>>();

	// This one sticks around between method calls...
	private Map<Variable, SootField> localToFieldMap = new HashMap<Variable, SootField>();

	private static Variable staticFieldContainerVariable;
	private static List<SootField> usedStaticFields;
	private static final String globalsClassName = "JayhornGlobals";
	private static final String globalsClassVarName = "JayhornGlobalsClassVar";
	
	protected static void resetGlobalState() {
		NewMemoryModel.staticFieldContainerVariable = null;
		NewMemoryModel.usedStaticFields = null;		
	}
	
	public NewMemoryModel() {
		NewMemoryModel.resetGlobalState();
		
		// load points to analysis
		setGeomPointsToAnalysis();
	}

	public void clearFieldToLocalMap() {
		// TODO: this is only here because we know it's only called once per
		// method:
		fieldToLocalMap.clear();
	}
	
	private boolean pullAt(Unit u, FieldRef fr) {
		SootMethod m = SootTranslationHelpers.v().getCurrentMethod();
		
		if (fr instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fr;
			
			// in constructor never pull 'this'
			if (m.isConstructor() && ifr.getBase().equals(m.getActiveBody().getThisLocal()))
				return false;
		} else if (fr instanceof StaticFieldRef) {	
			
			// in static initializer never pull static field
			if (m.isStaticInitializer())
				return false;
		}
		return true;
	}
	
	private boolean pushAt(Unit u, FieldRef fr) {
		SootMethod m = SootTranslationHelpers.v().getCurrentMethod();
		UnitGraph graph = new CompleteUnitGraph(m.getActiveBody());
		
		if (fr instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fr;
			
			// in constructor only push 'this' after the last access
			if (m.isConstructor() && ifr.getBase().equals(m.getActiveBody().getThisLocal())) {
				
				//check if there is any path from 'u' to the tail(s) that does not contain an access to 'this'
				List<Unit> tails = graph.getTails();
				List<Unit> todo = new LinkedList<Unit>();
				todo.add(u);
				boolean foundPathWithoutAccess = false;
				while (!todo.isEmpty()) {
					Unit unit = todo.remove(0);
					
					// if it contains another access to 'this', stop exploring this path
					Stmt s = (Stmt) u;
					if (s.containsFieldRef()) {
						FieldRef fr2 = s.getFieldRef();
						if (fr2 instanceof InstanceFieldRef) {
							InstanceFieldRef ifr2 = (InstanceFieldRef) fr2;
							if (ifr2.getBase().equals(m.getActiveBody().getThisLocal()) && !ifr2.equals(ifr)) {
								continue;
							}
						}
					}
					
					if (tails.contains(unit))
						foundPathWithoutAccess = true; // at the end of this path
					else
						todo.addAll(graph.getSuccsOf(unit));
				}
				return foundPathWithoutAccess;
			}
		} else if (fr instanceof StaticFieldRef) {
			// in static initializer only push at the end
			if (m.isStaticInitializer()) {
				
				//check if there is any path from 'u' to the tail(s) that does not contain an access to 'this'
				List<Unit> tails = graph.getTails();
				List<Unit> todo = new LinkedList<Unit>();
				todo.add(u);
				boolean foundPathWithoutAccess = false;
				while (!todo.isEmpty()) {
					Unit unit = todo.remove(0);
					
					// if it contains another access to a static field, stop exploring this path
					Stmt s = (Stmt) u;
					if (s.containsFieldRef()) {
						FieldRef fr2 = s.getFieldRef();
						if (fr2 instanceof StaticFieldRef && !fr2.equals(fr))
								continue;
					}
					
					if (tails.contains(unit))
						foundPathWithoutAccess = true; // at the end of this path
					else
						todo.addAll(graph.getSuccsOf(unit));
				}
				return foundPathWithoutAccess;
			}
		}	
		return true;
	}

	@Override
	public void mkHeapWriteStatement(Unit u, FieldRef fieldRef, Value rhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);

		rhs.apply(valueSwitch);
		Expression value = valueSwitch.popExpression();

		ClassVariable classVar;
		IdentifierExpression base;
		List<Variable> fieldLocals = new LinkedList<Variable>();

		if (fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fieldRef;
			ifr.getBase().apply(valueSwitch);
			base = (IdentifierExpression) valueSwitch.popExpression();
			classVar = lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(fieldRef.getField().getDeclaringClass().getType()));

			for (SootField sf : fieldRef.getField().getDeclaringClass().getFields()) {
				if (!sf.isStatic()) {
					fieldLocals.add(lookupFieldLocal(base.getVariable(), sf));
				}
			}
		} else if (fieldRef instanceof StaticFieldRef) {
			/*
			 * For static fields, we use the artificial global class
			 * that holds all static fields as base.
			 */
			Variable glob = getStaticFieldContainerVariable();
			classVar = ((ReferenceType) glob.getType()).getClassVariable();
			base = new IdentifierExpression(loc, glob);

			for (SootField sf : usedStaticFields) {
				fieldLocals.add(lookupFieldLocal(glob, sf));
			}

		} else {
			throw new RuntimeException("not implemented");
		}

		Variable fieldVar = lookupFieldLocal(fieldRef); // TODO

		Verify.verify(fieldLocals.contains(fieldVar), fieldLocals.toString() + "\ndoesn't contain " + fieldVar);

		// Variable[] vars = classVar.getAssociatedFields();
		Variable[] vars = fieldLocals.toArray(new Variable[fieldLocals.size()]);
		// ------------- pull ---------------
		if (pullAt(u, fieldRef)) {
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PullStatement(loc, classVar, base, unpackedVars));
		}
		// ------------------------------------
		this.statementSwitch.push(new AssignStatement(loc,
				new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar), value));
		// ------------- push -----------------
		if (pushAt(u, fieldRef)) {
			List<Expression> packedVars = new LinkedList<Expression>();
			for (int i = 0; i < vars.length; i++) {
				packedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PushStatement(loc, classVar, base, packedVars));
		}
		// ------------------------------------

	}

	@Override
	public void mkHeapReadStatement(Unit u, FieldRef fieldRef, Value lhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		// Variable fieldVar = lookupField(fieldRef.getField());

		lhs.apply(valueSwitch);
		IdentifierExpression left = (IdentifierExpression) valueSwitch.popExpression();

		ClassVariable classVar;
		IdentifierExpression base;
		List<Variable> fieldLocals = new LinkedList<Variable>();
		if (fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fieldRef;
			ifr.getBase().apply(valueSwitch);
			base = (IdentifierExpression) valueSwitch.popExpression();
			classVar = lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(fieldRef.getField().getDeclaringClass().getType()));
			for (SootField sf : fieldRef.getField().getDeclaringClass().getFields()) {
				if (!sf.isStatic()) {
					fieldLocals.add(lookupFieldLocal(base.getVariable(), sf));
				}
			}
		} else if (fieldRef instanceof StaticFieldRef) {
			/*
			 * For static fields, we use the artificial global class
			 * that holds all static fields as base.
			 */
			Variable glob = getStaticFieldContainerVariable();
			classVar = ((ReferenceType) glob.getType()).getClassVariable();
			base = new IdentifierExpression(loc, glob);
			for (SootField sf : usedStaticFields) {
				fieldLocals.add(lookupFieldLocal(glob, sf));
			}

		} else {
			throw new RuntimeException("not implemented");
		}

		Variable fieldVar = lookupFieldLocal(fieldRef); // TODO
		Verify.verify(fieldLocals.contains(fieldVar));

		// Variable[] vars = classVar.getAssociatedFields();
		Variable[] vars = fieldLocals.toArray(new Variable[fieldLocals.size()]);

		// ------------- pull ---------------
		if (pullAt(u, fieldRef)) {
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PullStatement(loc, classVar, base, unpackedVars));
		}
		// ------------------------------------
		this.statementSwitch.push(new AssignStatement(loc, left,
				new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar)));
		// ------------- push -----------------
		if (pushAt(u, fieldRef)) {
			List<Expression> packedVars = new LinkedList<Expression>();
			for (int i = 0; i < vars.length; i++) {
				packedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PushStatement(loc, classVar, base, packedVars));
		}
		// ------------------------------------
	}

	protected Variable getStaticFieldContainerVariable() {
		if (staticFieldContainerVariable == null) {
			usedStaticFields = new LinkedList<SootField>();
			ClassVariable classVar = new ClassVariable(globalsClassVarName, new LinkedList<ClassVariable>());
			Set<Variable> fields = new LinkedHashSet<Variable>();
			for (SootClass sc : new LinkedList<SootClass>(Scene.v().getClasses())) {
				if (sc.resolvingLevel() >= SootClass.BODIES) {
					for (SootMethod sm : sc.getMethods()) {
						try {
							for (Unit u : sm.retrieveActiveBody().getUnits()) {
								Stmt st = (Stmt) u;
								if (st.containsFieldRef()) {
									SootField sf = st.getFieldRef().getField();
									if (sf.isStatic() && !usedStaticFields.contains(sf)) {
										fields.add(this.lookupField(sf));
										usedStaticFields.add(sf);
									}
								}
							}
						} catch (Exception e) {

						}
					}
				}
			}
			List<Variable> fieldList = new LinkedList<Variable>();
			fieldList.addAll(fields);
			classVar.setAssociatedFields(fieldList);
			SootTranslationHelpers.v().getProgram().addClassVariable(classVar);
			staticFieldContainerVariable = new Variable(globalsClassName, new ReferenceType(classVar), true, true);
		}
		return staticFieldContainerVariable;
	}

	/**
	 * If e is an identifier expression for a var of
	 * ReferenceType, return the classvariable otherwise
	 * throw an exception.
	 * 
	 * @param e
	 * @return
	 */
	// private ClassVariable getClassVarFromExpression(Expression e) {
	// return getClassVariableFromVar(getVarFromExpression(e));
	// }

	private Variable getVarFromExpression(Expression e) {
		IdentifierExpression base = (IdentifierExpression) e;
		return base.getVariable();
	}

	// private ClassVariable getClassVariableFromVar(Variable v) {
	// ReferenceType baseType =(ReferenceType)v.getType();
	// return baseType.getClassVariable();
	// }

	@Override
	public void mkConstructorCall(Unit u, SootMethod constructor, List<Expression> args) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Method method = SootTranslationHelpers.v().lookupOrCreateMethod(constructor);

		List<Expression> receiver = new LinkedList<Expression>();
		for (SootField sf : constructor.getDeclaringClass().getFields()) {
			if (sf.isFinal()) {
				Variable v = lookupFieldLocal(getVarFromExpression(args.get(0)), sf);
				receiver.add(new IdentifierExpression(loc, v));
			}
		}
		Verify.verify(method.getReturnType().size() == receiver.size(),
				method.getMethodName() + " -> " + method.getReturnType().size() + "!=" + receiver.size());

		// MethodInfo currentMethodInfo = this.statementSwitch.getMethodInfo();
		// for (soottocfg.cfg.type.Type tp : method.getReturnType()) {
		// Variable lhs = currentMethodInfo.createFreshLocal("foo", tp, true,
		// false);
		// receiver.add(new IdentifierExpression(loc, lhs));
		// }
		// TODO: keep a map between the locals and the fields of this class.
		CallStatement stmt = new CallStatement(loc, method, args, receiver);
		this.statementSwitch.push(stmt);
	}
	
	@Override
	public void mkCopy(Local lhs, Local rhs) {
//		System.out.println("Copying " + rhs + " into " + lhs);
		lhs.apply(valueSwitch);
		IdentifierExpression base = (IdentifierExpression) valueSwitch.popExpression();
		for (Map.Entry<Variable,Map<String,Variable>> e : fieldToLocalMap.entrySet()) {
			if (e.getKey().getName().equals(rhs.getName())) {
				fieldToLocalMap.put(base.getVariable(), e.getValue());
				return;
			}
		}
	}

	private Variable lookupFieldLocal(Variable baseVar, SootField sf) {
		if (!fieldToLocalMap.containsKey(baseVar)) {
			fieldToLocalMap.put(baseVar, new HashMap<String, Variable>());
		}
		Map<String, Variable> f2l = fieldToLocalMap.get(baseVar);
		if (!f2l.containsKey(sf.getDeclaration())) {
			MethodInfo currentMethodInfo = this.statementSwitch.getMethodInfo();
			soottocfg.cfg.type.Type tp = this.lookupType(sf.getType());
			String name = baseVar.getName() + "_" + sf.getName() + "_" + sf.getNumber();
			if (sf.isStatic()) {
				name = sf.getDeclaringClass().getName() + "_" + sf.getName();
			}
			Variable l = currentMethodInfo.createFreshLocal(name, tp, sf.isFinal(), false);
			f2l.put(sf.getDeclaration(), l);
			
			// also add it to the localToFieldMap
			localToFieldMap.put(l, sf);
		}
		return f2l.get(sf.getDeclaration());
	}

	private Variable lookupFieldLocal(FieldRef fieldRef) {
		Variable baseVar = null;
		if (fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fieldRef;
			ifr.getBase().apply(valueSwitch);
			IdentifierExpression base = (IdentifierExpression) valueSwitch.popExpression();
			baseVar = base.getVariable();
		} else if (fieldRef instanceof StaticFieldRef) {
			baseVar = getStaticFieldContainerVariable();
		} else {
			throw new RuntimeException("not implemented");
		}
		return lookupFieldLocal(baseVar, fieldRef.getField());
	}
	
	public SootField lookupField(Variable local) {
		return localToFieldMap.get(local);
	}

	private void setGeomPointsToAnalysis() {
		HashMap<String, String> opt = new HashMap<String, String>();
		opt.put("enabled", "true");
		opt.put("verbose", "true");
		opt.put("ignore-types", "false");
		opt.put("force-gc", "false");
		// opt.put("pre-jimplify","false");
		// opt.put("vta","false");
		// opt.put("rta","false");
		// opt.put("field-based","false");
		// opt.put("types-for-sites","false");
		// opt.put("merge-stringbuffer","true");
		// opt.put("string-constants","false");
		// opt.put("simulate-natives","true");
		// opt.put("simple-edges-bidirectional","false");
		// opt.put("on-fly-cg","true");
		// opt.put("simplify-offline","false");
		// opt.put("simplify-sccs","false");
		// opt.put("ignore-types-for-sccs","false");
		// opt.put("propagator","worklist");
		opt.put("set-impl", "double");
		opt.put("double-set-old", "hybrid");
		opt.put("double-set-new", "hybrid");
		// opt.put("dump-html","false");
		// opt.put("dump-pag","false");
		// opt.put("dump-solution","false");
		// opt.put("topo-sort","false");
		// opt.put("dump-types","true");
		// opt.put("class-method-var","true");
		// opt.put("dump-answer","false");
		// opt.put("add-tags","false");
		// opt.put("set-mass","false");

		// SparkTransformer.v().transform("",opt);
		SparkOptions so = new SparkOptions(opt);
		GeomPointsTo gpt = new GeomPointsTo(so);
		Scene.v().setPointsToAnalysis(gpt);
	}
}
