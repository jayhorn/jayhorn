/**
 * 
 */
package jayhorn;

import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import jayhorn.checker.Checker;
import jayhorn.hornify.Hornify;
import jayhorn.hornify.MethodEncoder;
import jayhorn.old_inconsistency_check.InconsistencyChecker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverHornClause;
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
    
    
    public static void safetyAnalysis(Options options, ProverFactory factory){
		System.out.println("\t\t ---   JAYHORN : Static Analayzer for Java Programs ---- ");
		System.out.println("\t Building CFG  ... " + Options.v().getJavaInput());
		System.out.println( "\t \t  ----------- \n");
		String outDir = options.getOut();
		String outName = null;
		if (outDir!=null) {
			String in = Options.v().getJavaInput();
			if (in.endsWith("/"))
				in = in.substring(0, in.length()-1);
			outName = in.substring(in.lastIndexOf('/'), in.length()).replace(".java", "").replace(".class", "");
			if (outName.equals(""))
				outName = "noname";
		}
		SootToCfg soot2cfg = new SootToCfg(true, false, MemModel.PullPush, outDir, outName);
		soot2cfg.run(Options.v().getJavaInput(), Options.v().getClasspath());			
//		Checker checker = new Checker(factory, outDir + outName + ".horn");
		System.out.println( "\t \t  ----------- ");
		System.out.println("\t Hornify and check  ... " + Options.v().getJavaInput());
		System.out.println( "\t \t  ----------- \n");
//		boolean result = checker.checkProgram(soot2cfg.getProgram());
		Program program = soot2cfg.getProgram();
		Log.info("Hornify  ... ");
		Hornify hornify = new Hornify(factory);
		hornify.toHorn(program);	
		if (Options.v().getPrintHorn()){
			System.out.println("-- Generated Horn -- ");
			System.out.println(hornify.writeHorn());
			System.out.println("------");
		} 		
		List<ProverHornClause> clauses = hornify.getClauses();
		MethodEncoder mEncoder = hornify.getMethodEncoder();
		Checker hornChecker = new Checker(factory, mEncoder);
		
		boolean result = hornChecker.checkProgram(program, clauses);
		
		System.out.println( "\t \t  ----------- \n");

		System.out.println("\t SAFETY VERIFICATION RESULT ... " + parseResult(Options.v().getSolver(), result));
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
			
			System.out.println("\t\t ---   JAYHORN : Static Analayzer for Java Programs ---- ");
			
			if ("safety".equals(Options.v().getChecker())) {
				safetyAnalysis(options, factory);
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