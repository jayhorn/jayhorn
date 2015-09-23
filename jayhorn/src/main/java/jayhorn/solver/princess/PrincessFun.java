package jayhorn.solver.princess;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverType;
import scala.collection.mutable.ArrayBuffer;
import ap.basetypes.IdealInt$;
import ap.parser.IExpression$;
import ap.parser.IFunApp;
import ap.parser.IFunction;
import ap.parser.IIntLit;
import ap.parser.ITerm;
import ap.parser.ITermITE;

class PrincessFun implements ProverFun {

	private final IFunction fun;
	private final ProverType resType;

	PrincessFun(IFunction fun, ProverType resType) {
		this.fun = fun;
		this.resType = resType;
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

		final ITerm t = new IFunApp(fun, argsBuf.toSeq());

		if (resType instanceof BoolType)
			return new FormulaExpr(IExpression$.MODULE$.eqZero(t));
		else
			return new TermExpr(t, resType);
	}

	public String toString() {
		return fun.toString();
	}

}
