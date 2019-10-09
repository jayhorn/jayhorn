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
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.variable.Variable;

/**
 * @author teme
 *
 */
public class AssumeStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4719730863944690585L;
	private Expression expression;

	/**
	 * @param createdFrom
	 */
	public AssumeStatement(SourceLocation loc, Expression expr) {
		super(loc);
		this.expression = expr.castToBoolIfNecessary();
	}

	public Expression getExpression() {
		return this.expression;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("assume ");
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
	public AssumeStatement deepCopy() {
		return new AssumeStatement(getSourceLocation(), expression);
	}

	@Override
	public AssumeStatement substitute(Map<Variable, Variable> subs) {
            Expression newExpr = expression.substitute(subs);
            if (newExpr == expression)
                return this;
            else
		return new AssumeStatement(getSourceLocation(), newExpr);
	}

	@Override
	public AssumeStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
            Expression newExpr = expression.substituteVarWithExpression(subs);
            if (newExpr == expression)
                return this;
            else
		return new AssumeStatement(getSourceLocation(), newExpr);
	}
	
}
