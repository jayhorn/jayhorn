/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 *
 */
public class ReferenceType extends Type {

	private final ClassConstant classConstant;
	
	/**
	 * 
	 */
	public ReferenceType(ClassConstant cc) {
		classConstant = cc;
	}
	
	
	public String toString() {
		if (classConstant == null) {
			return "Null";
		} else {
			return "ref("+classConstant.getName()+")";
		}
	}

}
