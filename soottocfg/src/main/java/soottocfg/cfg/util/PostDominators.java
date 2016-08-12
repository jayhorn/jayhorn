/**
 * 
 */
package soottocfg.cfg.util;

import org.jgrapht.DirectedGraph;

/**
 * @author schaef
 *
 */
public class PostDominators<V> extends AbstractDominators<V> {

	public PostDominators(DirectedGraph<V, ?> g, V sink) {
		super(g);		
		dominators = computeDominators(sink, false);
		iDominators = computeImmediateDominators(dominators);
		dominatorTree = computeDominatorTree(iDominators);
	}

}
