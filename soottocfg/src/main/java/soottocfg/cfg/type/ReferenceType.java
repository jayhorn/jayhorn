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

//	private static HashMap<String, LinkedHashMap<String, Type>> DEBUG_SIZE = new HashMap<String, LinkedHashMap<String, Type>> (); 

    public static final String RefFieldName = "$Ref";
    public static final String TypeFieldName = "$Type";
    public static final String AllocationSiteFieldName = "$AllocSite";

    private static final long serialVersionUID = 4056715121602313972L;
    private final ClassVariable classVariable;

    private LinkedHashMap<String, Type> elementTypes;	// LinkedHashMap is needed to preserve order

    public ReferenceType(ClassVariable var, LinkedHashMap<String, Type> elementTypes) {
        classVariable = var;
        this.elementTypes = elementTypes;
    }

    public ReferenceType(ClassVariable var) {
        this(var, null);
    }

    public ClassVariable getClassVariable() {
        return classVariable;
    }

    public static LinkedHashMap<String, Type> mkDefaultElementTypes() {
        LinkedHashMap<String, Type> elementTypes = new LinkedHashMap<String, Type>();
        elementTypes.put(RefFieldName, IntType.instance() );
        elementTypes.put(TypeFieldName, new TypeType());
        if (soottocfg.Options.v().useAllocationSiteTupleElement) {
            elementTypes.put(AllocationSiteFieldName, IntType.instance() );
        }
        return elementTypes;
    }

    public Map<String, Type> getElementTypes() {
        if (this.elementTypes==null) {
            /*
             * Compute element types on the fly to avoid problems
             * with recursive definitions.
             */
            elementTypes = mkDefaultElementTypes();

            if (classVariable != null) {
                for (Variable finalField : classVariable.getInlineableFields()) {
                    // TODO: don't look for final - look for all fields that
                    //       are only written to once, and the rhs is a constant.
                    elementTypes.put(finalField.getName(), finalField.getType());
                }
            }
//
//			if (classVariable!=null) {
//			if (DEBUG_SIZE.containsKey(classVariable.getName())) {
//				if(DEBUG_SIZE.get(classVariable.getName()).size()!=elementTypes.size()) {
//					System.err.println(classVariable.getName());
//					System.err.println("A: " + this.elementTypes.keySet());
//					System.err.println("A: " + this.elementTypes.values());
//					System.err.println("B: " + DEBUG_SIZE.get(classVariable.getName()).keySet());
//					System.err.println("B: " + DEBUG_SIZE.get(classVariable.getName()).values());
//					Verify.verify(false);
//				}
//			}
//			DEBUG_SIZE.put(classVariable.getName(), (LinkedHashMap<String, Type>) elementTypes);
//			}
        }

        return this.elementTypes;
    }

    public List<Type> getElementTypeList() {
        return new LinkedList<Type>(getElementTypes().values());
    }

    public String toString() {
        if (classVariable == null) {
            return "Null";
        } else {
            return classVariable.getName().replace('/', '.');
        }
    }

    @Override
    public int hashCode() {
        return 17 * classVariable.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReferenceType other = (ReferenceType) obj;
        return this.classVariable.equals(other.classVariable);
    }

    public boolean isNull() {
        return (classVariable == null);
    }

}