/**
 * 
 */
package soottocfg.cfg.type;

import java.util.LinkedList;
import java.util.List;

/**
 * @author schaef
 *
 */
public class MapType extends Type {

	private final List<Type> indices;
	private final Type valueType; 
	
	/**
	 * 
	 */
	public MapType(List<Type> ids, Type v) {
		indices = new LinkedList<Type>(ids);
		valueType = v;
	}

	public Type[] getIndices() {
		return indices.toArray(new Type[indices.size()]);
	}
	
	public Type getValueType() {
		return valueType;
	}
	
	@Override
	public int hashCode() {
		int result = indices.hashCode();
		result = 37 * result + valueType.hashCode();
		return result;
	}
	
	@Override 
	public boolean equals(Object other) {
		if (other instanceof MapType) {
			MapType mt = (MapType)other;
			Type[] ids = mt.getIndices();
			if (ids.length==indices.size()) {
				for (int i=0; i<ids.length; i++) {
					if (!ids[i].equals(indices.get(i))) {
						return false;
					}
				}
				if (!mt.getValueType().equals(valueType)) {
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Map<");
		String comma = "";
		for (Type t : indices) {
			sb.append(comma);
			sb.append(t);
			comma = ", ";
		}
		sb.append(">[");
		sb.append(valueType);
		sb.append("]");
		return sb.toString();
	}
}
