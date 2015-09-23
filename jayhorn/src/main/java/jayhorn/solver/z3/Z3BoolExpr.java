package jayhorn.solver.z3;

import java.math.BigInteger;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverType;

import com.microsoft.z3.BoolExpr;

class Z3BoolExpr implements ProverExpr {

	protected final BoolExpr formula;

	Z3BoolExpr(BoolExpr formula) {
		this.formula = formula;
	}

	public String toString() {
            return this.formula.toString();
	}

	/**
	 * Unpack the Z3 Expr 
	 * @return Z3 Expr for this Z3BoolExpr
	 */
	public BoolExpr getExpr() {
		return this.formula;
	}

	
	public ProverType getType() {
		return BoolType.INSTANCE;
	}

	public BigInteger getIntLiteralValue() {
		throw new RuntimeException();
	}

	public boolean getBooleanLiteralValue() {
		throw new RuntimeException();
	}

  public int hashCode() {
    return formula.hashCode();
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Z3BoolExpr other = (Z3BoolExpr) obj;
    if (formula == null) {
      if (other.formula != null)
        return false;
    } else if (!formula.equals(other.formula))
      return false;
    return true;
  }
}
