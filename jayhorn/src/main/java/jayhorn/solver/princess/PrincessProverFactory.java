package jayhorn.solver.princess;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;

public class PrincessProverFactory implements ProverFactory {

	@Override
	public Prover spawn() {
		return new PrincessProver();
	}

	@Override
	public Prover spawnWithLog(String basename) {
		return new PrincessProver(basename);
	}

}
