package jayhorn.solver.princess;

import jayhorn.solver.ProverExpr;

import ap.parser.ITerm;
import ap.parser.IFormula;
import ap.parser.IExpression;

abstract class PrincessProverExpr implements ProverExpr {

    public abstract ITerm toTerm();
    public abstract IFormula toFormula();
    public abstract IExpression toExpression();
    public abstract boolean isBoolean();

}
