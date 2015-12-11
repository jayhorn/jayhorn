/**
 * 
 */
package soottocfg.cfg.type;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.Variable;

/**
 * @author schaef TODO ... this surely should be a subtype of something.
 */
public class ClassSignature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1647842783828780974L;
	private final String className;
	private final Set<ClassSignature> parentConstants;
	private List<Variable> associatedFields;

	public ClassSignature(String name, Collection<ClassSignature> parents) {
		className = name;
		parentConstants = new HashSet<ClassSignature>();
		parentConstants.addAll(parentConstants);
		associatedFields = new LinkedList<Variable>();
	}

	public String getName() {
		return className;
	}

	public Collection<ClassSignature> getParents() {
		return parentConstants;
	}

	public void setAssociatedFields(List<Variable> fields) {
		associatedFields = new LinkedList<Variable>();
		associatedFields.addAll(fields);
	}

	public Variable[] getAssociatedFields() {
		return associatedFields.toArray(new Variable[associatedFields.size()]);
	}

}
