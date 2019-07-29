/**
 * 
 */
package soottocfg.cfg.expression.literal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class BooleanLiteral extends Expression implements Literal{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8808519364513150612L;

	private final boolean value;

	public static BooleanLiteral trueLiteral() {
		return new BooleanLiteral(null, true);
	}

	public static BooleanLiteral falseLiteral() {
		return new BooleanLiteral(null, false);
	}

	public BooleanLiteral(SourceLocation loc, boolean value) {
		super(loc);
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
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		return new HashSet<IdentifierExpression>();
	}

	@Override
	public Set<Variable> getDefVariables() {
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
			return ((BooleanLiteral) other).getValue() == this.value;
		}
		return false;
	}

	@Override
	public int hashCode() {
		// same hash code as Boolean.hashCode
		return value ? 1231 : 1237;
	}

	@Override
	public Expression substitute(Map<Variable, Variable> subs) {
		return this;
	}

	@Override
	public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
		return this;
	}

}
