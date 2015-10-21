package jayhorn.solver.princess;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import scala.collection.mutable.ArrayBuffer;
import ap.parser.IAtom;
import ap.parser.ITerm;
import ap.terfor.preds.Predicate;

class PredicateFun implements ProverFun {

	private final Predicate pred;

	PredicateFun(Predicate pred) {
		this.pred = pred;
	}

	public ProverExpr mkExpr(ProverExpr[] args) {
		final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
		for (int i = 0; i < args.length; ++i) {
			argsBuf.$plus$eq(((PrincessProverExpr)args[i]).toTerm());
		}

 	        return new FormulaExpr(new IAtom(pred, argsBuf.toSeq()));
	}

	public String toString() {
		return pred.toString();
	}

}
