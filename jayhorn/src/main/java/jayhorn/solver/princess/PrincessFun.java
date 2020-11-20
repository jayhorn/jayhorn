package jayhorn.solver.princess;

import ap.basetypes.IdealInt$;
import ap.parser.IExpression$;
import ap.parser.IFunApp;
import ap.parser.IFunction;
import ap.parser.IIntLit;
import ap.parser.ITerm;
import ap.parser.ITermITE;
import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverType;
import scala.collection.mutable.ArrayBuffer;

class PrincessFun implements ProverFun {

	private final IFunction fun;
	private final ProverType resType;

	PrincessFun(IFunction fun, ProverType resType) {
		this.fun = fun;
		this.resType = resType;
	}

	public ProverExpr mkExpr(ProverExpr ... args) {
            ProverExpr[] flatArgs = ProverTupleExpr.flatten(args);
            
            final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
            for (int i = 0; i < flatArgs.length; ++i) {
                ITerm termArg;
                if (flatArgs[i].getType() == BoolType.INSTANCE)
                    termArg = new ITermITE(((FormulaExpr) flatArgs[i]).formula,
                                           new IIntLit(IdealInt$.MODULE$.apply(0)),
                                           new IIntLit(IdealInt$.MODULE$.apply(1)));
                else
                    termArg = ((TermExpr) flatArgs[i]).term;
                argsBuf.$plus$eq(termArg);
            }
            
            final ITerm t = new IFunApp(fun, argsBuf.toSeq());
            
            if (resType instanceof BoolType)
                return new FormulaExpr(IExpression$.MODULE$.eqZero(t));
            else
                return new TermExpr(t, resType);
	}

	public String toString() {
		return fun.name();
	}

    public int hashCode() {
        return fun.hashCode() + 17;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PrincessFun other = (PrincessFun) obj;
        if (fun == null) {
            if (other.fun != null)
                return false;
        } else if (!fun.equals(other.fun))
            return false;
        return true;
    }

}
