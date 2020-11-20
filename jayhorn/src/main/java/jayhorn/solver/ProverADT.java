package jayhorn.solver;

/**
 * Interface representing algebraic data-types
 */
public interface ProverADT {

    /**
     * Query the individual ADT types
     */
    ProverType getType(int typeIndex);


    /**
     * Build nondeterministic term of the specified ADT type
     */
    ProverExpr mkHavocExpr(int typeIndex);

    /**
     * Build constructor terms, using the ctorIndex'th constructor
     */
    ProverExpr mkCtorExpr(int ctorIndex, ProverExpr[] args);

    /**
     * Build selector terms, using the selIndex'th selector for the
     * ctorIndex'th constructor
     */
    ProverExpr mkSelExpr(int ctorIndex, int selIndex, ProverExpr term);
    
    /**
     * Build a tester formula, testing whether the given term has the
     * ctorIndex'th constructor as head symbol
     */
    ProverExpr mkTestExpr(int ctorIndex, ProverExpr term);

    /**
     * Build a size term for the given ADT term
     */
    ProverExpr mkSizeExpr(ProverExpr term);

}
