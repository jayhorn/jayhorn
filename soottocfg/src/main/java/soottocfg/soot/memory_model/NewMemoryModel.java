/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.LinkedList;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.PackStatement;
import soottocfg.cfg.statement.UnPackStatement;
import soottocfg.cfg.type.ClassSignature;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class NewMemoryModel extends BasicMemoryModel {

	public NewMemoryModel() {
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

			// ------------- unpack ---------------
			ClassSignature c = lookupClassSignature(field.getField().getDeclaringClass());
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			Variable[] vars = c.getAssociatedFields();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new UnPackStatement(loc, c, base, unpackedVars));
			// ------------------------------------
			this.statementSwitch.push(new AssignStatement(loc, new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar), value));
			// ------------- pack -----------------
			List<Expression> packedVars = new LinkedList<Expression>();
			for (int i = 0; i < vars.length; i++) {
				packedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new PackStatement(loc, c, base, packedVars));
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
			ClassSignature c = lookupClassSignature(field.getField().getDeclaringClass());
			List<IdentifierExpression> unpackedVars = new LinkedList<IdentifierExpression>();
			Variable[] vars = c.getAssociatedFields();
			for (int i = 0; i < vars.length; i++) {
				unpackedVars.add(new IdentifierExpression(this.statementSwitch.getCurrentLoc(), vars[i]));
			}
			this.statementSwitch.push(new UnPackStatement(loc, c, base, unpackedVars));
			// ------------------------------------
			this.statementSwitch.push(new AssignStatement(loc, left, new IdentifierExpression(this.statementSwitch.getCurrentLoc(), fieldVar)));
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
