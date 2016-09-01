/**
 * 
 */
package jayhorn;



import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import jayhorn.checker.Checker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.z3.Z3ProverFactory;
import soottocfg.cfg.Program;
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
    }
    /**
     * Safety Analysis with JayHorn
     * @param options
     * @param factory
     */
    public static void safetyAnalysis(ProverFactory factory){
  		Log.info("Building CFG  ... ");
  		String outDir = Options.v().getOut();
  		String outName = null;
  		if (outDir!=null) {
  			String in = Options.v().getJavaInput();
  			outName = in.substring(in.lastIndexOf('/') + 1, in.length()).replace(".java", "").replace(".class", "");
  		}
  		SootToCfg soot2cfg = new SootToCfg(true, false, MemModel.PullPush, outDir, outName);
  		soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());	
  	
  		Program program = soot2cfg.getProgram();
  		
  		Log.info("Safety Verification ... ");
  		Checker hornChecker = new Checker(factory);
  		
  		boolean result = hornChecker.checkProgram(program);
  		Log.info("Safety Result ... " + parseResult(Options.v().getSolver(), result));
  		
      }
    
	public static void main(String[] args) {
		System.out.println("\t\t ---   JAYHORN : Static Analayzer for Java Programs ---- ");
		
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
				safetyAnalysis(factory);
				
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
