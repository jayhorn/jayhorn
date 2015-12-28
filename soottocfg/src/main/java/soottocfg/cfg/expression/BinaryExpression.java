/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.Type;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class BinaryExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1992559147136566989L;

	public enum BinaryOperator {
		Plus("+"), Minus("-"), Mul("*"), Div("/"), Mod("%"), And("&&"), Or("||"), Xor("^"), Implies("->"), Eq("=="), Ne(
				"!="), Gt(">"), Ge(">="), Lt("<"), Le("<="), Shl("<<"), Shr(">>"), Ushr("u>>"), BOr("|"), BAnd("&");

		private final String name;

		private BinaryOperator(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return (otherName == null) ? false : name.equals(otherName);
		}

		@Override
		public String toString() {
			return this.name;
		}
	}

	private final Expression left, right;
	private final BinaryOperator op;

	public BinaryExpression(SourceLocation loc, BinaryOperator op, Expression left, Expression right) {
		super(loc);
		if (left.getType().getClass() != right.getType().getClass()
				&& !SootTranslationHelpers.v().getMemoryModel().isNullReference(right)) {
			// TODO: this should be somewhere in the translation.
			if (left.getType() == BoolType.instance() && right instanceof IntegerLiteral) {
				if (((IntegerLiteral) right).getValue() == 0L) {
					right = BooleanLiteral.falseLiteral();
				} else if (((IntegerLiteral) right).getValue() == 1L) {
					right = BooleanLiteral.trueLiteral();
				} else {
					throw new RuntimeException();
				}
			}
		}
		Preconditions.checkArgument(
				left.getType().getClass() == right.getType().getClass()
						|| SootTranslationHelpers.v().getMemoryModel().isNullReference(right)
						|| SootTranslationHelpers.v().getMemoryModel().isNullReference(left),
				"Types don't match: " + left.getType() + " and " + right.getType());
		// TODO: more type checking depending on operator
		this.left = left;
		this.right = right;
		this.op = op;
	}

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	public BinaryOperator getOp() {
		return op;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.left);
		sb.append(" " + this.op + " ");
		sb.append(this.right);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> ret = new HashSet<IdentifierExpression>();
		ret.addAll(left.getUseIdentifierExpressions());
		ret.addAll(right.getUseIdentifierExpressions());
		return ret;
	}

	@Override
	public Set<Variable> getDefVariables() {
		// because this can't happen on the left.
		Set<Variable> used = new HashSet<Variable>();
		return used;
	}

	@Override
	public Type getType() {
		switch (op) {
		case Plus:
		case Minus:
		case Mul:
		case Div:
		case Mod: {
			assert (left.getType().equals(right.getType()));
			return left.getType();
		}
		case Eq:
		case Ne:
		case Gt:
		case Ge:
		case Lt:
		case Le: {
			// assert(left.getType().equals(right.getType()));
			return BoolType.instance();
		}
		case And:
		case Or:
		case Implies: {
			assert (left.getType() == BoolType.instance() && right.getType() == BoolType.instance());
			return BoolType.instance();
		}

		default: {
			// throw new RuntimeException("Not implemented for " + op);
			System.err.println("Not tested for " + op);
			return left.getType();
		}
		}
	}

	@Override
	public Expression deepCopy() {		
		return new BinaryExpression(getSourceLocation(), op, left.deepCopy(), right.deepCopy());
	}

}
