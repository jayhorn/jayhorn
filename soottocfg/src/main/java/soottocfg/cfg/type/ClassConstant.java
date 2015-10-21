/**
 * 
 */
package soottocfg.cfg.type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author schaef
 * 
 */
public class ClassConstant {

	private final String className;
	private final Set<ClassConstant> parentConstants;
	
	/**
	 * 
	 * @param name Name of the Java class.
	 * @param parents List of interfaces and super-classes.
	 */
	public ClassConstant(String name, Collection<ClassConstant> parents) {
		className = name;
		parentConstants = new HashSet<ClassConstant>();
		parentConstants.addAll(parentConstants);
	}

	public String getName() {
		return className;
	}
	
	public Collection<ClassConstant> getParents() {
		return parentConstants;
	}
	
}
