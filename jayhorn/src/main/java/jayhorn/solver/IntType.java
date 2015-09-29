package jayhorn.solver;


public class IntType implements ProverType {

	public static final IntType INSTANCE = new IntType();

	private IntType() {
	}

	@Override
	public String toString() {
		return "Int";
	}

}
