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
 * @author schaef
 *
 */
public abstract class AbstractDominators<V> {

	private final DirectedGraph<V, ?> graph;
	
	public AbstractDominators(DirectedGraph<V, ?> g) {
		graph = g;
	}
	
	/**
	 * Get the directed graph for which the dominators are computed.
	 * @return
	 */
	public DirectedGraph<V, ?> getGraph() {
		return graph;
	}
	
	/**
	 * Check if node is dominated by dominator 
	 * @param node
	 * @param dominator
	 * @return
	 */
	public abstract boolean isDominatedBy(V node, V dominator);
	
	/**
	 * Get the immediate dominator for node
	 * @param node
	 * @return The immediate dominator of node, or null if node has no dominator.
	 */
	public abstract V getImmediateDominator(V node);
	
	/**
	 * Get the dominators for node
	 * @param node
	 * @return
	 */
	public abstract Set<V> getDominators(V node);

	/**
	 * Get the mapping from vertex to its set of dominators
	 * @param node
	 * @return
	 */
	public abstract Map<V, Set<V>> getDominators();

	
	/**
	 * Get the dominator tree of the graph.
	 * @return
	 */
	public abstract Tree<V> getDominatorTree();
	
	/**
	 * Compute the immediate (post)dominator for each vertex. 
	 * @param dom Mapping from each vertex to its set of (post)dominators.
	 * @return Map from vertex to its immediate (post)dominator, or null if 
	 *         the vertex does not have an immediate (post)dominator
	 */
	protected Map<V, V> computeImmediateDominators(Map<V, Set<V>> dom) {
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
			idominators.put(entry.getKey(), idom);
		}
		return idominators;
	}

	/**
	 * Computes the (post)dominators for all vertices in a graph
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
	
	/**
	 * Creates a (post)dominator tree from a given map.
	 * @param immediateDominators Mapping from vertex to its immediate (post)dominator.
	 * @return A (post)dominator tree.
	 */
	protected Tree<V> computeDominatorTree(Map<V, V> immediateDominators) {
		Tree<V> dominatorTree = new Tree<V>();
		for (Entry<V,V> entry : immediateDominators.entrySet()) {
			if (entry.getValue()!=null) {
				dominatorTree.addEdge(entry.getValue(), entry.getKey());
			}
		}
		return dominatorTree;
	}

}
