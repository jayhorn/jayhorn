package jayhorn.solver.princess;

import jayhorn.solver.*;

import ap.theories.ADT.ADTProxySort;

public class PrincessADTType implements ProverADTType {
    public final ADTProxySort sort;

    protected PrincessADTType(ADTProxySort sort) {
        this.sort = sort;
    }

    public int getTypeIndex() {
        return sort.sortNum();
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        // TODO: maybe move this comparison to another method
        if (obj instanceof ProverTupleType) {
            // check reference to object
            ProverTupleType ptt = (ProverTupleType) obj;
            if (ptt.getArity() != 3)
                return false;
            ProverType[] subTypes = ptt.getSubTypes();
            for (ProverType subType: subTypes) {
                if (subType != IntType.INSTANCE)
                    return false;
            }
            return true;
        }
        if (getClass() != obj.getClass())
            return false;
        PrincessADTType other = (PrincessADTType) obj;
        if (sort == null) {
            if (other.sort != null)
                return false;
//      } else if (!sort.equals(other.sort))    // FIXME: not equals when different objects with same properties
        // Temp Fix:
        } else if (sort.sortNum() != other.sort.sortNum()
                || !sort.name().equals(other.sort.name())
                || !sort.adtTheory().constructors().canEqual(other.sort.adtTheory().constructors())
                || !sort.adtTheory().selectors().canEqual(other.sort.adtTheory().selectors()))
            return false;
        return true;
    }

    public int hashCode() {
        return sort.hashCode() + 17;
    }

    public String toString() {
        return sort.toString();
    }
    
}
