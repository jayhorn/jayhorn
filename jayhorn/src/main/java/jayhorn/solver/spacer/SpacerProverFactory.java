/**
 * 
 */
package jayhorn.solver.spacer;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;

/**
 * @author schaef
 *
 */
public class SpacerProverFactory implements ProverFactory {

	/* (non-Javadoc)
	 * @see jhorn.solver.ProverFactory#spawn()
	 */
	@Override
	public Prover spawn() {
		Prover z3 = null;
		try {
			z3 = new SpacerProver();
//			z3 = new Z3HornProver();
		} catch (UnsatisfiedLinkError e) {
			Log.error("Cannot start z3. "+e.toString());
		}
		return z3;
	}

	/* (non-Javadoc)
	 * @see jhorn.solver.ProverFactory#spawnWithLog(java.lang.String)
	 */
	@Override
	public Prover spawnWithLog(String basename) {
		return spawn();
	}

}
