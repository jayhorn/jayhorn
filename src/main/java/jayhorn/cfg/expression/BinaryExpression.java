/**
 * 
 */
package jayhorn.cfg.expression;

/**
 * @author schaef
 *
 */
public class BinaryExpression extends Expression {

	public enum BinaryOperator {
		Plus, Minus, Mul, Div, Mod,
		And, Or, Xor, Implies,
		Eq, Ne, Gt, Ge, Lt, Le,
		Shl, Shr, Ushr, BOr, BAnd
	}
	
	private final Expression left, right;
	private final BinaryOperator op;
	
	public BinaryExpression(BinaryOperator op, Expression left, Expression right) {
		this.left = left;
		this.right = right;
		this.op = op;
	}
	
	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	public BinaryOperator getOp() {
		return op;
	}

	
}
