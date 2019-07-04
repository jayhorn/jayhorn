/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class AssignStatement extends Statement {

	private static final long serialVersionUID = 6725099779878843508L;
	private Expression left, right;

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	/**
	 * @param createdFrom
	 */
	public AssignStatement(SourceLocation loc, Expression lhs, Expression rhs) {
		super(loc);
		if (lhs.getType().getClass() != rhs.getType().getClass()
				&& !SootTranslationHelpers.v().getMemoryModel().isNullReference(rhs)) {
			// TODO: this should be somewhere in the translation.
			if (lhs.getType() == BoolType.instance() && rhs instanceof IntegerLiteral) {
				if (((IntegerLiteral) rhs).getValue() == 0L) {
					rhs = BooleanLiteral.falseLiteral();
				} else if (((IntegerLiteral) rhs).getValue() == 1L) {
					rhs = BooleanLiteral.trueLiteral();
				} else {
					throw new RuntimeException("Assignment statement not implemented for bool/int mix.");
				}
			}
		}

		this.left = lhs;
		this.right = rhs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.left);
		sb.append(" := ");
		sb.append(this.right);
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(right.getUseIdentifierExpressions());
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		if (left instanceof IdentifierExpression) {
			res.add((IdentifierExpression)left);	
		} else {
			throw new RuntimeException("Did not consider the case where lhs is not an IdentifierExpression.");
		}
		return res;
	}

	@Override
	public Statement deepCopy() {
		return new AssignStatement(getSourceLocation(), left, right);
	}

	@Override
	public AssignStatement substitute(Map<Variable, Variable> subs) {
            Expression newLeft = left.substitute(subs);
            Expression newRight = right.substitute(subs);
            if (newLeft == left && newRight == right)
                return this;
            else
		return new AssignStatement(getSourceLocation(), newLeft, newRight);
	}

	@Override
	public AssignStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
            Expression newRight = right.substituteVarWithExpression(subs);
            if (newRight == right)
                return this;
            else
		return new AssignStatement(getSourceLocation(), left, newRight);
	}
	
}
