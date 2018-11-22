/**
 * 
 */
package soottocfg.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class Program {

	private final Map<String, Variable> globalVariables = new LinkedHashMap<String, Variable>();
	private final Map<String, Method> methods = new LinkedHashMap<String, Method>();

        /**
         * Check whether this variable originated from some more complex expression that
         * could not be handled precise (i.e., whether something is over-approximated)
         */
        public static boolean isAbstractedVariable(Variable v) {
            return v.getName().startsWith(SootTranslationHelpers.AbstractedVariablePrefix);
        }

	private Method entryPoint;

	private DirectedGraph<Method, DefaultEdge> callGraph;
	private final DirectedGraph<ClassVariable, DefaultEdge> typeGraph = new DefaultDirectedGraph<ClassVariable, DefaultEdge>(
			DefaultEdge.class);;


	public void addClassVariable(ClassVariable cv) {
		if (!this.typeGraph.containsVertex(cv)) {
			this.typeGraph.addVertex(cv);
			for (ClassVariable parent : cv.getParents()) {
				addClassVariable(parent);
				this.typeGraph.addEdge(parent, cv);
			}
		}
	}

	public Set<ClassVariable> getClassVariables() {
		return this.typeGraph.vertexSet();
	}

	public ClassVariable findClassVariableByName(final String name) {
		for (ClassVariable cv : this.typeGraph.vertexSet()) {
			if (name.equals(cv.getName())) {
				return cv;
			}
		}
		return null;
	}

	public DirectedGraph<ClassVariable, DefaultEdge> getTypeGraph() {
		return this.typeGraph;
	}

	public Variable lookupGlobalVariable(String varName, Type t) {
		if (!this.globalVariables.containsKey(varName)) {
			this.globalVariables.put(varName, new Variable(varName, t, true, true));
		}
		return this.globalVariables.get(varName);
	}	


	public List<Variable> getGlobalVariables() {
		return new LinkedList<Variable>(this.globalVariables.values());
	}

	public Map<String, Variable> getGlobalsMap() {
		return this.globalVariables;
	}
	
	public Method lookupMethod(String methodSignature) {
		return methods.get(methodSignature);
	}

	public void addMethod(Method m) {
		// set the callGraph to null because it has to be recomputed.
		callGraph = null;
		this.methods.put(m.getMethodName(), m);
	}

	public void removeMethods(Collection<Method> methods) {
		callGraph = null;
		for (Method m : methods) {
			this.methods.remove(m.getMethodName());
		}
	}

	public void setEntryPoint(Method entry) {
		entryPoint = entry;
	}

	public Method getEntryPoint() {
		return entryPoint;
	}

	public Method[] getMethods() {
		return methods.values().toArray(new Method[methods.size()]);
	}

	public DirectedGraph<Method, DefaultEdge> getCallGraph() {
		if (callGraph == null) {
			computeCallGraph();
		}
		return callGraph;
	}

	private void computeCallGraph() {
		callGraph = new DefaultDirectedGraph<Method, DefaultEdge>(DefaultEdge.class);
		for (Method m : methods.values()) {
			callGraph.addVertex(m);
		}
		for (Method m : methods.values()) {
			for (CfgBlock b : m.vertexSet()) {
				for (Statement s : b.getStatements()) {
					if (s instanceof CallStatement) {
						Method callee = ((CallStatement) s).getCallTarget();
						if (!callGraph.containsEdge(m, callee)) {
							callGraph.addEdge(m, callee);
						}
					}
				}
			}
		}
	}

	public String toString() {
		StringBuilder prog = new StringBuilder();

		// global variables
		for (Variable g : globalVariables.values()) {
			prog.append(g + ";\n");
		}
		prog.append("\n");

		List<Method> sortedMethods = new LinkedList<Method>(methods.values());
		Collections.sort(sortedMethods, new Comparator<Method>() {
			@Override
			public int compare(Method o1, Method o2) {
				return o1.getMethodName().compareTo(o2.getMethodName());
			}
		});

		// methods
		for (Method m : sortedMethods) {
			prog.append(m + "\n");
		}

		return prog.toString();
	}
}
