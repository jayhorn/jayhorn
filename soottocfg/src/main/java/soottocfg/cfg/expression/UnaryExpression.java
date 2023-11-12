/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class UnaryExpression extends Expression {

	private static final long serialVersionUID = -3534248180235954114L;
	private final Expression expression;
	private final UnaryOperator op;

	public enum UnaryOperator {
		Neg("-"), LNot("!"), Len("<len>");
		private final String name;

		private UnaryOperator(String s) {
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

	public UnaryExpression(SourceLocation loc, UnaryOperator op, Expression inner) {
		super(loc);
		this.expression = inner;
		this.op = op;
	}

	public Expression getExpression() {
		return expression;
	}

	public UnaryOperator getOp() {
		return op;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.op);
		sb.append(this.expression);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		return expression.getUseIdentifierExpressions();
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
			case LNot: {
				return BoolType.instance();
			}
			case Neg: {
				return expression.getType();
			}
			case Len: {
				return IntType.instance();
			}
		}
		throw new RuntimeException("Unknown case " + op);
	}

	@Override
	public UnaryExpression substitute(Map<Variable, Variable> subs) {
            Expression newE = expression.substitute(subs);
            if (newE == expression)
                return this;
            else
		return new UnaryExpression(getSourceLocation(), op, newE);
	}

	@Override
	public UnaryExpression substituteVarWithExpression(Map<Variable, Expression> subs) {
            Expression newE = expression.substituteVarWithExpression(subs);
            if (newE == expression)
                return this;
            else
		return new UnaryExpression(getSourceLocation(), op, newE);
	}
	
}
