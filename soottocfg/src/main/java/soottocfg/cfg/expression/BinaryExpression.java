/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.type.TypeType;
import soottocfg.cfg.variable.Variable;
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
		Plus("+"), Minus("-"), Mul("*"), Div("/"), Mod("%"), And("&&"), Or("||"), Xor("^"), Implies("->"), Eq("=="),
		Ne("!="), Gt(">"), Ge(">="), Lt("<"), Le("<="), Shl("<<"), Shr(">>"), Ushr("u>>"), BOr("|"), BAnd("&"),
		PoLeq("<:"), StringEq("==="), StringConcat("+++"), StringCompareTo("<?>"), StartsWith("startsWith"), EndsWith("endsWith"), CharAt("charAt"),
		ToString("<str>"), BoolToString("<str_b>"), CharToString("<str_c>"), IndexInString("<idx_str>"), StringIndexOf("<idx_of>"), StringIndexOfChar("<idx_of_char>"),
		StringLastIndexOf("<last_idx_of>"), StringLastIndexOfChar("<last_idx_of_char>");	// TODO: not an actual BinaryExpression

		private final String name;

		private BinaryOperator(String s) {
			name = s;
		}

		public boolean equalsName(String otherName) {
			return name.equals(otherName);	// Object.equals(o) returns false when o is null
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
					throw new RuntimeException("BinaryExpression: bool/int confusion");
				}
			} else if (right.getType() == BoolType.instance() && left instanceof IntegerLiteral) {
				if (((IntegerLiteral) left).getValue() == 0L) {
					left = BooleanLiteral.falseLiteral();
				} else if (((IntegerLiteral) left).getValue() == 1L) {
					left = BooleanLiteral.trueLiteral();
				} else {
					throw new RuntimeException("BinaryExpression: bool/int confusion");
				}				
			}
		}

		Preconditions.checkArgument(
				left.getType().getClass() == right.getType().getClass()
						|| (left.getType() instanceof ReferenceType && right.getType() instanceof ReferenceType &&
							((ReferenceType) left.getType()).getClassVariable().equals(((ReferenceType) right.getType()).getClassVariable()))
						|| SootTranslationHelpers.v().getMemoryModel().isNullReference(right)
						|| SootTranslationHelpers.v().getMemoryModel().isNullReference(left)
						|| op == BinaryOperator.CharAt || op == BinaryOperator.IndexInString
						|| op == BinaryOperator.ToString || op == BinaryOperator.BoolToString || op == BinaryOperator.CharToString
							|| op == BinaryOperator.StringIndexOfChar || op == BinaryOperator.StringLastIndexOfChar
						|| ((op == BinaryOperator.StringConcat || op == BinaryOperator.StringCompareTo || op == BinaryOperator.StringIndexOf || op == BinaryOperator.StringLastIndexOf) &&
							(left.getType() instanceof ReferenceType || right.getType() instanceof ReferenceType)),
				"Types don't match: " + left.getType() + " and " + right.getType() + " for "+left.toString()+op+right.toString());
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
		if (this.op.equals(BinaryOperator.ToString) || this.op.equals(BinaryOperator.BoolToString) ||
			this.op.equals(BinaryOperator.CharToString)) {
			sb.append(this.op);
			sb.append(left);
		} else {
			sb.append(this.left);
			sb.append(" " + this.op + " ");
			sb.append(this.right);
		}
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
			case StringEq:
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

			case PoLeq:
			case StartsWith:
			case EndsWith:
			case IndexInString: {
				return BoolType.instance();
			}

			case CharAt: {
				return IntType.instance();
			}

			case ToString:
			case BoolToString:
			case CharToString: {
				return right.getType();
			}

			default: {
				//TODO: more testing here?
				return left.getType();
			}
		}
	}

	public BinaryExpression substitute(Map<Variable, Variable> subs) {
            Expression newLeft = left.substitute(subs);
            Expression newRight = right.substitute(subs);
            if (left == newLeft && right == newRight)
                return this;
            else
		return new BinaryExpression(getSourceLocation(), op, newLeft, newRight);
	}

	public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
            if (op == BinaryOperator.PoLeq &&
                left instanceof TupleAccessExpression) {
                final TupleAccessExpression ta = (TupleAccessExpression)left;
                if (subs.get(ta.getVariable()) instanceof NullLiteral &&
                    ta.getType() instanceof TypeType) {
                    // as a special case, if the left operand of an
                    // instanceof check is replace with null, the
                    // whole expression becomes false.  we currently
                    // cannot express null instanceof Class in the
                    // CFG, so we catch this case right away
                    return BooleanLiteral.falseLiteral();
                }
            }

            Expression newLeft = left.substituteVarWithExpression(subs);
            Expression newRight = right.substituteVarWithExpression(subs);
            if (left == newLeft && right == newRight)
                return this;
            else
		return new BinaryExpression(getSourceLocation(), op, newLeft, newRight);
	}

}
