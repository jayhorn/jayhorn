package jayhorn.solver;


public class ADTTempType implements ProverType {

        public final int typeIndex;

	public ADTTempType(int typeIndex) {
            this.typeIndex = typeIndex;
	}

	@Override
	public String toString() {
		return "ADT[" + typeIndex + "]";
	}

}
