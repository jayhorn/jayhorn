package jayhorn.solver.princess;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import scala.collection.mutable.ArrayBuffer;
import ap.basetypes.IdealInt$;
import ap.parser.IAtom;
import ap.parser.IIntLit;
import ap.parser.ITerm;
import ap.parser.ITermITE;
import ap.terfor.preds.Predicate;

class PredicateFun implements ProverFun {

	private final Predicate pred;

	PredicateFun(Predicate pred) {
		this.pred = pred;
	}

	public ProverExpr mkExpr(ProverExpr[] args) {
		final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
		for (int i = 0; i < args.length; ++i) {
			ITerm termArg;
			if (args[i].getType() == BoolType.INSTANCE)
				termArg = new ITermITE(((FormulaExpr) args[i]).formula,
						new IIntLit(IdealInt$.MODULE$.apply(0)), new IIntLit(
								IdealInt$.MODULE$.apply(1)));
			else
				termArg = ((TermExpr) args[i]).term;
			argsBuf.$plus$eq(termArg);
		}

 	        return new FormulaExpr(new IAtom(pred, argsBuf.toSeq()));
	}

	public String toString() {
		return pred.toString();
	}

}
