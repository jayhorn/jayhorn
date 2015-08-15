/**
 * 
 */
package jayhorn.soot;

import java.util.Iterator;

import jayhorn.soot.memory_model.MemoryModel;
import jayhorn.soot.util.MethodInfo;
import jayhorn.soot.util.SootKitchenSink;
import jayhorn.soot.visitors.SootStmtSwitch;
import jayhorn.util.Log;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
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
		if (sc.resolvingLevel()<SootClass.SIGNATURES) {			
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

		SootKitchenSink.v().currentMethod = new MethodInfo(body.getMethod());
		
		ExceptionalUnitGraph tug = new ExceptionalUnitGraph(body,
				UnitThrowAnalysis.v());
		Iterator<Unit> stmtIt = tug.iterator();
		
		
		MemoryModel mm = new MemoryModel(); 
		SootStmtSwitch bss = new SootStmtSwitch(body.getMethod(), mm);
		
		
		while (stmtIt.hasNext()) {
			Unit u = stmtIt.next();
			((Stmt)u).apply(bss);
		}
	}

}
