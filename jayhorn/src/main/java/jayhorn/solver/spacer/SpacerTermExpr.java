package jayhorn.solver.spacer;

import java.math.BigInteger;

import com.microsoft.z3.Expr;
import com.microsoft.z3.Z3Exception;
import com.microsoft.z3.enumerations.Z3_lbool;

import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverType;

class SpacerTermExpr implements ProverExpr {

	protected final ProverType type;
	protected final Expr term;

	SpacerTermExpr(Expr expr, ProverType type) {
		this.term = expr;
		this.type = type;
	}

	public String toString() {
		return term.toString();
	}

	public ProverType getType() {
		return type;
	}

	/**
	 * Unpack the Z3 Expr 
	 * @return Z3 Expr for this TermExpr
	 */
	public Expr getExpr() {
		return this.term;
	}
	
	public BigInteger getIntLiteralValue() {
		throw new RuntimeException();
	}

	public boolean getBooleanLiteralValue() {
		try {
			return this.term.getBoolValue()==Z3_lbool.Z3_L_TRUE;
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public int hashCode() {
		return term.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpacerTermExpr other = (SpacerTermExpr) obj;
		if (term == null) {
			if (other.term != null)
				return false;
		} else if (!term.equals(other.term))
			return false;
		return true;
	}
}
