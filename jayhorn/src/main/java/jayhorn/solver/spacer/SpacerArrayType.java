package jayhorn.solver.spacer;

import com.microsoft.z3.ArraySort;

import jayhorn.solver.ArrayType;
import jayhorn.solver.ProverType;


public class SpacerArrayType extends ArrayType {

	private final ProverType indexType, valueType;
	private final ArraySort sort;

	public SpacerArrayType(ArraySort s, ProverType idx, ProverType val) {
		//TODO:
		super(2);
		this.indexType = idx;
		this.valueType = val;
		this.sort = s;
	}

	public ArraySort getSort() {
		return this.sort;
	}
	
	public ProverType getIndexType() {
		return this.indexType;
	}
	
	public ProverType getValueType() {
		return this.valueType;
	}
	
	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + arity;
//		return result;
		throw new RuntimeException("not implemented");
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SpacerArrayType other = (SpacerArrayType) obj;
		if (sort != other.getSort())
			return false;
		return true;
	}

	public String toString() {
		return "Array(" + this.indexType + " " + this.valueType + ")";
	}

}
