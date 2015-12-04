/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;

/**
 * @author teme
 *
 */
public class AssertStatement extends Statement {

	/**
	 * 
	 */
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
	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(expression.getIdentifierExpressions());
		return used;
	}
	
	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		return used;
	}

}
