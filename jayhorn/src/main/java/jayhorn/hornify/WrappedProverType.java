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
  

}
