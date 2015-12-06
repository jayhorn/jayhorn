/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

/**
 * @author dsn, schaef
 *
 */
public class Dominators<A, B extends DefaultEdge> {

	private final DirectedGraph<A, B> graph;
	// private final A source, sink;


	/**
	 * For this to work correctly, g must not be modified.
	 * @param g
	 */
	public Dominators(DirectedGraph<A, B> g) {
		graph = g;
	}

	/**
	 * For each vertex n compute the set of vertices that dominate n.
	 * Implemented using the algorithm in Aho 2nd ed, p 658.
	 * @param source The unique source of the graph.
	 * @return Map from vertices the their set of dominator vertices
	 */
	public Map<A, Set<A>> computeDominators(A source) {
		return computeDominators(source, true);
	}

	/**
	 * For each vertex n compute the set of vertices that post-dominate n.
	 * Implemented using the algorithm in Aho 2nd ed, p 658.
	 * @param source The unique sink of the graph.
	 * @return Map from vertices the their set of post-dominator vertices
	 */
	public Map<A, Set<A>> computePostDominators(A source) {
		return computeDominators(source, false);
	}

	
	/**
	 * Computes the dominators or post-dominators for all vertices in a graph
	 * starting from the given source.
	 * @param source The source vertex to start from (should be either source or sink)
	 * @param forward true, for computing dominators; false, for computing post-dominators
	 * @return
	 */
	protected Map<A, Set<A>> computeDominators(A source, boolean forward) {
		Preconditions.checkArgument(graph.containsVertex(source));
		Set<A> vertices = graph.vertexSet();
		Map<A, Set<A>> dominators = new LinkedHashMap<A, Set<A>>(vertices.size());

		// Initialize the set
		for (A b : vertices) {
			if (b == source) {
				// The Source node only dominates itself
				Set<A> tmp = new LinkedHashSet<A>();
				tmp.add(b);
				dominators.put(b, tmp);
			} else {
				// All other nodes are initialized to be the full graph. They
				// will shrink later
				Set<A> tmp = new LinkedHashSet<A>(vertices);
				dominators.put(b, tmp);
			}
		}

		boolean changed;
		do {
			changed = false;
			for (A b : vertices) {
				// Source node is always only dominated by itself.
				if (b != source) {
					Set<A> newDom = new HashSet<A>(vertices);
					// This is a bit ugly way to handle the initialization
					// of the intersection problem
					// but it should work
					if (forward) {
						Verify.verify(graph.inDegreeOf(b) != 0, "Unexpected indegree of 0");						
						for (A inBlock : Graphs.predecessorListOf(graph, b)) {
							newDom.retainAll(dominators.get(inBlock));
						}
					} else {
						Verify.verify(graph.outDegreeOf(b) != 0, "Unexpected outdegree of 0");
						for (A inBlock : Graphs.successorListOf(graph, b)) {
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
