/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class NewMemoryModel extends BasicMemoryModel {

	private HashMap<SootMethod, PackingList> plists;

	public NewMemoryModel() {
		plists = new HashMap<SootMethod, PackingList>();
	}

	public void updatePullPush() {
		// System.out.println("Determining when to PACK / UNPACK");
		SootMethod m = SootTranslationHelpers.v().getCurrentMethod();
		PackingList pl = new PackingList(m);
		plists.put(m, pl);
		// System.out.println("Done.");
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

		Variable[] vars = classVar.getAssociatedFields();
		SootMethod sm = SootTranslationHelpers.v().getCurrentMethod();
		// ------------- unpack ---------------
		// if (!skipUnpack) {
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
		// if (!skipPack) {
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
			List<Variable> fields = new LinkedList<Variable>();
			for (SootClass sc : Scene.v().getClasses()) {
				if (sc.resolvingLevel() >= SootClass.SIGNATURES) {
					for (SootField sf : sc.getFields()) {
						if (sf.isStatic()) {
							fields.add(this.lookupField(sf));
						}
					}
				}
			}
			classVar.setAssociatedFields(fields);
			staticFieldContainerVariable = new Variable(gloablsClassName, new ReferenceType(classVar), true, true);
		}
		return staticFieldContainerVariable;
	}

}
