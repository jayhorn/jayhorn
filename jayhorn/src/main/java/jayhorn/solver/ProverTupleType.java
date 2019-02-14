package jayhorn.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class ProverTupleType implements ProverType {

    /**
     * Eliminate tuple types by expanding them.
     */
    public static ProverType[] flatten(ProverType[] types) {
        ArrayList<ProverType> res = new ArrayList<ProverType>();
        
        Stack<ProverType> todo = new Stack<ProverType>();
        for (int i = types.length - 1; i >= 0; --i)
            todo.push(types[i]);

        while (!todo.isEmpty()) {
            ProverType next = todo.pop();
            if (next instanceof ProverTupleType) {
                ProverTupleType tnext = (ProverTupleType)next;
                for (int i = tnext.getArity() - 1; i >= 0; --i)
                    todo.push(tnext.getSubType(i));
            } else {
                res.add(next);
            }
        }

        return res.toArray(new ProverType[0]);
    }
    
    private final ProverType[] subTypes;

    public ProverTupleType(ProverType[] subTypes) {
        this.subTypes = subTypes.clone();
    }
    
    public int getArity() {
        return subTypes.length;
    }

    public ProverType getSubType(int ind) {
        return subTypes[ind];
    }

    public ProverType[] getSubTypes() {
        return subTypes.clone();
    }

    
    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("[");
        String sep = " ";
        for (int i = 0; i < getArity(); ++i) {
            res.append(sep);
            sep = " , ";
            res.append(getSubType(i));
        }
        res.append(" ]");

        return res.toString();
    }

    @Override
    public int hashCode() {
        return 12345 * Arrays.hashCode(subTypes);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProverTupleType other = (ProverTupleType) obj;
        return Arrays.equals(this.subTypes, other.subTypes);
    }

}
