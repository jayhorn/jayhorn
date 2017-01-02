/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;

/**
 * @author schaef
 * 'The dominance frontier of a node x is the set of all nodes w such that x
 *	dominates a predecessor of w, but does not strictly dominate w.'	
 * Algorithm from 
 * Modern Compiler Implementation in Java, Second Edition page 406
 * 
 */
public class DominanceFrontier<V> {

	private final DirectedGraph<V, ?> graph;
	private final Dominators<V> dominators;
	private final Map<V,Set<V>> dominanceFrontier = new HashMap<V, Set<V>>();
	private final Tree<V> tree;
	
	public DominanceFrontier(Dominators<V> dom) {
		dominators = dom;
		this.graph = dominators.getGraph();		
		tree = dominators.getDominatorTree();
		computeDominanceFrontier(tree.getRoot());
	}
	
	public Map<V,Set<V>> getDominanceFrontier() {
		return dominanceFrontier;
	}
	
	/**
	 * Algorithm from
	 * Modern Compiler Implementation in Java, Second Edition page 406
	 * computeDF[n] =
		S <- {}
		for each node y in succ[n]    //This loop computes DFlocal[n]
			if idom(y) != n
				S <- S \cup {y}
		for each child c of n in the dominator tree
			computeDF[c]
			for each element w of DF[c]     // This loop computes DFup[c]
				if n does not dominate w, or if n = w
				S <- S \cup {w}
		DF[n] <- S
	 * @param n Vertex for which we want to compute the dominance frontier.
	 * @return Set of vertices that comprise the dominance frontier of n.
	 */
	private void computeDominanceFrontier(V n) {
		Set<V> S = new HashSet<V>();
		for (V y : Graphs.successorListOf(graph, n)) {
			if (!n.equals(dominators.getImmediateDominator(y))) {
				S.add(y);
			}
		}
		for (V c : tree.getChildrenOf(n)) {
			computeDominanceFrontier(c);
			for (V w : dominanceFrontier.get(c)) {
				if (!dominators.isDominatedBy(w, n) || n.equals(w)) {
					S.add(w);
				}
			}
		}	
		dominanceFrontier.put(n, S);
	}
	
}
