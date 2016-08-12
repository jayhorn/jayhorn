/**
 * 
 */
package soottocfg.cfg.util;

import org.jgrapht.DirectedGraph;

/**
 * @author schaef
 *
 */
public class Dominators<V> extends AbstractDominators<V> {
	
	public Dominators(DirectedGraph<V, ?> g, V source) {
		super(g);
		dominators = computeDominators(source, true);
		iDominators = computeImmediateDominators(dominators);
		dominatorTree = computeDominatorTree(iDominators);
	}
}
