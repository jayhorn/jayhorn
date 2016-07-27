/**
 * 
 */
package jayhorn.checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import jayhorn.Log;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.MethodEncoder;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverResult;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.method.Method;

/**
 * @author teme
 *
 */
public class Checker {

	private final ProverFactory factory;
	private final MethodEncoder mEncoder;
	
	public Checker(ProverFactory fac, MethodEncoder mEncoder) {
		this.factory = fac;
		this.mEncoder = mEncoder;
	}
	

	public boolean checkProgram(Program program, List<ProverHornClause> clauses) {
		Log.info("Starting verification for " + program.getEntryPoints().length + " entry points.");
		Prover p = factory.spawn();
		p.setHornLogic(true);
		ProverResult result = ProverResult.Unknown;
		try{
			
			for (Method method : program.getEntryPoints()) {
				Log.info("\tVerification from entry " + method.getMethodName());
				p.push();

				for (ProverHornClause clause : clauses)
					p.addAssertion(clause);

				// add an entry clause from the preconditions
				final HornPredicate entryPred = mEncoder.getMethodContract(method.getMethodName()).precondition;
				//final HornPredicate entryPred = methodContracts.get(method.getMethodName()).precondition;
				final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
				final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
				mEncoder.createVarMap(p, entryPred.variables, entryVars, varMap);

				final ProverExpr entryAtom = entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));

				p.addAssertion(p.mkHornClause(entryAtom, new ProverExpr[0], p.mkLiteral(true)));

				if (jayhorn.Options.v().getTimeout() > 0) {
					int timeoutInMsec = (int) TimeUnit.SECONDS.toMillis(jayhorn.Options.v().getTimeout());
					p.checkSat(false);
					result = p.getResult(timeoutInMsec);
				} else {
					result = p.checkSat(true);
				}
				p.pop();
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		} finally {
			p.shutdown();
		}
		
		if (result == ProverResult.Sat) {
			return true;
		} else if (result == ProverResult.Unsat) {
			return false;
		}
		throw new RuntimeException("Verification failed with prover code " + result);
	}


}