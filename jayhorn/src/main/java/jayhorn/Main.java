/**
 * 
 */
package jayhorn;

import jayhorn.soot.SootToCfg;
import jayhorn.util.Log;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {

	public static void main(String[] args) {
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			SootToCfg soot2cfg = new SootToCfg();
			soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath(), Options.v().getCallGraphAlgorithm());
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