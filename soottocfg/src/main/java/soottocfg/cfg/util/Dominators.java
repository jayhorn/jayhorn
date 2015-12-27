/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

/**
 * @author dsn, schaef
 *
 */
public class Dominators<V, E> {

	private final DirectedGraph<V, E> graph;
	private Map<V, Set<V>> dominators = null, postDominators = null;
	private Map<V, V> iDominators = null, iPostDominators = null;

	/**
	 * For this to work correctly, g must not be modified.
	 * 
	 * @param g
	 */
	public Dominators(DirectedGraph<V, E> g) {
		graph = g;
	}
	
	/**
	 * For each vertex n compute the set of vertices that dominate n.
	 * Implemented using the algorithm in Aho 2nd ed, p 658.
	 * 
	 * @param source
	 *            The unique source of the graph.
	 * @return Map from vertices the their set of dominator vertices
	 */
	public Map<V, Set<V>> computeDominators(V source) {
		if (dominators == null) {
			dominators = computeDominators(source, true);
		}
		return dominators;
	}

	/**
	 * For each vertex n compute the set of vertices that post-dominate n.
	 * Implemented using the algorithm in Aho 2nd ed, p 658.
	 * 
	 * @param source
	 *            The unique sink of the graph.
	 * @return Map from vertices the their set of post-dominator vertices
	 */
	public Map<V, Set<V>> computePostDominators(V source) {
		if (postDominators == null) {
			postDominators = computeDominators(source, false);
		}
		return postDominators;
	}

	/**
	 * For each vertex n, compute the immediate dominator
	 * @param source The source of the graph
	 * @return Map from vertex to its idom
	 */
	public Map<V, V> computeImmediateDominators(V source) {
		if (iDominators == null) {
			iDominators = computeIDom(computeDominators(source));
		}
		return iDominators;
	}

	/**
	 * For each vertex n, compute the immediate post-dominator
	 * @param sink The sink of the graph
	 * @return Map from vertex to its iPostDom
	 */
	public Map<V, V> computeImmediatePostDominators(V sink) {
		if (iPostDominators == null) {
			iPostDominators = computeIDom(computePostDominators(sink));
		}
		return iPostDominators;
	}

	public Tree<V> computeDominatorTree(V source) {
		Tree<V> dominatorTree = new Tree<V>();
		for (Entry<V,V> entry : computeImmediateDominators(source).entrySet()) {
			dominatorTree.addEdge(entry.getValue(), entry.getKey());
		}
		return dominatorTree;
	}
	
	private Map<V, V> computeIDom(Map<V, Set<V>> dom) {
		Map<V, V> idominators = new HashMap<V, V>();
		for (Entry<V, Set<V>> entry : dom.entrySet()) {
			V idom = null;
			for (V dominator : entry.getValue()) {
				Set<V> dominatorDiff = new HashSet<V>(entry.getValue());
				dominatorDiff.removeAll(dom.get(dominator));
				if (dominatorDiff.size() == 1 && dominatorDiff.iterator().next() == entry.getKey()) {
					idom = dominator;
					break;
				}
			}
			if (idom != null) {
				idominators.put(entry.getKey(), idom);
			}
		}
		return idominators;
	}

	/**
	 * Computes the dominators or post-dominators for all vertices in a graph
	 * starting from the given source.
	 * 
	 * @param source
	 *            The source vertex to start from (should be either source or
	 *            sink)
	 * @param forward
	 *            true, for computing dominators; false, for computing
	 *            post-dominators
	 * @return
	 */
	protected Map<V, Set<V>> computeDominators(V source, boolean forward) {
		Preconditions.checkArgument(graph.containsVertex(source));
		Set<V> vertices = graph.vertexSet();
		Map<V, Set<V>> dominators = new LinkedHashMap<V, Set<V>>(vertices.size());

		// Initialize the set
		for (V b : vertices) {
			if (b == source) {
				// The Source node only dominates itself
				Set<V> tmp = new LinkedHashSet<V>();
				tmp.add(b);
				dominators.put(b, tmp);
			} else {
				// All other nodes are initialized to be the full graph. They
				// will shrink later
				Set<V> tmp = new LinkedHashSet<V>(vertices);
				dominators.put(b, tmp);
			}
		}

		boolean changed;
		do {
			changed = false;
			for (V b : vertices) {
				// Source node is always only dominated by itself.
				if (b != source) {
					Set<V> newDom = new HashSet<V>(vertices);
					// This is a bit ugly way to handle the initialization
					// of the intersection problem
					// but it should work
					if (forward) {
						Verify.verify(graph.inDegreeOf(b) != 0, "Unexpected indegree of 0");
						for (V inBlock : Graphs.predecessorListOf(graph, b)) {
							newDom.retainAll(dominators.get(inBlock));
						}
					} else {
						Verify.verify(graph.outDegreeOf(b) != 0, "Unexpected outdegree of 0");
						for (V inBlock : Graphs.successorListOf(graph, b)) {
							newDom.retainAll(dominators.get(inBlock));
						}
					}

					// every node dominates itself
					newDom.add(b);
					if (!newDom.equals(dominators.get(b))) {
						dominators.put(b, newDom);
						changed = true;
					}
				}
			}
		} while (changed);

		return dominators;
	}

}
