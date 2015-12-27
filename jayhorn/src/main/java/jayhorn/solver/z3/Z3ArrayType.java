package jayhorn.solver.z3;

import com.microsoft.z3.ArraySort;

import jayhorn.solver.ArrayType;
import jayhorn.solver.ProverType;


public class Z3ArrayType extends ArrayType {

	private final ProverType indexType, valueType;
	private final ArraySort sort;

	public Z3ArrayType(ArraySort s, ProverType idx, ProverType val) {
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
		Z3ArrayType other = (Z3ArrayType) obj;
		if (sort != other.getSort())
			return false;
		return true;
	}

	public String toString() {
		return "Array(" + this.indexType + " " + this.valueType + ")";
	}

}
