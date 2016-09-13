package jayhorn.hornify;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.variable.ClassVariable;

/**
 * This class assigns a unique number to every ClassType
 * in a program. This is used to implement instanceof checks:
 * for "a instanceof b" we obtain the list of IDs of all 
 * subtypes of "b" using getSubtypeIDs and check if any of 
 * these is equal to the ID of "a" that we obtain with 
 * "getTypeID".
 * 
 * Make sure that there exists no more than one instance of this per 
 * Prover.
 * @author schaef
 *
 */
public class ClassTypeEnumerator {

	private final Program program;

	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();

	public ClassTypeEnumerator(Program prog) {
		this.program = prog;
		for (ClassVariable var : program.getTypeGraph().vertexSet()) {
			typeIds.put(var, typeIds.size());
		}
	}

	public Integer getTypeID(ClassVariable var) {
		return typeIds.get(var);
	}

	public Set<Integer> getSubtypeIDs(ClassVariable var) {
		final Set<Integer> result = new HashSet<Integer>();
		for (ClassVariable v : GraphUtil.getForwardReachableVertices(program.getTypeGraph(), var)) {
			result.add(getTypeID(v));
		}
		return result;
	}

	public Map<ClassVariable, Integer> getTypeIds() {
		return typeIds;
	}
}
