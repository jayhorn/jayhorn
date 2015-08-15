/**
 * 
 */
package jayhorn.cfg.expression;

/**
 * @author schaef
 *
 */
public class UnaryExpression extends Expression {

	public enum UnaryOperator {
		Neg, LNot
	}
	
	private final Expression expression;
	private final UnaryOperator op;
	
	public UnaryExpression(UnaryOperator op, Expression inner) {
		this.expression = inner;
		this.op = op;
	}
	
	public Expression getExpression() {
		return expression;
	}

	public UnaryOperator getOp() {
		return op;
	}

	
}
