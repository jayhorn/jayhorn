package jayhorn.solver;

public interface ProverListener {
	/**
	 * The result determines whether the listener should be kept in the set of
	 * listeners for a prover.
	 */
	boolean proverFinished(Prover prover, ProverResult result);
}
