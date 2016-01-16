/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class InstanceOfExpression extends Expression {

	private static final long serialVersionUID = -3697139804431041726L;
	private final Expression expression;
	private final Type type;
	
	public InstanceOfExpression(SourceLocation loc, Expression expr, Type t) {
		super(loc);
		this.expression = expr;
		this.type = t;
	}

	public Expression getExpression() {
		return expression;
	}

	public Type getCheckedType() {
		return type;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.expression);
		sb.append(" instanceof ");
		sb.append(this.type);
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
		return BoolType.instance();
	}

	@Override
	public Expression deepCopy() {		
		return new InstanceOfExpression(getSourceLocation(), expression.deepCopy(), type);
	}
}
