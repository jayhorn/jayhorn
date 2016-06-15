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
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class NewMemoryModel extends BasicMemoryModel {

	private HashMap<SootMethod, PackingList> plists;
	
	
	private Map<Variable, Map<SootField, Variable>> fieldToLocalMap = new HashMap<Variable, Map<SootField, Variable>>(); 

	public NewMemoryModel() {
		plists = new HashMap<SootMethod, PackingList>();
	}

	public void updatePullPush() {
		//TODO: this is only here because we know it's only called once per method:
		fieldToLocalMap.clear();
		SootMethod m = SootTranslationHelpers.v().getCurrentMethod();
		PackingList pl = new PackingList(m);
		plists.put(m, pl);
	}

	@Override
	public void mkHeapWriteStatement(Unit u, FieldRef fieldRef, Value rhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Variable fieldVar = lookupField(fieldRef.getField());
		rhs.apply(valueSwitch);
		Expression value = valueSwitch.popExpression();

		ClassVariable classVar;
		IdentifierExpression base;

		if (fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fieldRef;
			ifr.getBase().apply(valueSwitch);
			base = (IdentifierExpression) valueSwitch.popExpression();
			classVar = lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(fieldRef.getField().getDeclaringClass().getType()));
		} else if (fieldRef instanceof StaticFieldRef) {
			/*
			 * For static fields, we use the artificial global class
			 * that holds all static fields as base.
			 */
			Variable glob = getStaticFieldContainerVariable();
			classVar = ((ReferenceType) glob.getType()).getClassVariable();
			base = new IdentifierExpression(loc, glob);
		} else {
			throw new RuntimeException("not implemented");
		}

		lookupFieldLocal(fieldRef); //TODO
		
		Variable[] vars = classVar.getAssociatedFields();
		SootMethod sm = SootTranslationHelpers.v().getCurrentMethod();
		// ------------- unpack ---------------
		if (plists.get(sm) != null && plists.get(sm).unpackAt(fieldRef)) {
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PullStatement(loc, classVar, base, unpackedVars));
		}
		// ------------------------------------
		this.statementSwitch.push(new AssignStatement(loc,
				new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar), value));
		// ------------- pack -----------------
		if (plists.get(sm) != null && plists.get(sm).packAt(fieldRef)) {
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
		Variable fieldVar = lookupField(fieldRef.getField());

		lhs.apply(valueSwitch);
		IdentifierExpression left = (IdentifierExpression) valueSwitch.popExpression();

		ClassVariable classVar;
		IdentifierExpression base;

		if (fieldRef instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) fieldRef;
			ifr.getBase().apply(valueSwitch);
			base = (IdentifierExpression) valueSwitch.popExpression();
			classVar = lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(fieldRef.getField().getDeclaringClass().getType()));
		} else if (fieldRef instanceof StaticFieldRef) {
			/*
			 * For static fields, we use the artificial global class
			 * that holds all static fields as base.
			 */
			Variable glob = getStaticFieldContainerVariable();
			classVar = ((ReferenceType) glob.getType()).getClassVariable();
			base = new IdentifierExpression(loc, glob);
		} else {
			throw new RuntimeException("not implemented");
		}

		Variable[] vars = classVar.getAssociatedFields();
		SootMethod sm = SootTranslationHelpers.v().getCurrentMethod();

		// ------------- unpack ---------------
		if (plists.get(sm) != null && plists.get(sm).unpackAt(fieldRef)) {
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PullStatement(loc, classVar, base, unpackedVars));
		}
		// ------------------------------------
		this.statementSwitch.push(new AssignStatement(loc, left,
				new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar)));
		// ------------- pack -----------------
		if (plists.get(sm) != null && plists.get(sm).packAt(fieldRef)) {
			List<Expression> packedVars = new LinkedList<Expression>();
			for (int i = 0; i < vars.length; i++) {
				packedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PushStatement(loc, classVar, base, packedVars));
		}
		// ------------------------------------
	}

	private static Variable staticFieldContainerVariable = null;
	private static final String gloablsClassName = "JayhornGlobals";
	private static final String gloablsClassVarName = "JayhornGlobalsClassVar";

	protected Variable getStaticFieldContainerVariable() {
		if (staticFieldContainerVariable == null) {
			ClassVariable classVar = new ClassVariable(gloablsClassVarName, new LinkedList<ClassVariable>());
			Set<Variable> fields = new LinkedHashSet<Variable>();
			for (SootClass sc : Scene.v().getClasses()) {
				if (sc.resolvingLevel() >= SootClass.BODIES) {
					for (SootMethod sm : sc.getMethods()) {
						try {
							for (Unit u : sm.retrieveActiveBody().getUnits()) {
								Stmt st = (Stmt)u;
								if (st.containsFieldRef()) {
									SootField sf = st.getFieldRef().getField();
									if (sf.isStatic()) {
										fields.add(this.lookupField(sf));
									}
								}
							}
						} catch (Exception e) {
							
						}
					}
//					for (SootField sf : sc.getFields()) {
//						if (sf.isStatic()) {
//							fields.add(this.lookupField(sf));
//						}
//					}
				}
			}
			List<Variable> fieldList = new LinkedList<Variable>();
			fieldList.addAll(fields);
			classVar.setAssociatedFields(fieldList);
			staticFieldContainerVariable = new Variable(gloablsClassName, new ReferenceType(classVar), true, true);
		}
		return staticFieldContainerVariable;
	}

	/**
	 * If e is an identifier expression for a var of
	 * ReferenceType, return the classvariable otherwise
	 * throw an exception.
	 * @param e
	 * @return
	 */
//	private ClassVariable getClassVarFromExpression(Expression e) {
//		return getClassVariableFromVar(getVarFromExpression(e));
//	}
	
	private Variable getVarFromExpression(Expression e) {
		IdentifierExpression base = (IdentifierExpression)e;
		return base.getVariable();		
	}
	
//	private ClassVariable getClassVariableFromVar(Variable v) {
//		ReferenceType baseType =(ReferenceType)v.getType();
//		return baseType.getClassVariable();		
//	}
	
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
		if (method.getReturnType().size()!=receiver.size()){
			System.err.println(method.getReturnType());
			System.err.println(receiver);
		}
		
		Verify.verify(method.getReturnType().size()==receiver.size(), method.getReturnType().size()+"!="+receiver.size());
		
//		MethodInfo currentMethodInfo = this.statementSwitch.getMethodInfo();
//		for (soottocfg.cfg.type.Type tp : method.getReturnType()) {				
//			Variable lhs = currentMethodInfo.createFreshLocal("foo", tp, true, false);
//			receiver.add(new IdentifierExpression(loc, lhs));
//		}
		//TODO: keep a map between the locals and the fields of this class.
		CallStatement stmt = new CallStatement(loc, method, args, receiver);
		this.statementSwitch.push(stmt);
	}

	
	private Variable lookupFieldLocal(Variable baseVar, SootField sf) {
		if (!fieldToLocalMap.containsKey(baseVar)) {
			fieldToLocalMap.put(baseVar, new HashMap<SootField, Variable>());			
		}
		Map<SootField, Variable> f2l = fieldToLocalMap.get(baseVar);		
		if (!f2l.containsKey(sf)) {
			MethodInfo currentMethodInfo = this.statementSwitch.getMethodInfo();
			soottocfg.cfg.type.Type tp = this.lookupType(sf.getType());
			Variable l = currentMethodInfo.createFreshLocal(baseVar.getName()+"_"+sf.getName()+"_"+sf.getNumber(), tp, sf.isFinal(), false);
			f2l.put(sf, l);
		}
		return f2l.get(sf);
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
}
