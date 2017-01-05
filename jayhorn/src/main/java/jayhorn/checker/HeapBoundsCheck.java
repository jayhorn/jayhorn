package jayhorn.checker;

import jayhorn.hornify.HornHelper;
import jayhorn.hornify.encoder.S2H;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverType;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.variable.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Add Space Heap Bound Check
 * @author Teme
 *
 */

public class HeapBoundsCheck {
	private Prover prover = null;
	private ProverExpr boundCheckMain;
	
	public HeapBoundsCheck(Prover p){
		this.prover = p;
	}
	
	/**
	 * Heap checks per method
	 * @param m
	 * @param bound
	 */
	public void addMethodHeapCheck(Method m, int bound){}
	
	/**
	 * Add heap check to the main method
	 * @param bound
	 * @return
	 */
	public ProverHornClause addMainHeapCheck(int bound){
		Entry<Variable, Variable> countVars = S2H.sh().getMainHeapCount();
		
		Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
		
		ProverExpr inCount = HornHelper.hh().findOrCreateProverVar(prover, countVars.getValue(), varMap);
		ProverExpr outCount = HornHelper.hh().findOrCreateProverVar(prover, countVars.getKey(), varMap);
		ProverExpr diff = prover.mkLeq(prover.mkLiteral(bound), prover.mkMinus(outCount, inCount));
		
		final ProverExpr boundCheck;
		String tag = "BoundCheck@Main";
		final ProverFun errorPredicate = prover.mkHornPredicate(tag, new ProverType[] {});
	
		if (prover.toString().equals("spacer")){
			boundCheck = errorPredicate.mkExpr(new ProverExpr[]{});
			S2H.sh().setErrorState(boundCheck, 0);
		}else{
			boundCheck = prover.mkLiteral(false);
		}
		this.boundCheckMain = boundCheck;
		return prover.mkHornClause(boundCheck, new ProverExpr[] {  }, prover.mkNot(diff));
	}
	
	public ProverExpr mainHeapExpr(){
		return boundCheckMain;
	}
}
