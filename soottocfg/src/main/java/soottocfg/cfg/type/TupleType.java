/**
 * 
 */
package soottocfg.cfg.type;

import java.util.LinkedList;
import java.util.List;

/**
 * @author schaef
 * Tuple types are used to simplify encoding of references. Instead of
 * of encoding a reference and its dynamic type as seperate fields, we
 * encode them as a single variable of tuple type because they should 
 * always be passed around together.
 */
public class TupleType extends ReferenceLikeType {

	private static final long serialVersionUID = 4056715121602313972L;
	private final List<Type> elementTypes = new LinkedList<Type>();

	/**
	 * 
	 */
	public TupleType(List<Type> elementTypes) {
		this.elementTypes.addAll(elementTypes);
	}

	public List<Type> getElementTypes() {
		return this.elementTypes;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String seperator = "(";
		for (Type t : elementTypes) {
			sb.append(seperator);
			sb.append(t.toString());
			seperator = ", ";
		}
		sb.append(")");
		return sb.toString();
	}

}
