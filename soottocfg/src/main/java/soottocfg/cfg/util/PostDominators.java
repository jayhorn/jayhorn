/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;

/**
 * @author schaef
 *
 */
public class PostDominators<V> extends AbstractDominators<V> {

	private final Map<V, Set<V>> postDominators;
	private final Map<V, V> iPostDominators;
	private final Tree<V> postDominatorTree;

	
	public PostDominators(DirectedGraph<V, ?> g, V sink) {
		super(g);		
		postDominators = computeDominators(sink, false);
		iPostDominators = computeImmediateDominators(postDominators);
		postDominatorTree = computeDominatorTree(iPostDominators);		
	}

	@Override
	public boolean isDominatedBy(V node, V dominator) {
		if (!postDominators.containsKey(node)) {
			throw new IllegalArgumentException("Node is not part of the graph: "+node);
		}
		if (!postDominators.containsKey(dominator)) {
			throw new IllegalArgumentException("Node is not part of the graph: "+node);
		}

		return postDominators.get(node).contains(dominator);
	}

	@Override
	public V getImmediateDominator(V node) {
		if (!iPostDominators.containsKey(node)) {
			throw new IllegalArgumentException("Node is not part of the graph: "+node);
		}
		return iPostDominators.get(node);
	}

	@Override
	public Set<V> getDominators(V node) {
		if (!postDominators.containsKey(node)) {
			throw new IllegalArgumentException("Node is not part of the graph: "+node);
		}
		return new HashSet<V>(postDominators.get(node));
	}

	@Override
	public Map<V, Set<V>> getDominators() {
		return new HashMap<V, Set<V>>(postDominators);
	}
	
	@Override
	public Tree<V> getDominatorTree() {
		return postDominatorTree;
	}

}
