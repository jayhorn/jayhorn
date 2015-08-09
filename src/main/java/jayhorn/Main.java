/**
 * 
 */
package jayhorn;

import java.io.File;

import jayhorn.soot.SootRunner;
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
			runConsoleMode();
		} catch (CmdLineException e) {
			Log.error(e.toString());
			Log.error("java -jar joogie.jar [options...] arguments...");
			parser.printUsage(System.err);
		}
	}

	/**
	 * Launches Joogie in console mode
	 */
	public static void runConsoleMode() {
		try {
			run(Options.v().getJavaInput(), Options.v().getSmtFile());
		} catch (Exception e) {
			Log.error(e.toString());
		}
	}

	public static void run(String input, String output) {
		try {
			Log.debug("Running Soot");
			runSoot(input, output);

		} catch (Exception e) {
			Log.error(e.toString());
			;
		} finally {
			Options.resetInstance();
			soot.G.reset();
		}
	}

	protected static void runSoot(String input, String output) {
		SootRunner sootRunner = new SootRunner();

		if (null == input || input.isEmpty()) {
			return;
		}

		if (input.endsWith(".jar")) {
			// run with JAR file
			sootRunner.runWithJar(input, output);
		} else if (input.endsWith(".apk")) {
			// run with Android file
			sootRunner.runWithApk(input, output);
		} else {
			File file = new File(input);
			if (file.isDirectory()) {
				sootRunner.runWithPath(input, output);
			} else {
				throw new RuntimeException("Don't know what to do with: "
						+ input);
			}
		}
	}

}