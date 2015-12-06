/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Variable;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 * 
 */
public class ArrayLengthExpression extends Expression {

	private static final long serialVersionUID = 8363741748130907850L;
	
	private final Expression expression;
	
	public ArrayLengthExpression(Expression inner) {
		this.expression = inner;
	}
	
	public Expression getExpression() {
		return expression;
	}


	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("lengthof(");		
		sb.append(this.expression);
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
		return IntType.instance();
	}

}
