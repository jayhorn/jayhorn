package soottocfg.cfg.type;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class ReferenceType extends Type {

	public static final String RefFieldName = "$Ref";
	public static final String TypeFieldName = "$Type";
	
	public static final String AllocationSiteFieldName = "$AllocSite";

	private static final long serialVersionUID = 4056715121602313972L;
	private final ClassVariable classVariable;

	private final Map<String, Type> elementTypes;

	public ReferenceType(ClassVariable var) {
		classVariable = var;
		elementTypes = new LinkedHashMap<String, Type>();
		elementTypes.put(RefFieldName, IntType.instance() );
		elementTypes.put(TypeFieldName, new TypeType());
		if (soottocfg.Options.v().useAllocationSiteTupleElement) {
			elementTypes.put(AllocationSiteFieldName, IntType.instance() );
		}
		
		if (classVariable != null) {			
			for (Variable finalField : classVariable.getFinalFields()) {
				// TODO: don't look for final - look for all fields that
				// are only written to once, and the rhs is a constant.
				elementTypes.put(finalField.getName(), finalField.getType());
			}
		}
	}

	private static ReferenceType instance = new ReferenceType(null);
	public static Type instance() {
		return instance;
	}

	
	public ClassVariable getClassVariable() {
		return classVariable;
	}

	public Map<String, Type> getElementTypes() {
		return this.elementTypes;
	}

	public List<Type> getElementTypeList() {
		return new LinkedList<Type>(this.elementTypes.values());
	}

	public String toString() {
		if (classVariable == null) {
			return "Null";
		} else {
			return classVariable.getName().replace('/', '.');
		}
	}

	public boolean isNull() {
		return (classVariable == null);
	}
}
