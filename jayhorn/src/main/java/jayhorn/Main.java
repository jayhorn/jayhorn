/**
 * 
 */
package jayhorn;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import jayhorn.checker.Checker;
import soottocfg.soot.SootToCfg;

public class Main {

	public static void main(String[] args) {
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			SootToCfg soot2cfg = new SootToCfg();
			soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath(), Options.v().getCallGraphAlgorithm());
			
			Checker checker = new Checker();
			boolean result = checker.checkProgram(soot2cfg.getProgram());
			System.out.println("checker says "+ result);
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