/**
 * 
 */
package soottocfg.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.type.ReferenceType;

/**
 * @author schaef
 * Global variable to present a Java class (e.g, String). 
 */
public class ClassVariable extends Variable  {

	private static final long serialVersionUID = -1647842783828780974L;	
	private final Set<ClassVariable> parentConstants;
	private List<Variable> associatedFields;

	public ClassVariable(String name, Collection<ClassVariable> parents) {
		super(name, new ReferenceType(null), true, true); //TODO, its actually not reference type.	
		parentConstants = new HashSet<ClassVariable>();
		parentConstants.addAll(parentConstants);
		associatedFields = new LinkedList<Variable>();
	}

	public String getName() {
		return this.variableName;
	}

	public Collection<ClassVariable> getParents() {
		return Collections.unmodifiableCollection(parentConstants);
	}

	public void setAssociatedFields(List<Variable> fields) {
		associatedFields = new LinkedList<Variable>();
		associatedFields.addAll(fields);
	}

	public Variable[] getAssociatedFields() {
		return associatedFields.toArray(new Variable[associatedFields.size()]);
	}

	@Override
	public String toString() {
		return this.variableName;
	}
}
