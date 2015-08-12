/**
 * 
 */
package jayhorn.soot;

import java.util.Iterator;

import jayhorn.Options;
import jayhorn.soot.SootRunner.CallgraphAlgorithm;
import jayhorn.soot.visitors.SootStmtSwitch;
import jayhorn.util.Log;
import soot.Body;
import soot.PointsToAnalysis;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;

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

		// TODO: store the points-to analysis and CG somewhere.
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		System.out.println("PTA " + pta);

		if (Options.v().getCallGraphAlgorithm() != CallgraphAlgorithm.None) {
			CallGraph cg = Scene.v().getCallGraph();
			System.out.println("CG " + cg);
		}

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
		Log.info("Class " + sc.getName() + "  " + sc.resolvingLevel());
		if (sc.resolvingLevel()<SootClass.SIGNATURES) {			
			return;
		}
		
		if (sc.isApplicationClass()) {
			for (SootMethod sm : sc.getMethods()) {
				processSootMethod(sm);
			}
		}

	}

	private void processSootMethod(SootMethod sm) {		
		if (sm.isConcrete()) {
			Log.info("\t"+sm.getName());
			transformStmtList(sm.retrieveActiveBody());
		}
	}

	/**
	 * Transforms a list of statements
	 * 
	 * @param body
	 *            Body
	 */
	private void transformStmtList(Body body) {

		ExceptionalUnitGraph tug = new ExceptionalUnitGraph(body,
				UnitThrowAnalysis.v());

		Iterator<Unit> stmtIt = tug.iterator();
		while (stmtIt.hasNext()) {
			Stmt s = (Stmt) stmtIt.next();

			SootStmtSwitch bss = new SootStmtSwitch();
			s.apply(bss);
		}
	}

}
