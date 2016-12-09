/**
 *
 */
package jayhorn.checker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.variable.Variable;

/**
 * @author teme
 */
public class SpacerChecker implements Checker{

	private ProverFactory factory;
	private Prover prover;

	public SpacerChecker(ProverFactory factory) {
		this.factory = factory;
	}
	
	private Map<ProverExpr, ProverResult> results = new HashMap<ProverExpr, ProverResult>();
	private List<ProverHornClause> allClauses = new LinkedList<ProverHornClause>();
	
	public boolean checkProgram(Program program) {
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
		Stats.stats().add("ToHorn", String.valueOf(toHornTimer.stop()));
		prover = hf.getProver();
		//tsClauses = S2H.sh().getTransitionRelationClause();
		//propertyClauses = S2H.sh().getPropertyClause();
		allClauses.addAll(hf.clauses);
				
		if (Options.v().getPrintHorn()) {
			System.out.println(hf.writeHorn());
		}

		ProverResult result = ProverResult.Unknown;
		try {			
			final Method entryPoint = program.getEntryPoint();
			Log.info("Running from entry point: " + entryPoint.getMethodName());
			final HornPredicate entryPred = hornContext.getMethodContract(entryPoint).precondition;
			final ProverExpr entryAtom = entryPred.instPredicate(new HashMap<Variable, ProverExpr>());

			final ProverHornClause entryClause = prover.mkHornClause(entryAtom, new ProverExpr[0],
					prover.mkLiteral(true));

			//tsClauses.add(entryClause);
			allClauses.add(entryClause);

			for (ProverHornClause clause : allClauses){
				prover.addRule(clause);
			}
			
			Log.info("Checking properties");
			Stopwatch satTimer = Stopwatch.createStarted();
			if (S2H.sh().getErrorState().isEmpty()){
				Stats.stats().add("Warning", "No assertions found.");
				return true;
			}
			for (ProverExpr prop: S2H.sh().getErrorState()){
				result = prover.query(prop);	
				results.put(prop, result);
		}
			Stats.stats().add("CheckSatTime", String.valueOf(satTimer.stop()));
		
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		} finally {
			prover.shutdown();
		}

		if (result == ProverResult.Unsat) {
			return true;
		} else if (result == ProverResult.Sat) {
			return false;
		}
		throw new RuntimeException("Verification failed with prover code " + result);
	}

	private void removeUnreachableMethods(Program program) {
		Set<Method> reachable = reachableMethod(program.getEntryPoint());
		Set<Method> toRemove = new HashSet<Method>();
		for (Method m : program.getMethods()) {
			if (!reachable.contains(m)) {
				toRemove.add(m);
				Log.info("\tRemoving unreachable method: "+m.getMethodName());
			} 
		}
		program.removeMethods(toRemove);
//		System.err.println(program);
	}
	
	private Set<Method> reachableMethod(Method main) {
		Set<Method> reachable = new HashSet<Method>();
		List<Method> todo = new LinkedList<Method>();
		todo.add(main);
		while (!todo.isEmpty()) {
			Method m = todo.remove(0);
			reachable.add(m);
			for (Method n : calledMethods(m)) {
				if (!reachable.contains(n) && !todo.contains(n)) {
					todo.add(n);
				}
			}
		}
		return reachable;
	}
	
	private List<Method> calledMethods(Method m) {
		List<Method> res = new LinkedList<Method>();
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				if (s instanceof CallStatement) {
					CallStatement cs = (CallStatement) s;
					res.add(cs.getCallTarget());
				}
			}
		}
		return res;
	}
	
	
}