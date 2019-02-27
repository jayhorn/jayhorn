package jayhorn.solver.princess;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverADT;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.StringADTFactory;

public class PrincessProverFactory implements ProverFactory {

	private final StringADTFactory stringADTFactory;

	public PrincessProverFactory() {
		this.stringADTFactory = new PrincessStringADTFactory();
	}

	public PrincessProverFactory(StringADTFactory stringADTFactory) {
		this.stringADTFactory = stringADTFactory;
	}

	@Override
	public Prover spawn() {
		return new PrincessProver();
	}

	@Override
	public Prover spawnWithLog(String basename) {
		return new PrincessProver(basename);
	}

	@Override
	public ProverADT spawnStringADT() {
		return stringADTFactory.spawnStringADT();
	}

}
