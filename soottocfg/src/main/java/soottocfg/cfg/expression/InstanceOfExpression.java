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
	private final Variable typeVariable;
	
	public InstanceOfExpression(SourceLocation loc, Expression expr, Variable typeVar) {
		super(loc);
		this.expression = expr;
		this.typeVariable = typeVar;
	}

	public Expression getExpression() {
		return expression;
	}

	public Variable getTypeVariable() {
		return typeVariable;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.expression);
		sb.append(" instanceof ");
		sb.append(this.typeVariable.getName());
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
		return new InstanceOfExpression(getSourceLocation(), expression.deepCopy(), typeVariable);
	}
}
