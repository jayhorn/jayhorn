package soottocfg.cfg.type;

import java.util.HashSet;
import java.util.Set;

//import com.google.common.base.Verify;

import soottocfg.cfg.variable.ClassVariable;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class ReferenceType extends ReferenceLikeType {

	private static final long serialVersionUID = 4056715121602313972L;
	private final ClassVariable classVariable;

	private Set<Integer> pointsToSet = new HashSet<Integer>();
	
	public ReferenceType(ClassVariable var) {
		classVariable = var;
	}

	public ClassVariable getClassVariable() {
		return classVariable;
	}
	
	public Set<Integer> getPointsToSet() {
		return pointsToSet;
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
