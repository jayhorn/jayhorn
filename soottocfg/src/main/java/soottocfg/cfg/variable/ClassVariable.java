/**
 * 
 */
package soottocfg.cfg.variable;

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
		parentConstants.addAll(parents);
		associatedFields = new LinkedList<Variable>();
		//add all fields from the super class
		for (ClassVariable parent : parents) {
			for (Variable pfield : parent.getAssociatedFields()) {
				if (!hasField(pfield.getName())) {
					associatedFields.add(new Variable(pfield.getName(), pfield.getType()));
				}
			}
		}
	}

	public String getName() {
		return this.variableName;
	}

	public Collection<ClassVariable> getParents() {
		return Collections.unmodifiableCollection(parentConstants);
	}

	public void addFields(List<Variable> fields) {
		for (Variable f : fields) {
			if (!hasField(f.getName())) {
				associatedFields.add(new Variable(f.getName(), f.getType()));
			} else {
				//warn about that.
			}
		}
	}

	public Variable[] getAssociatedFields() {
		return associatedFields.toArray(new Variable[associatedFields.size()]);
	}

	public void addGhostField(Variable gf) {
		associatedFields.add(associatedFields.size(), gf);
	}
	
	public void addGhostFieldAtPos(Variable gf, int pos) {
		// TODO handle this nicely
		//@Rody, is that what you had in mind?
		if (this.hasField(gf.getName())) {
			throw new RuntimeException("Cannot add ghostfield, already exists: " + gf.getName());
		}
		associatedFields.add(pos, gf);		
	}
	
	
	public boolean hasField(String fname) {
		for (Variable v : associatedFields) {			
			if (v.getName().equals(fname)) {
				return true;
			}
		}
		return false;
	}
	
	public int findField(String fname) {
		for (int i=0; i<this.associatedFields.size();i++) {
			if (associatedFields.get(i).getName().equals(fname)) {
				return i;
			}			
		}
		throw new RuntimeException("Field not found "+fname);
	}
	
	@Override
	public String toString() {
		return this.variableName;
	}
	
	public boolean subclassOf(ClassVariable cls) {
		List<ClassVariable> todo = new LinkedList<ClassVariable>();
		todo.add(this);
		while (!todo.isEmpty()) {
			ClassVariable cv = todo.remove(0);
			if (cv==cls) return true;
			if (cv.getParents()!=null) {
				todo.addAll(cv.getParents());
			}
		}
		return false;
	}
	
	public boolean superclassOf(ClassVariable cls) {
		return cls.subclassOf(this);
	}
}
