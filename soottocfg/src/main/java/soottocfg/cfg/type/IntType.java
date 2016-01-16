/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 *
 */
public class IntType extends PrimitiveType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5047960968046256817L;
	private static final IntType instance = new IntType();

	public static IntType instance() {
		return instance;
	}

	/**
	 * 
	 */
	private IntType() {
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return "int";
	}
}
