package jayhorn.hornify;

import java.util.List;

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

}
