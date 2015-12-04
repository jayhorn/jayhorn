/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.CycleDetector;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.util.UnreachableNodeRemover;

/**
 * @author schaef
 *
 */
public class LoopRemoval {

	private final Method method;
	/**
	 * 
	 */
	public LoopRemoval(Method m) {
		method = m;
	}

	public void removeLoops() {
		CycleDetector<CfgBlock, CfgEdge> cycles = new CycleDetector<CfgBlock, CfgEdge>(method);
		Set<CfgBlock> blocksInCycles = cycles.findCycles();
		Set<CfgBlock> loopHeads = new HashSet<CfgBlock>();
		if (!blocksInCycles.isEmpty()) {
//			System.err.print("Has cycles ");
			for (CfgBlock b : blocksInCycles) {
//				System.err.print(b.getLabel() + ", ");
				boolean isLoopHead = false;
				for (CfgBlock pre: method.getPredsOf(b)) {
					if (!blocksInCycles.contains(pre)) {
						isLoopHead = true;
						break;
					}
				}
				if (isLoopHead) {
					loopHeads.add(b);
				}
			}
//			System.err.println();

//			System.err.print("Loop heads ");
//			for (CfgBlock b : loopHeads) {
//				System.err.print(b.getLabel() + ", ");
//			}
//			System.err.println();

			// TODO: simple hack where we just cut off the back edges
			Set<CfgEdge> edgesToRemove = new HashSet<CfgEdge>();
			for (CfgBlock b : loopHeads) {
				Set<CfgBlock> loopExits = new HashSet<CfgBlock>();
				for (CfgBlock succ : method.getSuccsOf(b)) {
					if (!blocksInCycles.contains(succ)) {
						loopExits.add(succ);
					}
				}
//				System.err.println("loop exits " + loopExits.size());

				for (CfgBlock pre: method.getPredsOf(b)) {
					if (blocksInCycles.contains(pre)) {
						edgesToRemove.add(method.getEdge(pre, b));
						for (CfgBlock le : loopExits) {
							if (!method.containsEdge(pre, le)) {
								// redirect the back edge of the loop to the
								// exits
								//TODO: add havoc statements for
								//all variables that have changed in the loop
								method.addEdge(pre, le);
							}
						}
					}
				}
			}
			method.removeAllEdges(edgesToRemove);
			UnreachableNodeRemover<CfgBlock, CfgEdge> remover = new UnreachableNodeRemover<CfgBlock, CfgEdge>(method,
					method.getSource(), method.getSink());
			remover.removeDangelingPaths();
		}
	}
}
