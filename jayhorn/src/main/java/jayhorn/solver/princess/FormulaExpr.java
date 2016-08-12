package jayhorn.solver.princess;

import java.math.BigInteger;

import ap.SimpleAPI$;
import ap.basetypes.IdealInt$;
import ap.parser.IBoolLit;
import ap.parser.IExpression;
import ap.parser.IFormula;
import ap.parser.IIntLit;
import ap.parser.ITerm;
import ap.parser.ITermITE;
import jayhorn.solver.BoolType;
import jayhorn.solver.ProverType;

class FormulaExpr extends PrincessProverExpr {

	protected final IFormula formula;

	FormulaExpr(IFormula formula) {
		this.formula = formula;
	}

	@Override
	public String toString() {
		return SimpleAPI$.MODULE$.pp(formula);
	}

	public ProverType getType() {
		return BoolType.INSTANCE;
	}

	public ITerm toTerm() {
		return new ITermITE(formula, new IIntLit(IdealInt$.MODULE$.apply(0)), new IIntLit(IdealInt$.MODULE$.apply(1)));
	}

	public IFormula toFormula() {
		return formula;
	}

	public IExpression toExpression() {
		return formula;
	}

	public boolean isBoolean() {
		return true;
	}

	public BigInteger getIntLiteralValue() {
		throw new RuntimeException();
	}

	public boolean getBooleanLiteralValue() {
		if (formula instanceof IBoolLit)
			return ((IBoolLit) formula).value();
		throw new RuntimeException();
	}

	@Override
	public int hashCode() {
		return formula.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormulaExpr other = (FormulaExpr) obj;
		if (formula == null) {
			if (other.formula != null)
				return false;
		} else if (!formula.equals(other.formula))
			return false;
		return true;
	}

}
