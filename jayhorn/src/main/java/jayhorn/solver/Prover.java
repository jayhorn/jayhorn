package jayhorn.solver;

import java.math.BigInteger;
import java.util.Map;
import java.util.List;

public interface Prover {

	/**
	 * Returns a map from predicate name to formula.
	 * This is only textual stuff produced by the solver.
	 * Mostly for debugging.
	 * @return
	 */
	Map<String, String> getLastSolution();  
	
	// Types
	ProverType getBooleanType();

	ProverType getIntType();

	ProverType getArrayType(ProverType[] argTypes, ProverType resType);

	ProverType getTupleType(ProverType[] subTypes);

	// Variables
	ProverExpr mkBoundVariable(int deBruijnIndex, ProverType type);

	ProverExpr mkVariable(String name, ProverType type);

	ProverFun mkUnintFunction(String name, ProverType[] argTypes,
                                  ProverType resType);

	/**
	 * Define a new interpreted function. The body is supposed to contain bound
	 * variables with indexes <code>0, 1, ..., (n-1)</code> representing the
	 * arguments of the function.
	 */
	ProverFun mkDefinedFunction(String name, ProverType[] argTypes,
			ProverExpr body);

	// Quantifiers
	ProverExpr mkAll(ProverExpr body, ProverType type);

	ProverExpr mkEx(ProverExpr body, ProverType type);

	ProverExpr mkTrigger(ProverExpr body, ProverExpr[] triggers);

	// Equations (applicable to all types) (also have n-ary version?)
	ProverExpr mkEq(ProverExpr left, ProverExpr right);

	// Propositional (also have n-ary versions?)
	ProverExpr mkLiteral(boolean value);

	ProverExpr mkNot(ProverExpr body);

	ProverExpr mkAnd(ProverExpr left, ProverExpr right);

	ProverExpr mkAnd(ProverExpr ... args);

	ProverExpr mkOr(ProverExpr left, ProverExpr right);

	ProverExpr mkOr(ProverExpr ... args);

	ProverExpr mkImplies(ProverExpr left, ProverExpr right);

	ProverExpr mkIte(ProverExpr cond, ProverExpr thenExpr, ProverExpr elseExpr);

	// Arithmetic
	ProverExpr mkLiteral(int value);

	ProverExpr mkLiteral(BigInteger value);

	ProverExpr mkPlus(ProverExpr left, ProverExpr right);

	ProverExpr mkPlus(ProverExpr[] args);

	ProverExpr mkMinus(ProverExpr left, ProverExpr right);

	ProverExpr mkNeg(ProverExpr arg);

	/**
	 * Euclidian integer division.
	 */
	ProverExpr mkEDiv(ProverExpr num, ProverExpr denom);

	/**
	 * Euclidian integer modulus.
	 */
	ProverExpr mkEMod(ProverExpr num, ProverExpr denom);

	/**
	 * Truncation integer division.
	 */
	ProverExpr mkTDiv(ProverExpr num, ProverExpr denom);

	/**
	 * Truncation integer modulus.
	 */
	ProverExpr mkTMod(ProverExpr num, ProverExpr denom);

	ProverExpr mkMult(ProverExpr left, ProverExpr right);

	ProverExpr mkGeq(ProverExpr left, ProverExpr right);

	ProverExpr mkGt(ProverExpr left, ProverExpr right);

	ProverExpr mkLeq(ProverExpr left, ProverExpr right);

	ProverExpr mkLt(ProverExpr left, ProverExpr right);

	ProverExpr mkSelect(ProverExpr ar, ProverExpr[] indexes);

	ProverExpr mkStore(ProverExpr ar, ProverExpr[] indexes, ProverExpr value);

        ProverExpr mkTuple(ProverExpr[] subExprs);

        ProverExpr mkTupleSelect(ProverExpr tuple, int index);

        ProverExpr mkTupleUpdate(ProverExpr tuple, int index, ProverExpr newVal);

	// Maintain assertion stack (affects assertions and variable declarations)
	void push();

	void pop();

	void addAssertion(ProverExpr assertion);

	/**
	 * Check satisfiability of the currently asserted formulae. Will block until
	 * completion if <code>block</code> argument is true, otherwise return
	 * immediately.
	 */
	ProverResult checkSat(boolean block);

