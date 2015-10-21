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
public class IntegerLiteral extends Expression {

	private final long value;
	
	public long getValue() {
		return value;
	}

	private static final IntegerLiteral one = new IntegerLiteral(1);
	private static final IntegerLiteral zero = new IntegerLiteral(0);
	private static final IntegerLiteral minusOne = new IntegerLiteral(-1);

	
	public static IntegerLiteral one() { 
		return one;
	}

	public static IntegerLiteral zero() {
		return zero;
	}

	public static IntegerLiteral minusOne() {
		return minusOne;
	}
	
	public IntegerLiteral(int value) {
		this.value = value;
	}
	
	public IntegerLiteral(long value) {
		this.value = value;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		return sb.toString();		
	}	
	
	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> used = new HashSet<Variable>();
		return used;
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
