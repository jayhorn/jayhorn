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
		Prover spacer = null;
		try {
			spacer = new SpacerProver();
		} catch (UnsatisfiedLinkError e) {
			Log.error("Cannot start z3. "+e.toString());
		}
		return spacer;
	}

	/* (non-Javadoc)
	 * @see jhorn.solver.ProverFactory#spawnWithLog(java.lang.String)
	 */
	@Override
	public Prover spawnWithLog(String basename) {
		return spawn();
	}

}
