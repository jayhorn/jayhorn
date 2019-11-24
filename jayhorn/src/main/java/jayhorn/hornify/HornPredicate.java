package jayhorn.hornify;

import java.util.List;
import java.util.Map;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverTupleType;
import jayhorn.solver.ProverType;
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
            return predicate.mkExpr(compileArguments(varMap));
	}

    public ProverExpr[] compileArguments(Map<Variable, ProverExpr> varMap) {
		List<ProverExpr> allArgs = HornHelper.hh().findOrCreateProverVar(p, variables, varMap);
		ProverExpr[] exprs = allArgs.toArray(new ProverExpr[allArgs.size()]);
		/*
		 * Adjust the size of TupleExpr if necessary. This happens when dealing
		 * with subtyping etc.
		 */
		for (int i = 0; i<variables.size(); i++) {
			ProverType pt = HornHelper.hh().getProverType(p,variables.get(i).getType());
			if (pt instanceof ProverTupleType) {
				ProverTupleType ptt = (ProverTupleType)pt;
				ProverTupleExpr pte = (ProverTupleExpr)exprs[i];
				if (pte.getArity()<ptt.getArity()) {
					ProverExpr[] tupleElements = new ProverExpr[ptt.getArity()];
					for (int j=0;j<ptt.getArity();j++) {
						if (j<pte.getArity()) {
							tupleElements[j]=pte.getSubExpr(j);
						} else {
							tupleElements[j]=p.mkHornVariable("$dummy"+i+j, ptt.getSubType(j));
						}
					}
					exprs[i] = new ProverTupleExpr(tupleElements);
				} else if (pte.getArity()>ptt.getArity()) {
					ProverExpr[] tupleElements = new ProverExpr[ptt.getArity()];
					for (int j=0;j<ptt.getArity();j++) {
						tupleElements[j]=pte.getSubExpr(j);
					}
					exprs[i] = new ProverTupleExpr(tupleElements);					
				} else {
					//do nothing.
				}
			}
		}

                return exprs;
    }
	
}
