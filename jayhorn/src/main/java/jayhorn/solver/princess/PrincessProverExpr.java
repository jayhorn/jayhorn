package jayhorn.solver.princess;

import ap.parser.IExpression;
import ap.parser.IFormula;
import ap.parser.ITerm;
import jayhorn.solver.ProverExpr;

abstract class PrincessProverExpr implements ProverExpr {

    public abstract ITerm toTerm();
    public abstract IFormula toFormula();
    public abstract IExpression toExpression();
    public abstract boolean isBoolean();

}
