/**
 * 
 */
package soottocfg.cfg.type;

import soottocfg.cfg.variable.ClassVariable;

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

	public ClassVariable getClassVariable() {
		return classVariable;
	}
	
	public String toString() {
		if (classVariable == null) {
			return "Null";
		} else {
//			return "ref(" + classVariable.getName() + ")";
			return classVariable.getName().replace('/','.');
		}
	}

}
