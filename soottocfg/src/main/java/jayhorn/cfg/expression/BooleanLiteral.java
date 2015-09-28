/**
 * 
 */
package jayhorn.cfg.expression;

/**
 * @author schaef
 *
 */
public class BooleanLiteral extends Expression {

	private boolean value;

	public static BooleanLiteral trueLiteral() {
		return new BooleanLiteral(true);
	}

	public static BooleanLiteral falseLiteral() {
		return new BooleanLiteral(false);
	}

	public BooleanLiteral(boolean value) {
		this.value = value;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(value);
		return sb.toString();
	}
}
