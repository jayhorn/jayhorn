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
import soot.jimple.JimpleBody;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
//import soot.jimple.spark.geom.geomPA.GeomPointsTo;
//import soot.options.SparkOptions;
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
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class NewMemoryModel extends BasicMemoryModel {

	private Map<Variable, Map<String, Variable>> fieldToLocalMap = new HashMap<Variable, Map<String, Variable>>();

	// This one sticks around between method calls...
	private Map<Variable, SootField> localToFieldMap = new HashMap<Variable, SootField>();

	private static Variable staticFieldContainerVariable;
	private static List<SootField> usedStaticFields;
	public static final String globalsClassName = "JayhornGlobals";
	private static final String globalsClassVarName = "JayhornGlobalsClassVar";

	protected static void resetGlobalState() {
		NewMemoryModel.staticFieldContainerVariable = null;
		NewMemoryModel.usedStaticFields = null;
	}

	public NewMemoryModel() {
		NewMemoryModel.resetGlobalState();

		// load points to analysis
		// setGeomPointsToAnalysis();
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

	/**
	 * Helper method to create a push of all field locals.
	 * This method should only be used at the very end of a constructor.
	 */
	private void pushAllFields() {
		SootMethod m = SootTranslationHelpers.v().getCurrentMethod();
		if (!m.isConstructor() || m.isStatic() || !m.isConcrete()) {
			return;
		}
		SourceLocation loc = this.statementSwitch.getCurrentLoc();

		Variable thisLocal = this.statementSwitch.getMethodInfo().lookupLocalVariable(m.getActiveBody().getThisLocal());
		ClassVariable classVar = ((ReferenceType) thisLocal.getType()).getClassVariable();
		
		List<Variable> vars = new LinkedList<Variable>();
		for (SootField sf : SootTranslationHelpers.findNonStaticFieldsRecursively(m.getDeclaringClass())) {
			vars.add(lookupFieldLocal(thisLocal, sf));
		}

		List<Expression> packedVars = new LinkedList<Expression>();
		for (int i = 0; i < vars.size(); i++) {
			packedVars.add(new IdentifierExpression(loc, vars.get(i)));
		}

		this.statementSwitch.push(new PushStatement(loc, classVar, new IdentifierExpression(loc, thisLocal), packedVars));
	}

	private boolean pushAt(Unit u, FieldRef fr) {
		SootMethod m = SootTranslationHelpers.v().getCurrentMethod();
		UnitGraph graph = new CompleteUnitGraph(m.getActiveBody());

		if (fr instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fr;

			// in constructor only push 'this' after the last access
			if (m.isConstructor() && ifr.getBase().equals(m.getActiveBody().getThisLocal())) {

				// check if there is any path from 'u' to the tail(s) that does
				// not contain an write to 'this'
				List<Unit> tails = graph.getTails();
				List<Unit> todo = new LinkedList<Unit>();
				todo.add(u);
				boolean foundPathWithoutAccess = false;
				while (!todo.isEmpty()) {
					Unit unit = todo.remove(0);

					// if it contains another write to 'this', stop exploring
					// this path
					Stmt s = (Stmt) unit;
					if (s.containsFieldRef()) {
						FieldRef fr2 = s.getFieldRef();
						if (fr2 instanceof InstanceFieldRef) {
							InstanceFieldRef ifr2 = (InstanceFieldRef) fr2;
							if (ifr2.getBase().equals(m.getActiveBody().getThisLocal()) && !u.equals(unit)) {
								continue;
							}
						}
					} else if (s.containsInvokeExpr() && s.getInvokeExpr() instanceof SpecialInvokeExpr) {
						// also stop exploring if there is a call to the
						// super class constructor.
						SpecialInvokeExpr ivk = (SpecialInvokeExpr) s.getInvokeExpr();

						if (ivk.getMethod().isConstructor() && ivk.getBase().equals(m.getActiveBody().getThisLocal())) {
							// super class constructor.
							continue;
						}
					}

					if (tails.contains(unit)) {
						if (!s.containsInvokeExpr()) {
							foundPathWithoutAccess = true; // at the end of this
															// path
						}
					} else {
						todo.addAll(graph.getSuccsOf(unit));
					}
				}
				return foundPathWithoutAccess;
			}
		} else if (fr instanceof StaticFieldRef) {
			// in static initializer only push at the end
			if (m.isStaticInitializer()) {

				// check if there is any path from 'u' to the tail(s) that does
				// not contain an access to 'this'
				List<Unit> tails = graph.getTails();
				List<Unit> todo = new LinkedList<Unit>();
				todo.add(u);
				boolean foundPathWithoutAccess = false;
				while (!todo.isEmpty()) {
					Unit unit = todo.remove(0);

					// if it contains another access to a static field, stop
					// exploring this path
					Stmt s = (Stmt) unit;
					if (s.containsFieldRef()) {
						FieldRef fr2 = s.getFieldRef();
						if (fr2 instanceof StaticFieldRef && !u.equals(unit))
							continue;
					}

					if (tails.contains(unit))
						foundPathWithoutAccess = true; // at the end of this
														// path
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
			// classVar = lookupClassVariable(
			// SootTranslationHelpers.v().getClassConstant(fieldRef.getField().getDeclaringClass().getType()));
			classVar = ((ReferenceType) base.getType()).getClassVariable();
			// for (SootField sf :
			// findFieldsRecursively(fieldRef.getField().getDeclaringClass())) {
			for (SootField sf : SootTranslationHelpers.findFieldsRecursivelyForRef(ifr.getBase())) {
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
			// classVar = lookupClassVariable(
			// SootTranslationHelpers.v().getClassConstant(fieldRef.getField().getDeclaringClass().getType()));
			classVar = ((ReferenceType) base.getType()).getClassVariable();

			for (SootField sf : SootTranslationHelpers.findFieldsRecursivelyForRef(ifr.getBase())) {
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
		// if (pushAt(u, fieldRef)) {
		// List<Expression> packedVars = new LinkedList<Expression>();
		// for (int i = 0; i < vars.length; i++) {
		// packedVars.add(new
		// IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
		// }
		// this.statementSwitch.push(new PushStatement(loc, classVar, base,
		// packedVars));
		// }
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

										if (!sf.equals(SootTranslationHelpers.v().getExceptionGlobal())) {
											// TODO hack to exclude the
											// exception field.
											fields.add(this.lookupField(sf));
											usedStaticFields.add(sf);

										}
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
			classVar.addFields(fieldList);
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

	// private Variable getVarFromExpression(Expression e) {
	// IdentifierExpression base = (IdentifierExpression) e;
	// return base.getVariable();
	// }

	// private ClassVariable getClassVariableFromVar(Variable v) {
	// ReferenceType baseType =(ReferenceType)v.getType();
	// return baseType.getClassVariable();
	// }

	@Override
	public void mkConstructorCall(Unit u, SootMethod constructor, List<Expression> args) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Method method = SootTranslationHelpers.v().lookupOrCreateMethod(constructor);

		List<Expression> receiver = new LinkedList<Expression>();
		receiver.add(this.statementSwitch.getMethodInfo().getExceptionVariable());

		SootClass declClass = constructor.getDeclaringClass();
		SootClass currentClass = SootTranslationHelpers.v().getCurrentMethod().getDeclaringClass();

		if (currentClass.hasSuperclass() && declClass.equals(currentClass.getSuperclass())) {
			/*
			 * If this is a call to the super class constructor, use the return
			 * values to assign all field locals.
			 */
			JimpleBody jb = (JimpleBody) SootTranslationHelpers.v().getCurrentMethod().getActiveBody();
			Variable thisLocal = this.statementSwitch.getMethodInfo().lookupLocalVariable(jb.getThisLocal());

			for (SootField sf : SootTranslationHelpers.findNonStaticFieldsRecursively(declClass)) {
				receiver.add(new IdentifierExpression(loc, lookupFieldLocal(thisLocal, sf)));
			}
			verifyArgLength(u, method, receiver);

			CallStatement stmt = new CallStatement(loc, method, args, receiver);
			this.statementSwitch.push(stmt);
			// for superclass constructor calls, we need to update the dynamic
			// type
			// after the call.
//TODO: not needed with Tuple Types
//			Variable typeVar = lookupFieldLocal(thisLocal, SootTranslationHelpers.getTypeField(currentClass));
//			this.statementSwitch
//					.push(new AssignStatement(loc, new IdentifierExpression(loc, typeVar), new IdentifierExpression(loc,
//							((ReferenceType) lookupType(currentClass.getType())).getClassVariable())));
			
			//TODO: this is a hack at should be handeled properly.
			pushAllFields();

		} else {
			/*
			 * If this is not a superclass constructor, just fill the args up
			 * with dummy variables.
			 */
			// ignore the first outparam (for the exception) because we added
			// that already.
			for (int i = 1; i < method.getReturnType().size(); i++) {
				Variable freshLocal = this.statementSwitch.getMethodInfo().createFreshLocal("cret_",
						method.getReturnType().get(i), false, false);
				receiver.add(new IdentifierExpression(loc, freshLocal));
			}
			verifyArgLength(u, method, receiver);

			CallStatement stmt = new CallStatement(loc, method, args, receiver);
			this.statementSwitch.push(stmt);

		}
	}

	private void verifyArgLength(Unit u, Method method, List<Expression> receiver) {
		if (method.getReturnType().size() != receiver.size()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			for (Type t : method.getReturnType()) {
				sb.append(", ");
				sb.append(t.toString());
			}
			sb.append("]");
			sb.append(" != ");
			sb.append("[");
			for (Expression e : receiver) {
				sb.append(", ");
				sb.append(e.toString());
			}
			sb.append("]");

			Verify.verify(false, method.getMethodName() + " -> " + sb.toString());

		}
	}

	@Override
	public void mkCopy(Local lhs, Local rhs) {
		// System.out.println("Copying " + rhs + " into " + lhs);
		lhs.apply(valueSwitch);
		IdentifierExpression base = (IdentifierExpression) valueSwitch.popExpression();
		for (Map.Entry<Variable, Map<String, Variable>> e : fieldToLocalMap.entrySet()) {
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

			/*
			 * for this references in constructors
			 * we return the out params as field locals.
			 */
			if (this.statementSwitch.getMethod().isConstructor()) {
				JimpleBody jb = (JimpleBody) this.statementSwitch.getMethod().getActiveBody();
				Variable thisVar = statementSwitch.getMethodInfo().lookupLocalVariable(jb.getThisLocal());
				if (baseVar.equals(thisVar)) {
					int i = 1;
					for (SootField cfield : SootTranslationHelpers.findNonStaticFieldsRecursively(sf.getDeclaringClass())) {
						if (cfield.equals(sf)) {
							Variable outVar = statementSwitch.getMethodInfo().getOutVariable(i);
							if (!f2l.containsKey(sf.getDeclaration())) {
								f2l.put(sf.getDeclaration(), outVar);
							}
							localToFieldMap.put(outVar, sf);
							break;
						}
						i++;
					}
				}
			} else {

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

	// private void setGeomPointsToAnalysis() {
	// HashMap<String, String> opt = new HashMap<String, String>();
	// opt.put("enabled", "true");
	// opt.put("verbose", "true");
	// opt.put("ignore-types", "false");
	// opt.put("force-gc", "false");
	// // opt.put("pre-jimplify","false");
	// // opt.put("vta","false");
	// // opt.put("rta","false");
	// // opt.put("field-based","false");
	// // opt.put("types-for-sites","false");
	// // opt.put("merge-stringbuffer","true");
	// // opt.put("string-constants","false");
	// // opt.put("simulate-natives","true");
	// // opt.put("simple-edges-bidirectional","false");
	// // opt.put("on-fly-cg","true");
	// // opt.put("simplify-offline","false");
	// // opt.put("simplify-sccs","false");
	// // opt.put("ignore-types-for-sccs","false");
	// // opt.put("propagator","worklist");
	// opt.put("set-impl", "double");
	// opt.put("double-set-old", "hybrid");
	// opt.put("double-set-new", "hybrid");
	// // opt.put("dump-html","false");
	// // opt.put("dump-pag","false");
	// // opt.put("dump-solution","false");
	// // opt.put("topo-sort","false");
	// // opt.put("dump-types","true");
	// // opt.put("class-method-var","true");
	// // opt.put("dump-answer","false");
	// // opt.put("add-tags","false");
	// // opt.put("set-mass","false");
	//
	// // SparkTransformer.v().transform("",opt);
	// SparkOptions so = new SparkOptions(opt);
	// GeomPointsTo gpt = new GeomPointsTo(so);
	// Scene.v().setPointsToAnalysis(gpt);
	// }
}
