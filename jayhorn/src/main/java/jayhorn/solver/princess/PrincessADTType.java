package jayhorn.solver.princess;

import jayhorn.solver.ProverType;

import ap.types.Sort;
import ap.theories.ADT.ADTProxySort;

public class PrincessADTType implements ProverType {
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
        if (getClass() != obj.getClass())
            return false;
        PrincessADTType other = (PrincessADTType) obj;
        if (sort == null) {
            if (other.sort != null)
                return false;
        } else if (!sort.equals(other.sort))
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
