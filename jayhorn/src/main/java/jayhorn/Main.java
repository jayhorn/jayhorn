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
	
    private static String parseResult(String solver, boolean result)
    {
    	if (result){
			return "SAFE";
		}else{
			return "UNSAFE";
		}
//    	switch (solver){
//    	case "z3":
//    		if (result){
//    			return "UNSAFE";
//    		}else{
//    			return "SAFE";
//    		}
//    	case "princess":
//    		if (result){
//    			return "SAFE";
//    		}else{
//    			return "UNSAFE";
//    		}
//    		default:
//    			return "UNKNOWN";
//    	}
    }
    
	public static void main(String[] args) {
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			ProverFactory factory = null;
			if ("z3".equals(Options.v().getSolver())) {
				factory = new Z3ProverFactory();
			} else if ("eldarica".equals(Options.v().getSolver())) {
				factory = new PrincessProverFactory();
			} else {
				throw new RuntimeException("Don't know solver " + Options.v().getSolver() + ". Using Eldarica instead.");
			}
			
			if ("safety".equals(Options.v().getChecker())) {
				System.out.println("\t\t ---   JAYHORN : Static Analayzer for Java Programs ---- ");
				System.out.println("\t Building CFG  ... " + Options.v().getJavaInput());
				System.out.println( "\t \t  ----------- \n");
				String outDir = options.getOut();
				if (!outDir.endsWith("/"))
					outDir = outDir+"/";
				String outName = null;
				if (outDir!=null) {
					String in = Options.v().getJavaInput();
					if (in.endsWith("/"))
						in = in.substring(0, in.length()-1);
					outName = in.substring(in.lastIndexOf('/') + 1, in.length()).replace(".java", "").replace(".class", "");
					if (outName.equals(""))
						outName = "noname";
				}
				SootToCfg soot2cfg = new SootToCfg(true, false, MemModel.PullPush, outDir, outName);
				soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());			
				Checker checker = outDir==null ? new Checker(factory) : new Checker(factory, outDir + outName + ".horn");
				System.out.println( "\t \t  ----------- ");
				System.out.println("\t Hornify and check  ... " + Options.v().getJavaInput());
				System.out.println( "\t \t  ----------- \n");
				boolean result = checker.checkProgram(soot2cfg.getProgram());
				
				System.out.println( "\t \t  ----------- \n");
		
				System.out.println("\t SAFETY VERIFICATION RESULT ... " + parseResult(Options.v().getSolver(), result));
				
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