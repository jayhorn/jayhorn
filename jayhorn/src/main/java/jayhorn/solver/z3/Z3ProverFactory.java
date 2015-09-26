/**
 * 
 */
package jayhorn.solver.z3;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
import jayhorn.util.Log;

/**
 * @author schaef
 *
 */
public class Z3ProverFactory implements ProverFactory {

	/* (non-Javadoc)
	 * @see jhorn.solver.ProverFactory#spawn()
	 */
	@Override
	public Prover spawn() {
		Prover z3 = null;
		try {
			z3 = new Z3Prover();
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
