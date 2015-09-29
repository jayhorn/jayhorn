package jayhorn.solver.princess;

import java.math.BigInteger;

import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverType;
import ap.SimpleAPI$;
import ap.parser.IIntLit;
import ap.parser.ITerm;

class TermExpr implements ProverExpr {

	protected final ProverType type;
	protected final ITerm term;

	TermExpr(ITerm term, ProverType type) {
		this.term = term;
		this.type = type;
	}

	public String toString() {
            return SimpleAPI$.MODULE$.pp(term);
	}

	public ProverType getType() {
		return type;
	}

	public BigInteger getIntLiteralValue() {
		if (term instanceof IIntLit)
			return new BigInteger(((IIntLit) term).value().toString());
		throw new RuntimeException();
	}

	public boolean getBooleanLiteralValue() {
		throw new RuntimeException();
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
    TermExpr other = (TermExpr) obj;
    if (term == null) {
      if (other.term != null)
        return false;
    } else if (!term.equals(other.term))
      return false;
    return true;
  }
}
