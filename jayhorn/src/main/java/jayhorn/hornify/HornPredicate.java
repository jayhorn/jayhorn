package jayhorn.hornify;

import java.util.List;
import java.util.Map;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import soottocfg.cfg.variable.Variable;

public class HornPredicate {
	
	public final String name;
	public final List<Variable> variables;
	public final ProverFun predicate;
	private final Prover p;

	public HornPredicate(Prover p, String name, List<Variable> vars) {
		this.name = name;
		this.p = p;
		variables = vars;
		predicate = HornHelper.hh().genHornPredicate(p, name, vars);
	}

	public String toString() {
		return "" + predicate;
	}

	public ProverExpr instPredicate(Map<Variable, ProverExpr> varMap) {
		List<ProverExpr> allArgs = HornHelper.hh().findOrCreateProverVar(p, variables, varMap);
		System.out.println("----- HornPredicate -----");
		System.out.println(this.name);
		for (ProverExpr exp: allArgs){
			System.out.println(exp);
		}
		System.out.println("--------");
		return predicate.mkExpr(allArgs.toArray(new ProverExpr[allArgs.size()]));
		
	}

	
}
