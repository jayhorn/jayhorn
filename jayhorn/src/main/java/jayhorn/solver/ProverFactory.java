package jayhorn.solver;

public interface ProverFactory {

	public Prover spawn();

	public Prover spawnWithLog(String basename);

	public ProverADT spawnStringADT();

}
