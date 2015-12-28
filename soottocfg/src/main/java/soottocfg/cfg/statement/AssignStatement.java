/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.type.BoolType;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class AssignStatement extends Statement {

	private static final long serialVersionUID = 6725099779878843508L;

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	private final Expression left, right;

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
					throw new RuntimeException();
				}
			}
		}
		if (lhs instanceof IdentifierExpression) {
			Preconditions.checkArgument(!((IdentifierExpression) lhs).getVariable().isConstant()
					&& !((IdentifierExpression) lhs).getVariable().isUnique());
		}

		this.left = lhs;
		this.right = rhs;
		// if (left.getType().getClass()!=right.getType().getClass()) {
		// System.err.println("");
		// }
		// Preconditions.checkArgument(left.getType().getClass()==right.getType().getClass()
		// ||
		// SootTranslationHelpers.v().getMemoryModel().isNullReference(right),
		// "Types don't match: "+ left.getType() + " and " + right.getType() + "
		// for " + left + " and " + right);
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
		return new AssignStatement(getSourceLocation(), left.deepCopy(), right.deepCopy());
	}

}
