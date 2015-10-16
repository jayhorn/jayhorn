package jayhorn.solver.princess;

import java.math.BigInteger;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverType;
import lazabs.horn.bottomup.HornClauses;

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
  }
}
