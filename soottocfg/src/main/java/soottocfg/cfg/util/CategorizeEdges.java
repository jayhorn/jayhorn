/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;

/**
 * @author schaef
 *
 */
public class CategorizeEdges<V, E> {
	
	private final DirectedGraph<V, E> graph;
	private final Set<V> visited = new HashSet<V>();
	private final Tree<V> traversalTree = new Tree<V>(); 
	
	private final Set<E> forwardEdges = new HashSet<E>();
	private final Set<E> backwardEdges = new HashSet<E>();
	private final Set<E> crossEdges = new HashSet<E>();
	
	public CategorizeEdges(DirectedGraph<V, E> g, V root) {
		graph = g;
		dfs(root);
	}
	
	public Set<E> getForwardEdges() {
		return forwardEdges;
	}

	public Set<E> getBackwardEdges() {
		return backwardEdges;
	}

	public Set<E> getCrossEdges() {
		return crossEdges;
	}

	private void dfs(V vertex) {
		visited.add(vertex);
		for (V suc : Graphs.successorListOf(graph, vertex)) {
			if (!visited.contains(suc)) {
				traversalTree.addEdge(vertex, suc);
				dfs(suc);
			} else {
				if (traversalTree.isAncestor(vertex, suc)) {
					backwardEdges.add(graph.getEdge(vertex, suc));
				} else if (traversalTree.isDescendant(vertex, suc)) {
					forwardEdges.add(graph.getEdge(vertex, suc));
				} else {
					crossEdges.add(graph.getEdge(vertex, suc));
				}
			}
		}	
	}
	
}
