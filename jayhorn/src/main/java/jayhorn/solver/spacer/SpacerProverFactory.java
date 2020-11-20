/**
 * 
 */
package jayhorn.solver.spacer;

import jayhorn.Log;
import jayhorn.solver.*;

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

	@Override
	public ProverADT spawnStringADT() {

		return new ProverADT() {
			@Override
			public ProverType getType(int typeIndex) {
				throw new RuntimeException("not implemented");
			}

			@Override
			public ProverExpr mkHavocExpr(int typeIndex) {
				throw new RuntimeException("not implemented");
			}

			@Override
			public ProverExpr mkCtorExpr(int ctorIndex, ProverExpr[] args) {
				throw new RuntimeException("not implemented");
			}

			@Override
			public ProverExpr mkSelExpr(int ctorIndex, int selIndex, ProverExpr term) {
				throw new RuntimeException("not implemented");
			}

			@Override
			public ProverExpr mkTestExpr(int ctorIndex, ProverExpr term) {
				throw new RuntimeException("not implemented");
			}

			@Override
			public ProverExpr mkSizeExpr(ProverExpr term) {
				throw new RuntimeException("not implemented");
			}
		};

	}

}
