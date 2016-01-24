/**
 * 
 */
package jayhorn;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import jayhorn.checker.Checker;
import jayhorn.old_inconsistency_check.InconsistencyChecker;
import jayhorn.solver.princess.PrincessProverFactory;
import soottocfg.soot.SootToCfg;
import soottocfg.soot.SootToCfg.MemModel;

public class Main {

	public static void main(String[] args) {
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			
			if ("safety".equals(Options.v().getChecker())) {
				SootToCfg soot2cfg = new SootToCfg();
				soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());			
				Checker checker = new Checker(new PrincessProverFactory());
				boolean result = checker.checkProgram(soot2cfg.getProgram());
				System.out.println("checker says "+ result);		
			} else if ("inconsistency".equals(Options.v().getChecker())) {
				SootToCfg soot2cfg = new SootToCfg(false, true, MemModel.BurstallBornat);
				soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());			
				InconsistencyChecker checker = new InconsistencyChecker(new PrincessProverFactory());
				checker.checkProgram(soot2cfg.getProgram());				
			} else {
				Log.error(String.format("Checker %s is unknown", Options.v().getChecker()) );
			}
			
		} catch (CmdLineException e) {
			Log.error(e.toString());
			Log.error("java -jar joogie.jar [options...] arguments...");
			parser.printUsage(System.err);
		} catch (Throwable t) {
			Log.error(t.toString());
			throw t;
		} finally {
			Options.resetInstance();
			soot.G.reset();
		}
	}

}