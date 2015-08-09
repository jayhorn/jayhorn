package jayhorn.solver;


public class BoolType implements ProverType {

	public static final BoolType INSTANCE = new BoolType();

	private BoolType() {
	}

	@Override
	public String toString() {
		return "Bool";
	}

}
