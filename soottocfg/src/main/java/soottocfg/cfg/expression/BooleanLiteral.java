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
public class BooleanLiteral extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8808519364513150612L;
	
	private final boolean value;

	public static BooleanLiteral trueLiteral() {
		return new BooleanLiteral(true);
	}

	public static BooleanLiteral falseLiteral() {
		return new BooleanLiteral(false);
	}

	public BooleanLiteral(boolean value) {
		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		return sb.toString();
	}
	
	@Override
	public Set<IdentifierExpression> getIdentifierExpressions() {
		return new HashSet<IdentifierExpression>();
	}
	
	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		return used;
	}

	@Override
	public Type getType() {
		return BoolType.instance();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof BooleanLiteral) {
			return ((BooleanLiteral)other).getValue()==this.value;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		//same hash code as Boolean.hashCode 
		return value ? 1231 : 1237;
	}
}
