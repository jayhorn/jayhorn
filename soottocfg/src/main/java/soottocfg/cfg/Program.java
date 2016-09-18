/**
 * 
 */
package soottocfg.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
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
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class Program {

	private final Map<String, Variable> globalVariables = new LinkedHashMap<String, Variable>();
	private final Map<String, Method> methods = new LinkedHashMap<String, Method>();

	private final Collection<Method> entryPoints = new HashSet<Method>();

	private Variable exceptionGlobal;
	
	private DirectedGraph<Method, DefaultEdge> callGraph;
	private final DirectedGraph<ClassVariable, DefaultEdge> typeGraph = new DefaultDirectedGraph<ClassVariable,DefaultEdge>(DefaultEdge.class);;
	private Map<Method, Set<Variable>> modifiedGlobals;
	
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
	
	public Variable lookupGlobalVariable(String varName, Type t) {
		return lookupGlobalVariable(varName, t, false, false);
	}

	public Variable lookupGlobalVariable(String varName, Type t, boolean constant, boolean unique) {
		if (!this.globalVariables.containsKey(varName)) {
			this.globalVariables.put(varName, new Variable(varName, t, constant, unique));
		}
		return this.globalVariables.get(varName);
	}

	public Method loopupMethod(String methodSignature) {		
		return methods.get(methodSignature);
	}
	
	public void addMethod(Method m) {
		//set the callGraph to null because it has to be recomputed.
		callGraph=null;
		modifiedGlobals=null;
		this.methods.put(m.getMethodName(), m);
	}
	
	public void removeMethods(Collection<Method> methods) {
		callGraph=null;
		modifiedGlobals=null;		
		for (Method m : methods) {
			this.methods.remove(m.getMethodName());
		}
	}

	public void addEntryPoint(Method entry) {
		entry.isProgramEntryPoint(true);
		this.entryPoints.add(entry);
	}

	public Method[] getEntryPoints() {
		return entryPoints.toArray(new Method[entryPoints.size()]);
	}

	public Method[] getMethods() {
		return methods.values().toArray(new Method[methods.size()]);
	}

	public Variable getExceptionGlobal() {
		Verify.verify(exceptionGlobal!=null, "Exception global never set");
		return exceptionGlobal;
	}
	
	public void setExceptionGlobal(Variable exGlobal) {
		Verify.verify(exceptionGlobal==null, "Do not set this variable twice");
		exceptionGlobal = exGlobal;
	}
	
	public DirectedGraph<Method,DefaultEdge> getCallGraph() {
		if (callGraph==null) {
			computeCallGraph();
		}
		return callGraph;
	}
	
	private void computeCallGraph() {
		callGraph = new DefaultDirectedGraph<Method,DefaultEdge>(DefaultEdge.class);
		for (Method m : methods.values()) {
			callGraph.addVertex(m);
		}
		for (Method m : methods.values()) {
			for (CfgBlock b : m.vertexSet()) {
				for (Statement s : b.getStatements()) {
					if (s instanceof CallStatement) {
						Method callee = ((CallStatement)s).getCallTarget();
						if (!callGraph.containsEdge(m, callee)) {
							callGraph.addEdge(m, callee);
						}
					}
				}
			}
		}
	}
	
	/**
	 * TODO:
	 * Very brute-force implementation to get the set of modified globals.
	 * @return
	 */
	public Map<Method, Set<Variable>> getModifiedGlobals() {
		if (modifiedGlobals!=null) {
			return modifiedGlobals;
		}
		DirectedGraph<Method,DefaultEdge> cg = getCallGraph();
		modifiedGlobals = new HashMap<Method, Set<Variable>>();
		for (Method m : cg.vertexSet()) {
			Set<Variable> initialSet = m.getDefVariables(); 
			initialSet.retainAll(this.globalVariables.values());
			modifiedGlobals.put(m, initialSet);			
		}
		boolean change = true;
		while (change) {
			change = false;
			for (Method m : cg.vertexSet()) {
				for (Method suc : Graphs.successorListOf(cg, m)) {
					if (modifiedGlobals.get(m).addAll(modifiedGlobals.get(suc))) {
						change = true;
					}
				}
			}
		}
		return modifiedGlobals;
	}
	
	public String toString() {
		StringBuilder prog = new StringBuilder();
		
		// global variables
		for (Variable g : globalVariables.values()) {
			prog.append(g+";\n");
		}
		prog.append("\n");
		
		// methods
		for (Method m : methods.values()) {
			prog.append(m+"\n");
		}
		
		return prog.toString();
	}
}
