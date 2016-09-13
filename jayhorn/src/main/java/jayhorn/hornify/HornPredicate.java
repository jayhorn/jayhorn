package jayhorn.hornify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import soottocfg.cfg.variable.Variable;

public class HornPredicate {
	
	public final String name;
	public final List<Variable> variables;
	public final ProverFun predicate;

	public HornPredicate(String name, List<Variable> vars, ProverFun pred) {
		this.name = name;
		variables = vars;
		predicate = pred;
	}

	public String toString() {
		return "" + predicate;
	}

	public ProverExpr instPredicate(HornPredicate pred, List<ProverExpr> globalArgs, Map<Variable, ProverExpr> varMap) {
		List<ProverExpr> allArgs = new ArrayList<ProverExpr>();
		allArgs.addAll(globalArgs);
		for (Variable v : this.variables) {
			allArgs.add(varMap.get(v));	
		}
		return pred.predicate.mkExpr(allArgs.toArray(new ProverExpr[allArgs.size()]));
		
	}

	
}
