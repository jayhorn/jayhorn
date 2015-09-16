/**
 * 
 */
package jayhorn.soot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jayhorn.cfg.method.CfgBlock;
import jayhorn.soot.util.MethodInfo;
import jayhorn.soot.visitors.SootStmtSwitch;
import jayhorn.util.Log;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.TrapManager;
import soot.Unit;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.shimple.ShimpleFactory;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;

/**
 * This is the main class for the translation. It first invokes Soot to load all
 * classes and perform points-to analysis and then translates them into
 * Boogie/Horn.
 * 
 * @author schaef
 *
 */
public class SootToCfg {

	/**
	 * Run Soot and translate classes into Boogie/Horn
	 * 
	 * @param input
	 */
	public void run(String input) {

		// run soot to load all classes.
		SootRunner runner = new SootRunner();
		runner.run(input);

		for (SootClass sc : Scene.v().getClasses()) {
			processSootClass(sc);
		}

	}

	/**
	 * Analyze a single SootClass and transform all its Methods
	 * 
	 * @param sc
	 */
	private void processSootClass(SootClass sc) {
		if (sc.resolvingLevel() < SootClass.SIGNATURES) {
			return;
		}

		if (sc.isApplicationClass()) {
			Log.info("Class " + sc.getName() + "  " + sc.resolvingLevel());
			for (SootMethod sm : sc.getMethods()) {
				processSootMethod(sm);
			}
		}

	}

	private void processSootMethod(SootMethod sm) {
		if (sm.isConcrete()) {
			Log.info("\t" + sm.getName());
			processMethodBody(Shimple.v().newBody(sm.retrieveActiveBody()));
		}
	}

	private void processMethodBody(ShimpleBody body) {
		MethodInfo mi = new MethodInfo(body.getMethod());
		ShimpleFactory sf = soot.G.v().shimpleFactory;
		sf.setBody(body);
		BlockGraph bg = sf.getBlockGraph();
		Map<Block, SootStmtSwitch> subGraphs = new HashMap<Block, SootStmtSwitch>();
		for (Block b : bg.getBlocks()) {
			// first collect the traps and
			// exceptional successors for the block
			Set<Trap> traps = new HashSet<Trap>();
			Set<Block> exceptionalSuccessors = new HashSet<Block>();
			Iterator<Unit> iterator = b.iterator();
			//find traps
			while (iterator.hasNext()) {
				Unit u = iterator.next();
				traps.addAll(TrapManager.getTrapsAt(u, body));
			}
			//find successors that are traps
			for (Trap t : traps) {
				if (t.getHandlerUnit() == null) {
					throw new RuntimeException("No handler for trap");
				}
				for (Block s : b.getSuccs()) {
					if (s.getHead() == t.getHandlerUnit()) {
						exceptionalSuccessors.add(s);
					}
				}
			}

			// now do the actual translation
			SootStmtSwitch bss = new SootStmtSwitch(b, mi);
			subGraphs.put(b, bss);

			// connect the block to its non-exceptional successors
			if (bss.getExitBlock() != null) {
				for (Block s : b.getSuccs()) {
					if (!exceptionalSuccessors.contains(s)) {						
						bss.getExitBlock().addSuccessor(mi.lookupCfgBlock(s.getHead()));
					}
				}
			}

		}

		if (bg.getHeads().size() != 1) {
			throw new RuntimeException(
					"Never seen a procedure with two heads: "
							+ body.getMethod().getBytecodeSignature());
		} else {			
			CfgBlock root = subGraphs.get(bg.getHeads().get(0)).getEntryBlock();
			mi.setSource(root);
		}
		debugPrint(mi);
	}
	
	private void debugPrint(MethodInfo mi) {
		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		todo.add(mi.getSource());
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		while (!todo.isEmpty()) {
			CfgBlock current = todo.remove(0);
			done.add(current);
			System.err.println(current);
			for (CfgBlock succ : current.getSuccessors()) {
				if (!todo.contains(succ) && !done.contains(succ)) {
					todo.add(succ);
				}
			}
		}
	}

}
