/**
 * 
 */
package jayhorn;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import jayhorn.checker.Checker;
import jayhorn.old_inconsistency_check.InconsistencyChecker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.z3.Z3ProverFactory;
import soottocfg.soot.SootToCfg;
import soottocfg.soot.SootToCfg.MemModel;

public class Main {
	

	public static void main(String[] args) {
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			ProverFactory factory = null;
			if ("z3".equals(Options.v().getSolver())) {
				factory = new Z3ProverFactory();
			} else if ("princess".equals(Options.v().getSolver())) {
				factory = new PrincessProverFactory();
			} else {
				throw new RuntimeException("Don't know solver " + Options.v().getSolver() + ". Using Princess instead.");
			}
			
			if ("safety".equals(Options.v().getChecker())) {
				System.out.println("\t\t ---   JAYHORN : Static Analayzer for Java Programs ---- ");
				System.out.println("\t Build CFG  ... " + Options.v().getJavaInput());
				System.out.println( "\t \t  ----------- \n");
				SootToCfg soot2cfg = new SootToCfg(true, false, MemModel.PackUnpack);
				soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());			
				Checker checker = new Checker(factory);
				System.out.println( "\t \t  ----------- ");
				System.out.println("\t Hornify and check  ... " + Options.v().getJavaInput());
				System.out.println( "\t \t  ----------- \n");
				boolean result = checker.checkProgram(soot2cfg.getProgram());
				System.out.println( "\t \t  ----------- \n");
				if (!result){
					System.out.println("\t SAFETY VERIFICATION RESULT ... UNSAFE");
				} else {
				   System.out.println("\tSAFETY VERIFICATION RESULT ... SAFE");
				}
						
			} else if ("inconsistency".equals(Options.v().getChecker())) {
				SootToCfg soot2cfg = new SootToCfg(false, true, MemModel.BurstallBornat);
				soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());			
				InconsistencyChecker checker = new InconsistencyChecker(factory);
				checker.checkProgram(soot2cfg.getProgram());				
			} else {
				Log.error(String.format("Checker %s is unknown", Options.v().getChecker()) );
			}
			
		} catch (CmdLineException e) {
			Log.error(e.toString());
			Log.error("java -jar jayhorn.jar [options...] -j [JAR, DIR]");
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