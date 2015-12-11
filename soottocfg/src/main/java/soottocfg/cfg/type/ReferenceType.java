/**
 * 
 */
package soottocfg.cfg.type;

/**
 * @author schaef
 *
 */
public class ReferenceType extends Type {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4056715121602313972L;
	private final ClassSignature classConstant;

	/**
	 * 
	 */
	public ReferenceType(ClassSignature cc) {
		classConstant = cc;
	}

	public String toString() {
		if (classConstant == null) {
			return "Null";
		} else {
			return "ref(" + classConstant.getName() + ")";
		}
	}

}
