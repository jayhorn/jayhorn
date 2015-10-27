/**
 * 
 */
package soottocfg.cfg.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.Variable;

/**
 * @author schaef
 * 
 */
public class ClassConstant {

	private final String className;
	private final Set<ClassConstant> parentConstants;
	private List<Variable> associatedFields;

	public ClassConstant(String name, Collection<ClassConstant> parents) {
		className = name;
		parentConstants = new HashSet<ClassConstant>();
		parentConstants.addAll(parentConstants);
		associatedFields = new LinkedList<Variable>();	
	}

	
	public String getName() {
		return className;
	}
	
	public Collection<ClassConstant> getParents() {
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