	/**
	 * After a <code>Sat</code> result, continue searching for the next model.
	 * In most ways, this method behaves exactly like <code>checkSat</code>.
	 */
	ProverResult nextModel(boolean block);

	/**
	 * Query result of the last <code>checkSat</code> or <code>nextModel</code>
	 * call. Will block until a result is available if <code>block</code>
	 * argument is true, otherwise return immediately.
	 */
	ProverResult getResult(boolean block);
	
	/**
	 * Query the rechability of the relation
	 */
	ProverResult query(ProverExpr relation, boolean isTimed);
	
        /**
         * Query result of the last <code>checkSat</code> or <code>nextModel</code>
	 * call. Will block until a result is available, or until <code>timeout</code>
	 * milli-seconds elapse.
	 */
	ProverResult getResult(long timeout);

	/**
	 * Stop a running prover. If the prover had already terminated, give same
	 * result as <code>getResult</code>, otherwise <code>Unknown</code>.
	 */
	ProverResult stop();

	/**
	 * Construct proofs in subsequent <code>checkSat</code> calls. Proofs are
	 * needed for extract interpolants.
	 */
	void setConstructProofs(boolean b);

	/**
	 * Construct proofs in subsequent <code>checkSat</code> calls. Proofs are
	 * needed for extract interpolants.
	 */
	void setHornLogic(boolean b);
	
	
	/**
	 * Add subsequent assertions to the partition with index <code>num</code>.
	 * Index <code>-1</code> represents formulae belonging to all partitions
	 * (e.g., theory axioms).
	 */
	void setPartitionNumber(int num);

	/**
	 * If the last call to <code>checkSat</code> returned <code>Unsat</code>,
	 * compute an inductive sequence of interpolants for the given ordering of
	 * the input formulae. Each element of <code>partitionSeq</code> represents
	 * a set of input formulae.
	 */
	ProverExpr[] interpolate(int[][] partitionSeq);

	/**
	 * Listeners that are notified about the results of (non-blocking) calls to
	 * <code>checkSat</code> or <code>nextModel</code>.
	 */
	void addListener(ProverListener listener);

	/**
	 * Evaluate an expression to a literal, in the current model of the prover.
	 * This function must only be used if the result of the last
	 * <code>checkSat</code> or <code>nextModel</code> was <code>Sat</code>.
	 */
	ProverExpr evaluate(ProverExpr expr);

	/**
	 * Determine the set of free variables occurring in the given expression.
	 */
	ProverExpr[] freeVariables(ProverExpr expr);

	/**
	 * Simultaneously substitute <code>from</code> with <code>to</code>
	 * in <code>target</code>. <code>from</code> has to be an array of
	 * free or bound variables.
	 */
	ProverExpr substitute(ProverExpr target,
	                      ProverExpr[] from, ProverExpr[] to);

	/**
	 * Shutdown this prover and release resources. (Might stop threads used
	 * internally to implement the prover, etc.)
	 */
	void shutdown();

	/**
	 * Reset to initial state
	 */
	void reset();

    ////////////////////////////////////////////////////////////////////////////
    // Horn clause interface

    ProverExpr mkHornVariable(String name, ProverType type);

    ProverFun mkHornPredicate(String name, ProverType[] argTypes);
    
    /**
     * The head literal can either be constructed using
     * <code>mkHornPredicate</code>, or be the formula <code>false</code>.
     */
	ProverHornClause mkHornClause(ProverExpr head, ProverExpr[] body,
								  ProverExpr constraint);

//	ProverHornClause mkHornClause(ProverExpr head, ProverExpr[] body,
//								  ProverExpr constraint, String name);


//	void initializeStringHornClauses(Iterable<ProverHornClause> stringHornClauses);
//
//    boolean isInitializedStringHornClauses();

    void addRule(ProverExpr hornRule);
    
    
    void printRules();
    
    
    ProverExpr getCex();

    ////////////////////////////////////////////////////////////////////////////
    // Some functions for outputing SMT-LIB

    String toSMTLIBDeclaration(ProverFun fun);

    String toSMTLIBFormula(ProverHornClause clause);

    String toSMTLIBScript(List<ProverHornClause> clauses);

    void parseSMTLIBFormula(final String formula);
}
