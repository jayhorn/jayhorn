package jayhorn;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Level;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.common.base.Stopwatch;

import jayhorn.checker.EldaricaChecker;
import jayhorn.checker.SpacerChecker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.spacer.SpacerProverFactory;
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

  		boolean result;
  		if ("spacer".equals(Options.v().getSolver())){
  			SpacerChecker spacer = new SpacerChecker(factory);
  			result = spacer.checkProgram(program);
  		} else{
  			EldaricaChecker eldarica = new EldaricaChecker(factory);
  			result = eldarica.checkProgram(program);
  		}
  		String prettyResult = parseResult(Options.v().getSolver(), result);
  		Stats.stats().add("FinalResult", prettyResult);

		if (Options.v().stats){ 
			Stats.stats().printStats(); 
		}else{
			System.out.println(prettyResult);
		}
      }
    
	public static void main(String[] args) {
		
		Options options = Options.v();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			// parse command-line arguments
			parser.parseArgument(args);
			options.updateSootToCfgOptions();
			ProverFactory factory = null;
			if ("spacer".equals(Options.v().getSolver())) {
				factory = new SpacerProverFactory();
			} else if ("eldarica".equals(Options.v().getSolver())) {
				factory = new PrincessProverFactory();
			} else {
				throw new RuntimeException("Don't know solver " + Options.v().getSolver() + ". Using Eldarica instead.");
			}

			if (Options.v().verbose) Log.v().setLevel(Level.INFO);
			
			Log.info("\t\t ---   JAYHORN : Static Analayzer for Java Programs ---- ");
			Log.info("\t Verification : " + Options.v().getChecker());
			Log.info("\t Solver : " + Options.v().getSolver());
			
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
