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

import com.google.common.base.Preconditions;

/**
 * @author schaef
 * 'The dominance frontier of a node x is the set of all nodes w such that x
 *	dominates a predecessor of w, but does not strictly dominate w.'	
 * Algorithm from 
 * Modern Compiler Implementation in Java, Second Edition page 406
 * 
 */
public class DominanceFrontier<V,E> {

	private final DirectedGraph<V, E> graph;
	private final V root;
	private final Dominators<V,E> dominators;
	private final Map<V,V> iDom;
	private final Map<V,Set<V>> dominanceFrontier = new HashMap<V, Set<V>>();
	private final Tree<V> tree;
	
	public DominanceFrontier(DirectedGraph<V, E> g, V root) {
		Preconditions.checkNotNull(root, "root must not be null");
		this.graph = g;
		this.root = root;
		dominators = new Dominators<V,E>(graph);
		iDom = dominators.computeImmediateDominators(root);
		tree = dominators.computeDominatorTree(root);
		computeDominanceFrontier(tree.getRoot());
	}
	
	public Map<V,Set<V>> getDominanceFrontier() {
		return dominanceFrontier;
	}
	
	/**
	 * Algorithm from
	 * Modern Compiler Implementation in Java, Second Edition page 406
	 * computeDF[n] =
		S ← {}
		for each node y in succ[n]    //This loop computes DFlocal[n]
			if idom(y) != n
				S ← S ∪ {y}
		for each child c of n in the dominator tree
			computeDF[c]
			for each element w of DF[c]     // This loop computes DFup[c]
				if n does not dominate w, or if n = w
				S ← S ∪ {w}
		DF[n] ← S
	 * @param n Vertex for which we want to compute the dominance frontier.
	 * @return Set of vertices that comprise the dominance frontier of n.
	 */
	private void computeDominanceFrontier(V n) {
		Set<V> S = new HashSet<V>();
		for (V y : Graphs.successorListOf(graph, n)) {
			if (iDom.containsKey(y) && !iDom.get(y).equals(n)) {
				S.add(y);
			}
		}
		for (V c : tree.getChildrenOf(n)) {
			computeDominanceFrontier(c);
			for (V w : dominanceFrontier.get(c)) {
				if (!dominators.computeDominators(root).get(w).contains(n) || n.equals(w)) {
					S.add(w);
				}
			}
		}	
		dominanceFrontier.put(n, S);
	}
	
}
