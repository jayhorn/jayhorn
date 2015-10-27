package jayhorn.solver.princess;

import java.math.BigInteger;

import ap.SimpleAPI$;
import ap.parser.IExpression;
import ap.parser.IExpression$;
import ap.parser.IFormula;
import ap.parser.IIntLit;
import ap.parser.ITerm;
import jayhorn.solver.BoolType;
import jayhorn.solver.ProverType;

class TermExpr extends PrincessProverExpr {

	protected final ProverType type;
	protected final ITerm term;

	TermExpr(ITerm term, ProverType type) {
		this.term = term;
		this.type = type;
	}

	public String toString() {
            return SimpleAPI$.MODULE$.pp(term);
	}

    public ITerm toTerm() {
        return term;
    }
    
    public IFormula toFormula() {
        return IExpression$.MODULE$.eqZero(term);
    }

    public IExpression toExpression() {
        return term;
    }

    public boolean isBoolean() {
        return type == BoolType.INSTANCE;
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
