/**
 * 
 */
package jayhorn.cfg.expression;

/**
 * @author schaef
 *
 */
public class IntegerLiteral extends Expression {

	public static IntegerLiteral one() {
		return new IntegerLiteral(1);
	}

	public static IntegerLiteral zero() {
		return new IntegerLiteral(0);
	}

	
	public IntegerLiteral(int value) {
		
	}
	
	public IntegerLiteral(long value) {
		
	}
	
}
