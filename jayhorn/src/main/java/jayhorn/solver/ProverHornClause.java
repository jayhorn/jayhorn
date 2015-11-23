package jayhorn.solver;


public interface ProverHornClause extends ProverExpr {

    /**
     * Get the head predicate symbol; return <code>null</code> if the
     * head symbol is <code>false</code>.
     */
    ProverFun getHeadFun();

    /**
     * Get the head argument terms; return <code>null</code> if the
     * head symbol is <code>false</code>.
     */
    ProverExpr[] getHeadArgs();
    
    /**
     * Get the number of body literals.
     */
    int getArity();
    
    /**
     * Get the predicate symbol of the body literal <code>num</code>.
     */
    ProverFun getBodyFun(int num);

    /**
     * Get the arguments of the body literal <code>num</code>.
     */
    ProverExpr[] getBodyArgs(int num);

    /**
     * Get the constraint of the clause.
     */
    ProverExpr getConstraint();
    
}
