/**
 * 
 */
package soottocfg.cfg.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graphs;
import org.jgrapht.util.VertexPair;

/**
 * @author schaef
 *
 */
public class LoopFinder<V> {
	
	private final Map<V, Set<V>> loops;	
	private final LoopNestedTreeSet loopNestTree;
	
	public LoopFinder(Dominators<V> dom) {
		Set<VertexPair<V>> backEdges = findBackEdges(dom);
		loops = findLoops(dom, backEdges);
		loopNestTree = new LoopNestedTreeSet();
		loopNestTree.addAll(getLoopHeaders());
	}
	
	/**
	 * Returns the set of loop headers.
	 * @return Set of loop headers.
	 */
	public Set<V> getLoopHeaders() {
		return loops.keySet();
	}
	
	/**
	 * Returns the loop body for a given loop header.
	 * @param header
	 * @return
	 */
	public Set<V> getLoopBody(V header) {
		return loops.get(header);
	}
		
	/**
	 * Returns a map from loop header to loop body for
	 * each loop in the graph.
	 * @return
	 */
	public Map<V, Set<V>> getLoops() {
		return new HashMap<V, Set<V>>(loops);
	}
	
	/**
	 * Returns a loop-nested TreeSet where each element is a loop header
	 * and first (lowest) elements are the inner-most loops.
	 * This contains less information than a proper loop-nested tree but 
	 * is sufficient to iterate over the loops from inner loops to outer
	 * loops.
	 * @return Loop-nested TreeSet.
	 */
	public LoopNestedTreeSet getLoopNestTreeSet() {
		return new LoopNestedTreeSet(loopNestTree);
	}

	/**
	 * Find the set of backEdges. A backEdge is an edge from a
	 * vertex n to a vertex h, s.t. h dominates n.
	 * @param dom
	 * @return Set of back edges as vertex pairs (n,h).
	 */
	private Set<VertexPair<V>> findBackEdges(Dominators<V> dom) {		
		Set<VertexPair<V>> backEdges = new HashSet<VertexPair<V>>();
		for (V node : dom.getGraph().vertexSet()) {
			for (V succ : Graphs.successorListOf(dom.getGraph(), node)) {
				if (dom.isDominatedBy(node, succ)) {					
					backEdges.add(new VertexPair<V>(node, succ));					
				}
			}
		}
		return backEdges;
	}

	/**
	 * Given a set of back edges, compute a mapping from the loop headers
	 * to the set of vertices in the loop bodies.
	 * @param dom The dominator class of a graph.
	 * @param backEdges Set of backEdges from a vertex n to a loop header h.
	 * @return Map from loop header h to the set of vertices in its loop body.
	 */
	private Map<V, Set<V>> findLoops(Dominators<V> dom, Set<VertexPair<V>> backEdges) {
		Map<V, Set<V>> res = new HashMap<V, Set<V>>();
		for (VertexPair<V> backEdge : backEdges) {
			if (!res.containsKey(backEdge.getSecond())) {
				res.put(backEdge.getSecond(), new HashSet<V>());
			}			
			res.get(backEdge.getSecond()).addAll(findNaturalLoop(dom, backEdge));
		}
		return res;
	}
	
	/**
	 * Find the natural loop for a given backEdge. 
	 * Given a back edge from a vertex n to a loop head h, the natural loop
	 * contains all vertices x, s.t. h dominates x and there is a path from
	 * x to n not containing h.
	 * Note that a natural loop does contain the loop header.
	 * @param dom The dominator class of a graph. 
	 * @param backEdge A backEdge from a vertex n to a loop header h.
	 * @return The set of vertices included in the natural loop of backEdge.
	 */
	private Set<V> findNaturalLoop(Dominators<V> dom, VertexPair<V> backEdge) {
		Set<V> loopBody = new HashSet<V>();
		V header = backEdge.getSecond();
		Queue<V> todo = new LinkedList<V>();
		todo.add(backEdge.getFirst());
		while (!todo.isEmpty()) {
			V current = todo.poll();
			loopBody.add(current);
			for (V pre : Graphs.predecessorListOf(dom.getGraph(), current)) {				
				if (!todo.contains(pre) && !loopBody.contains(pre) && dom.isDominatedBy(pre, header)) {
					todo.add(pre);
				}
			}
		}
		return loopBody;
	}
	
	private class LoopNestTreeComparator implements Comparator<V> {
		@Override
		public int compare(V a, V b) {
			if (a.equals(b)) {
				return 0;
			}
			if (getLoopBody(b).contains(a)) {
				return -1;
			}
			return 1; //not comparable
		}		
	}
	
	public class LoopNestedTreeSet extends TreeSet<V> {
		private static final long serialVersionUID = 5639463978704086555L;
		
		public LoopNestedTreeSet() {
			super(new LoopNestTreeComparator());
		}

		public LoopNestedTreeSet(Collection<V> vertices) {
			super(new LoopNestTreeComparator());
			addAll(vertices);
		}

	}
}
