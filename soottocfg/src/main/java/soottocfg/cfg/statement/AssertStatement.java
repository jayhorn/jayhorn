/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;

/**
 * @author teme, schaef
 *
 */
public class AssertStatement extends Statement {

	private static final long serialVersionUID = -2043514337815140767L;
	private final Expression expression;

	/**
	 * @param createdFrom
	 */
	public AssertStatement(SourceLocation loc, Expression expr) {
		super(loc);
		this.expression = expr.castToBoolIfNecessary();
	}

	public Expression getExpression() {
		return this.expression;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("assert ");
		sb.append(this.expression);
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(expression.getUseIdentifierExpressions());
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		return new HashSet<IdentifierExpression>();
	}

	@Override
	public Statement deepCopy() {
		return new AssertStatement(getSourceLocation(), expression.deepCopy());
	}
}
