/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.Unit;
import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.PackStatement;
import soottocfg.cfg.statement.UnPackStatement;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class NewMemoryModel extends BasicMemoryModel {

	public NewMemoryModel() {
	}

	private boolean needsPacking(Unit u, FieldRef f) {
		UnitGraph graph = new CompleteUnitGraph(SootTranslationHelpers.v().getCurrentMethod().getActiveBody());
		List<Unit> todo = new LinkedList<Unit>(graph.getSuccsOf(u));
		Set<Unit> done = new HashSet<Unit>();
		while (!todo.isEmpty()) {
			Unit current = todo.remove(0);
			Stmt s = (Stmt)current;
			if (s.containsFieldRef() && s.getFieldRef().getField()==f.getField()) {
				return false;
			}
			done.add(current);
			for (Unit next : graph.getSuccsOf(current)) {
				if (!todo.contains(next) && !done.contains(next)) {
					todo.add(next);
				}
			}
		}
		return true;
	}

	private boolean needsUnpacking(Unit u, FieldRef f) {
		UnitGraph graph = new CompleteUnitGraph(SootTranslationHelpers.v().getCurrentMethod().getActiveBody());
		List<Unit> todo = new LinkedList<Unit>(graph.getPredsOf(u));
		Set<Unit> done = new HashSet<Unit>();
		while (!todo.isEmpty()) {
			Unit current = todo.remove(0);
			System.err.println("\tLooking at "+current + " from " +u);
			Stmt s = (Stmt)current;
			if (s.containsFieldRef() && s.getFieldRef().getField()==f.getField()) {
				return false;
			}
			done.add(current);
			for (Unit next : graph.getPredsOf(current)) {
				if (!todo.contains(next) && !done.contains(next)) {
					todo.add(next);
				}
			}
		}
		return true;	
	}

	
	@Override
	public void mkHeapWriteStatement(Unit u, FieldRef field, Value rhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Variable fieldVar = lookupField(field.getField());
		if (field instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) field;
			ifr.getBase().apply(valueSwitch);
			IdentifierExpression base = (IdentifierExpression) valueSwitch.popExpression();
			rhs.apply(valueSwitch);
			Expression value = valueSwitch.popExpression();

			ClassVariable c = lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(field.getField().getDeclaringClass().getType()));
			Variable[] vars = c.getAssociatedFields();

			boolean skipUnpack = false;
			boolean skipPack = false;

			// do not pack or unpack if we are in a constructor!
			if (SootTranslationHelpers.v().getCurrentMethod().isConstructor() && ifr.getBase()
					.equals(SootTranslationHelpers.v().getCurrentMethod().getActiveBody().getThisLocal())) {
				skipUnpack = true;
				skipPack = true;
			}
			
			if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof AnyNewExpr
					&& field.getField().getName().contains(SootTranslationHelpers.typeFieldName)) {
				// TODO: Hacky way of suppressing the unpack after new.
				skipUnpack = true;
			}

			if (!needsPacking(u, field)) { //TODO hack remove
				System.err.println("not packing " + u);
				skipPack = true;
			}
			if (!needsUnpacking(u, field)) {//TODO hack remove
				System.err.println("not unpacking " + u);
				skipUnpack = true;
			}
			
			// ------------- unpack ---------------
			if (!skipUnpack) {
				List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
				for (int i = 0; i < vars.length; i++) {
					unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
				}
				this.statementSwitch.push(new UnPackStatement(loc, c, base, unpackedVars));
			}
			// ------------------------------------
			this.statementSwitch.push(new AssignStatement(loc,
					new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar), value));
			// ------------- pack -----------------
			if (!skipPack) {
				List<Expression> packedVars = new LinkedList<Expression>();
				for (int i = 0; i < vars.length; i++) {
					packedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
				}
				this.statementSwitch.push(new PackStatement(loc, c, base, packedVars));
			}
			// ------------------------------------

		} else if (field instanceof StaticFieldRef) {
			Expression left = new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar);
			rhs.apply(valueSwitch);
			Expression right = valueSwitch.popExpression();
			this.statementSwitch.push(new AssignStatement(loc, left, right));
		} else {
			throw new RuntimeException("not implemented");
		}
	}

	@Override
	public void mkHeapReadStatement(Unit u, FieldRef field, Value lhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Variable fieldVar = lookupField(field.getField());
		if (field instanceof InstanceFieldRef) {
			lhs.apply(valueSwitch);
			IdentifierExpression left = (IdentifierExpression) valueSwitch.popExpression();

			InstanceFieldRef ifr = (InstanceFieldRef) field;
			ifr.getBase().apply(valueSwitch);
			IdentifierExpression base = (IdentifierExpression) valueSwitch.popExpression();

			// ------------- unpack ---------------
			ClassVariable c = lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(field.getField().getDeclaringClass().getType()));
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			Variable[] vars = c.getAssociatedFields();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new UnPackStatement(loc, c, base, unpackedVars));
			// ------------------------------------
			this.statementSwitch.push(new AssignStatement(loc, left,
					new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar)));
			// ------------- pack -----------------
			List<Expression> packedVars = new LinkedList<Expression>();
			for (int i = 0; i < vars.length; i++) {
				packedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PackStatement(loc, c, base, packedVars));
			// ------------------------------------
		} else if (field instanceof StaticFieldRef) {
			lhs.apply(valueSwitch);
			Expression left = valueSwitch.popExpression();
			Expression right = new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar);
			this.statementSwitch.push(new AssignStatement(loc, left, right));
		} else {
			throw new RuntimeException("not implemented");
		}
	}

}
