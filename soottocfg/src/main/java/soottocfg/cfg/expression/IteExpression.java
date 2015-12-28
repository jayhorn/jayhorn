/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class IteExpression extends Expression {

	private static final long serialVersionUID = -7138261822713810587L;
	private final Expression condition, thenExpr, elseExpr;

	public Expression getCondition() {
		return condition;
	}

	public Expression getThenExpr() {
		return thenExpr;
	}

	public Expression getElseExpr() {
		return elseExpr;
	}

	public IteExpression(SourceLocation loc, Expression condition, Expression thenExpr, Expression elseExpr) {
		super(loc);
		this.condition = condition;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.condition);
		sb.append("?");
		sb.append(this.thenExpr);
		sb.append(":");
		sb.append(this.elseExpr);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> ret = new HashSet<IdentifierExpression>();
		ret.addAll(condition.getUseIdentifierExpressions());
		ret.addAll(thenExpr.getUseIdentifierExpressions());
		ret.addAll(elseExpr.getUseIdentifierExpressions());
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
		assert (thenExpr.getType().equals(elseExpr.getType()));
		return thenExpr.getType();
	}

	@Override
	public Expression deepCopy() {		
		return new IteExpression(getSourceLocation(), condition.deepCopy(), thenExpr.deepCopy(), elseExpr.deepCopy());
	}

}
