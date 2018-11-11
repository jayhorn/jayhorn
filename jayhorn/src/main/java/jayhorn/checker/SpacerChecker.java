/**
 *
 */
package jayhorn.checker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.Options;
import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.Hornify;
import jayhorn.hornify.encoder.S2H;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverResult;
import jayhorn.utils.GhostRegister;
import jayhorn.utils.HeapCounterTransformer;
import jayhorn.utils.Stats;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.variable.Variable;

/**
 * @author teme
 */
public class SpacerChecker extends Checker{

	private ProverFactory factory;
	private Prover prover;

	public SpacerChecker(ProverFactory factory) {
		this.factory = factory;
	}
	
	// Collect all the results
	private Map<ProverExpr, ProverResult> results = new HashMap<ProverExpr, ProverResult>();
	private List<ProverHornClause> allClauses = new LinkedList<ProverHornClause>();
	
	public Prover getProver(){
		return prover;
	}
	
	public CheckerResult checkProgram(Program program) {
		Preconditions.checkNotNull(program.getEntryPoint(),
				"The program has no entry points and thus is trivially verified.");	

		GhostRegister.reset();
		
		if (soottocfg.Options.v().memPrecision() >= 2) {
			GhostRegister.v().ghostVariableMap.put("pushID", IntType.instance());
		}
		
		if (Options.v().useCallIDs) {
			Log.info("Inserting call IDs  ... ");
			Verify.verify(false, "Don't run this for now!");
//			CallingContextTransformer cct = new CallingContextTransformer();
//			cct.transform(program);
		}
		
		HeapCounterTransformer hct = new HeapCounterTransformer();
		hct.transform(program);
		
		if (Options.v().printCFG) {
			System.out.println(program);
		}
		
		Log.info("Hornify  ... ");
		Hornify hf = new Hornify(factory);
		Stopwatch toHornTimer = Stopwatch.createStarted();
		HornEncoderContext hornContext = hf.toHorn(program);
		Stats.stats().add("CfgToHorn", String.valueOf(toHornTimer.stop()));
		prover = hf.getProver();
		//tsClauses = S2H.sh().getTransitionRelationClause();
		//propertyClauses = S2H.sh().getPropertyClause();
		allClauses.addAll(hf.clauses);
				

		ProverResult result = ProverResult.Unknown;
		try {			
			final Method entryPoint = program.getEntryPoint();
			Log.info("Running from entry point: " + entryPoint.getMethodName());
			final HornPredicate entryPred = hornContext.getMethodContract(entryPoint).precondition;
			final ProverExpr entryAtom = entryPred.instPredicate(new HashMap<Variable, ProverExpr>());

			final ProverHornClause entryClause = prover.mkHornClause(entryAtom, new ProverExpr[0],
					prover.mkLiteral(true));
			allClauses.add(entryClause);

			for (ProverHornClause clause : allClauses){
				prover.addRule(clause);
			}
			
			if (Options.v().getPrintHorn()) {
				//System.out.println(hf.writeHorn());
				prover.printRules();
			}
			
			// Bounds Check
//			if (Options.v().getHeapLimit() > -1) {
//				HeapBoundsCheck bc = new HeapBoundsCheck(prover);
//				Log.info("Adding Heap Bounds Check");
//				final ProverHornClause boundClause = bc.addMainHeapCheck(Options.v().getHeapLimit());
//				prover.addRule(boundClause);
//				result = prover.query(bc.mainHeapExpr());
//				String propLine = "HeapBound";
//				if (result == ProverResult.Unsat) {
//					Stats.stats().add(propLine, "OK");
//				} else if (result == ProverResult.Sat) {
//					Stats.stats().add(propLine, "KO");
//				} else {
//					Stats.stats().add(propLine, "ERROR");
//				}
//			}
			Log.info("Checking properties");
			Stopwatch satTimer = Stopwatch.createStarted();
			if (S2H.sh().getErrorState().isEmpty()){
				Stats.stats().add("Warning", "No assertions found.");
				return CheckerResult.SAFE;
			}
			

			for (Map.Entry<ProverExpr, Integer> props : S2H.sh().getErrorState().entrySet()) {
			    ProverExpr prop = props.getKey();
			    if (jayhorn.Options.v().getTimeout() > 0) {
					int timeoutInMsec = (int) TimeUnit.SECONDS.toMillis(jayhorn.Options.v().getTimeout());
					prover.query(prop, true);
					result = prover.getResult(timeoutInMsec);
				} else {
					result = prover.query(prop, false);
				}
			    String propLine = "Property@Line"+props.getValue();
			    if (result == ProverResult.Unsat) {
			    	Stats.stats().add(propLine, "SAFE");
			    } else if (result == ProverResult.Sat){
			    	Stats.stats().add(propLine, "UNSAFE");
			    	if (Options.v().solution){
			    		cex();
			    	}
			    } else {
			    	Stats.stats().add(propLine, "ERROR");
			    }
				results.put(prop, result);
			}

			
			Stats.stats().add("CheckSatTime", String.valueOf(satTimer.stop()));
			
		} catch (Throwable t) {
			
			t.printStackTrace();
			throw new RuntimeException(t);
		} finally {
			prover.shutdown();
		}
		

		for (ProverResult res : results.values()) {
			if (res == ProverResult.Sat){
				return CheckerResult.UNSAFE;
			}else if(res != ProverResult.Unsat){
				throw new RuntimeException("Verification failed with prover code " + result);
			}
		}

		return CheckerResult.SAFE;
	}

	private void cex(){
		//work in progress
		System.out.println(prover.getCex());
	}
	
//	private void removeUnreachableMethods(Program program) {
//		Set<Method> reachable = reachableMethod(program.getEntryPoint());
//		Set<Method> toRemove = new HashSet<Method>();
//		for (Method m : program.getMethods()) {
//			if (!reachable.contains(m)) {
//				toRemove.add(m);
//				Log.info("\tRemoving unreachable method: "+m.getMethodName());
//			} 
//		}
//		program.removeMethods(toRemove);
////		System.err.println(program);
//	}
	
//	private Set<Method> reachableMethod(Method main) {
//		Set<Method> reachable = new HashSet<Method>();
//		List<Method> todo = new LinkedList<Method>();
//		todo.add(main);
//		while (!todo.isEmpty()) {
//			Method m = todo.remove(0);
//			reachable.add(m);
//			for (Method n : calledMethods(m)) {
//				if (!reachable.contains(n) && !todo.contains(n)) {
//					todo.add(n);
//				}
//			}
//		}
//		return reachable;
//	}
	
//	private List<Method> calledMethods(Method m) {
//		List<Method> res = new LinkedList<Method>();
//		for (CfgBlock b : m.vertexSet()) {
//			for (Statement s : b.getStatements()) {
//				if (s instanceof CallStatement) {
//					CallStatement cs = (CallStatement) s;
//					res.add(cs.getCallTarget());
//				}
//			}
//		}
//		return res;
//	}
	
	
}
