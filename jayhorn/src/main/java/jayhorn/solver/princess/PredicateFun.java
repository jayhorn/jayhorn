package jayhorn.solver.princess;

import ap.parser.IAtom;
import ap.parser.ITerm;
import ap.terfor.preds.Predicate;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverType;
import scala.collection.mutable.ArrayBuffer;

class PredicateFun implements ProverFun {

	private final Predicate pred;
        private final ProverType[] argTypes;

	PredicateFun(Predicate pred, ProverType[] argTypes) {
		this.pred = pred;
                this.argTypes = argTypes;
	}

	public ProverExpr mkExpr(ProverExpr ... args) {
            try {
            checkArgTypes(argTypes, args);
            } catch(RuntimeException e) {
              System.err.println("Warning: " + e);
              e.printStackTrace();
            }

            ProverExpr[] flatArgs = ProverTupleExpr.flatten(args);
            
            final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
            for (int i = 0; i < flatArgs.length; ++i) {
                argsBuf.$plus$eq(((PrincessProverExpr)flatArgs[i]).toTerm());
            }

            return new FormulaExpr(new IAtom(pred, argsBuf.toSeq()));
	}

    protected static void checkArgTypes(ProverType[] argTypes,
                                        ProverExpr[] args) {
      if (argTypes == null)
        return;
      if (argTypes.length != args.length)
        throw new RuntimeException("Wrong number of arguments: expected " +
                                   argTypes.length + " but got " +
                                   args.length);
      for (int i = 0; i < argTypes.length; ++i)
        if (!argTypes[i].equals(args[i].getType()))
          throw new RuntimeException("Wrong argument type: expected " +
                                     argTypes[i] + " but got " +
                                     args[i] + " of type " +
                                     args[i].getType());
    }

    public String toString() {
        return pred.name();
    }

    public String toSMTLIBDeclaration() {
        StringBuffer res = new StringBuffer();
        res.append("(declare-fun |" + pred.name() + "| (");
        String sep = "";
        for (int i = 0; i < pred.arity(); ++i) {
            res.append(sep);
            res.append("Int");
            sep = " ";
        }
        res.append(") Bool)");
        return res.toString();
    }

    public int hashCode() {
        return pred.hashCode() + 13;
    }
    
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PredicateFun other = (PredicateFun) obj;
        if (pred == null) {
            if (other.pred != null)
                return false;
        } else if (!pred.equals(other.pred))
            return false;
        return true;
    }

}
