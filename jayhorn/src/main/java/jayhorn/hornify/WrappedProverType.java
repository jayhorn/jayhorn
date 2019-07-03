package jayhorn.hornify;

import jayhorn.solver.ProverType;

import soottocfg.cfg.type.Type;

/**
 * CFG type wrapping an arbitrary prover type
 */
public class WrappedProverType extends Type {

    private final ProverType wrappedType;

    public WrappedProverType(ProverType wrappedType) {
        this.wrappedType = wrappedType;
    }

    public ProverType getProverType() {
        return wrappedType;
    }

    public String toString() {
        return wrappedType.toString();
    }
  
    @Override
    public int hashCode() {
        return 23 * wrappedType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WrappedProverType other = (WrappedProverType) obj;
        return this.wrappedType.equals(other.wrappedType);
    }

}
