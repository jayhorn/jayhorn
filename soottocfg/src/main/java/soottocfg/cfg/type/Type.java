/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 * Note that Type is not abstract, so we can use it as wildcard type.
 */
public class Type {

	private static final Type instance = new Type();
	
	public static Type instance() {
		return instance;
	}
	
	protected Type() {
	}

}
