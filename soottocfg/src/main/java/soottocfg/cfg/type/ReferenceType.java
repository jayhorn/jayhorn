/**
 * 
 */
package soottocfg.cfg.type;

import soottocfg.cfg.ClassVariable;

/**
 * @author schaef
 *
 */
public class ReferenceType extends ReferenceLikeType {

	private static final long serialVersionUID = 4056715121602313972L;
	private final ClassVariable classVariable;

	/**
	 * 
	 */
	public ReferenceType(ClassVariable var) {
		classVariable = var;
	}

	public String toString() {
		if (classVariable == null) {
			return "Null";
		} else {
			return "ref(" + classVariable.getName() + ")";
		}
	}

}
