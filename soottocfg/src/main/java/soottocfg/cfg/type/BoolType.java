/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 *
 */
public class BoolType extends PrimitiveType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8808194746696075445L;
	private static final BoolType instance = new BoolType();

	public static BoolType instance() {
		return instance;
	}

	/**
	 * 
	 */
	private BoolType() {
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return "boolean";
	}
}
