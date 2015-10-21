/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 *
 */
public class IntType extends Type {

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
