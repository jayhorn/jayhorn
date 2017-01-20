package jayhorn.solver.princess;

import java.math.BigInteger;

import ap.parser.PartialEvaluator$;
import ap.parser.EquivExpander$;
import ap.parser.Transform2Prenex$;
import ap.parser.IFormula;
import ap.parser.SMTLineariser$;

import jayhorn.solver.BoolType;
import jayhorn.solver.IntType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverType;
import lazabs.horn.bottomup.HornClauses;
import lazabs.horn.bottomup.SimpleWrapper;

class HornExpr implements ProverHornClause {

    protected final HornClauses.Clause clause;

    public HornExpr(HornClauses.Clause clause) {
        this.clause = clause;
    }

    public ProverType getType() {
        return BoolType.INSTANCE;
    }

    public BigInteger getIntLiteralValue() {
        throw new UnsupportedOperationException();
    }

    public boolean getBooleanLiteralValue() {
        throw new UnsupportedOperationException();
    }

  public int hashCode() {
    return clause.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HornExpr other = (HornExpr) obj;
    if (clause == null) {
      if (other.clause != null)
        return false;
    } else if (!clause.equals(other.clause))
      return false;
    return true;
  }

  @Override
  public String toString() {
	  return this.clause.toPrologString();
//	  return this.clause.toString();
//	  String ret = null;
//	  try {
//		  ret = this.clause.toPrologString();
//	  } catch (Exception e) {
//		  System.out.println("Exception on " + this.clause.toString());
//		  e.printStackTrace();
//	  }
//	  return ret;
  }

    public String toSMTLIBFormula() {
        //return clause.toSMTString();
        IFormula f = clause.toFormula();
        f = PartialEvaluator$.MODULE$.apply(f);
        f = EquivExpander$.MODULE$.apply(f);
        f = Transform2Prenex$.MODULE$.apply(f);
        return SMTLineariser$.MODULE$.asString(f);
    }

    /**
     * Get the head predicate symbol.
     */
    public ProverFun getHeadFun() {
        if (clause.head().equals(SimpleWrapper.FALSEAtom()))
            return null;
        return new PredicateFun(clause.head().pred(), null);
    }

    /**
     * Get the head argument terms.
     */
    public ProverExpr[] getHeadArgs() {
        if (clause.head().equals(SimpleWrapper.FALSEAtom()))
            return null;
        
        final int N = clause.head().pred().arity();
        ProverExpr[] res = new ProverExpr[N];

        for (int i = 0; i < N; ++i)
            // type is not likely to be correct in all cases, this
            // should be improved ...
            res[i] = new TermExpr(clause.head().args().apply(i),
                                  IntType.INSTANCE);
        
        return res;
    }
    
    /**
     * Get the number of body literals.
     */
    public int getArity() {
        return clause.body().size();
    }
    
    /**
     * Get the predicate symbol of the body literal <code>num</code>.
     */
    public ProverFun getBodyFun(int num) {
        return new PredicateFun(clause.body().apply(num).pred(), null);
    }

    /**
     * Get the arguments of the body literal <code>num</code>.
     */
    public ProverExpr[] getBodyArgs(int num) {
        final int N = clause.body().apply(num).pred().arity();
        ProverExpr[] res = new ProverExpr[N];

        for (int i = 0; i < N; ++i)
            // type is not likely to be correct in all cases, this
            // should be improved ...
            res[i] = new TermExpr(clause.body().apply(num).args().apply(i),
                                  IntType.INSTANCE);
        
        return res;
    }

    /**
     * Get the constraint of the clause.
     */
    public ProverExpr getConstraint() {
        return new FormulaExpr(clause.constraint());
    }
}
