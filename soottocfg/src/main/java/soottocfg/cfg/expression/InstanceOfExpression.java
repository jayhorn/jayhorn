/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Variable;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class InstanceOfExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3697139804431041726L;
	private final Expression expression;
	public Expression getExpression() {
		return expression;
	}

	public Variable getTypeVariable() {
		return typeVariable;
	}

	private final Variable typeVariable;
	
	
	/**
	 * 
	 */
	public InstanceOfExpression(Expression expr, Variable typeVar) {		
		this.expression = expr;
		this.typeVariable = typeVar;
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
	public Set<IdentifierExpression> getIdentifierExpressions() {
		return expression.getIdentifierExpressions();
	}
	
	@Override
	public Set<Variable> getLVariables() {
		//because this can't happen on the left.
		Set<Variable> used = new HashSet<Variable>();
		return used;
	}

	@Override
	public Type getType() {
		return BoolType.instance();
	}
}
