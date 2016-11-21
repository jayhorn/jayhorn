/**
 * 
 */
package soottocfg.cfg;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class Program {

	private final Map<String, Variable> globalVariables = new LinkedHashMap<String, Variable>();
	private final Map<String, Method> methods = new LinkedHashMap<String, Method>();

	private Method entryPoint;

	private Variable exceptionGlobal;

	private DirectedGraph<Method, DefaultEdge> callGraph;
	private final DirectedGraph<ClassVariable, DefaultEdge> typeGraph = new DefaultDirectedGraph<ClassVariable, DefaultEdge>(
			DefaultEdge.class);;

	public Variable[] getGlobalVariables() {
		return this.globalVariables.values().toArray(new Variable[this.globalVariables.size()]);
	}

	public Map<String, Variable> getGlobalsMap() {
		return this.globalVariables;
	}

	public Variable createFreshGlobal(String prefix, Type t) {
		final String vname = prefix + this.globalVariables.size();
		Preconditions.checkArgument(!globalVariables.containsKey(vname));
		Variable v = new Variable(vname, t);
		this.globalVariables.put(vname, v);
		return v;
	}

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

	/**
	 * Adds a field v to class cv and then traverses
	 * the class hierarchy and adds v to all children
	 * of cv at the same position where v was inserted
	 * in the super class ... this ensures that subtyping
	 * still works.
	 * @param cv
	 * @param v
	 * @return
	 */
	public int safelyInsertField(ClassVariable cv, Variable v) {		
		Verify.verify(!cv.hasField(v.getName()),
				String.format("%s already contains field named %s", cv.getName(), v.getName()));

		int pos = cv.getAssociatedFields().length;
		cv.addGhostField(v);
		
		List<ClassVariable> todo = new LinkedList<ClassVariable>();
		Set<ClassVariable> done = new HashSet<ClassVariable>();
		todo.addAll(Graphs.successorListOf(typeGraph, cv));
		while (!todo.isEmpty()) {
			cv = todo.remove(0);
			cv.addGhostFieldAtPos(v, pos);
			done.add(cv);
			for (ClassVariable suc : Graphs.successorListOf(typeGraph, cv)) {
				if (!todo.contains(suc) && !done.contains(suc)) {
					todo.add(suc);
				}
			}
		}
		return pos;
	}

	public int safelyInsertFieldIntoRootClass(Variable v) {
		int pos = -1;
		for (ClassVariable cv : GraphUtil.getSources(this.getTypeGraph())) {
			//TODO: hacky.
			int tmp = safelyInsertField(cv, v);
			if (cv.getName().endsWith("java/lang/Object")) {
				pos = tmp;
			}
		}
		return pos;
	}

	public Variable lookupGlobalVariable(String varName, Type t) {
		return lookupGlobalVariable(varName, t, false, false);
	}

	public Variable lookupGlobalVariable(String varName, Type t, boolean constant, boolean unique) {
		if (!this.globalVariables.containsKey(varName)) {
			this.globalVariables.put(varName, new Variable(varName, t, constant, unique));
		}
		return this.globalVariables.get(varName);
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

	public Variable getExceptionGlobal() {
		Verify.verify(exceptionGlobal != null, "Exception global never set");
		return exceptionGlobal;
	}

	public void setExceptionGlobal(Variable exGlobal) {
		Verify.verify(exceptionGlobal == null, "Do not set this variable twice");
		exceptionGlobal = exGlobal;
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
