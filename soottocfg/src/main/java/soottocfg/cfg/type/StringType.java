/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 *
 */
public class StringType extends PrimitiveType {

	/**
	 *
	 */
	// TODO: This is a random number distinct from other serialVersionUIDs. Is this the intended value?
	private static final long serialVersionUID = -5047164968046256817L;
	private static final StringType instance = new StringType();

	public static StringType instance() {
		return instance;
	}

	/**
	 *
	 */
	private StringType() {
		// TODO Auto-generated constructor stub
	}

	public String toString() {
		return "List[Int]";
	}
}
