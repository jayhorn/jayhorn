/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class IntegerLiteral extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7913206010686231183L;

	private final Long value;

	private static final IntegerLiteral one = new IntegerLiteral(null, 1);
	private static final IntegerLiteral zero = new IntegerLiteral(null, 0);
	private static final IntegerLiteral minusOne = new IntegerLiteral(null, -1);

	public static IntegerLiteral one() {
		return one;
	}

	public static IntegerLiteral zero() {
		return zero;
	}

	public static IntegerLiteral minusOne() {
		return minusOne;
	}

	public IntegerLiteral(SourceLocation loc, int value) {
		super(loc);
		this.value = Long.valueOf(value);
	}

	public IntegerLiteral(SourceLocation loc, long value) {
		super(loc);
		this.value = value;
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
		// because this can't happen on the left.
		Set<Variable> used = new HashSet<Variable>();
		return used;
	}

	public Long getValue() {
		return value;
	}

	@Override
	public Type getType() {
		return IntType.instance();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof IntegerLiteral) {
			return ((IntegerLiteral) other).getValue().equals(this.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
