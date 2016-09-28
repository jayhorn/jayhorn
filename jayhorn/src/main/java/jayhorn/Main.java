package jayhorn;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.common.base.Stopwatch;

import jayhorn.checker.Checker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.z3.Z3ProverFactory;
import jayhorn.utils.Stats;
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
  		SootToCfg soot2cfg = new SootToCfg();
  		soottocfg.Options.v().setBuiltInSpecs(Options.v().useSpecs);
  		soottocfg.Options.v().setResolveVirtualCalls(true);
  		soottocfg.Options.v().setMemModel(MemModel.PullPush);
  		if (Options.v().getOut()!=null) {
  			Path outDir = Paths.get(Options.v().getOut());
  			String in = Options.v().getJavaInput();
  			String outName = in.substring(in.lastIndexOf('/') + 1, in.length()).replace(".java", "").replace(".class", "");
  			soottocfg.Options.v().setOutDir(outDir);
  			soottocfg.Options.v().setOutBaseName(outName);
  		}
  	
  		Stopwatch sootTocfgTimer = Stopwatch.createStarted();
  		soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());	
  	
  		Program program = soot2cfg.getProgram();
  	    Stats.stats().add("SootToCFG", String.valueOf(sootTocfgTimer.stop()));
  		
  		Log.info("Safety Verification ... ");
  		Checker hornChecker = new Checker(factory);
  		
  		boolean result = hornChecker.checkProgram(program);
  		
  		String prettyResult = parseResult(Options.v().getSolver(), result);
  		
  		Stats.stats().add("Result", prettyResult);
  		
  		Log.info("Safety Result ... " + prettyResult);
		if (Options.v().stats){ Stats.stats().printStats(); }
      }
    
	public static void main(String[] args) {
		System.out.println("\t\t ---   JAYHORN : Static Analyzer for Java Programs ---- ");
		
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			options.updateSootToCfgOptions();
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
			Stats.stats().add("Result", "UNKNOWN");
			if (Options.v().stats){ Stats.stats().printStats(); }
			throw t;	
		} finally {
			Options.resetInstance();
			soot.G.reset();
		}

	}
	
}
