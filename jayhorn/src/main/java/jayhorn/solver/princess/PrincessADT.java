package jayhorn.solver.princess;

import jayhorn.solver.ProverADT;
import jayhorn.solver.ProverType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.IntType;

import ap.theories.ADT;
import ap.types.Sort;
import ap.types.MonoSortedIFunction;

/**
 * Class representing algebraic data-types
 */
public class PrincessADT implements ProverADT {

    private final ADT adt;

    private final PrincessFun[] constructors;
    private final PrincessFun[][] selectors;
    private final PrincessFun[] sizeOps;

    public PrincessADT(ADT adt) {
        this.adt = adt;

        constructors = new PrincessFun[adt.constructors().size()];
        selectors = new PrincessFun[adt.constructors().size()][];
        sizeOps = new PrincessFun[adt.termSize().size()];

        for (int i = 0; i < constructors.length; ++i) {
            final MonoSortedIFunction f = adt.constructors().apply(i);
            constructors[i] =
                new PrincessFun (f, PrincessProver.sort2Type(f.resSort()));

            selectors[i] = new PrincessFun [f.arity()];
            for (int j = 0; j < selectors[i].length; ++j) {
                final MonoSortedIFunction g = adt.selectors().apply(i).apply(j);
                selectors[i][j] =
                    new PrincessFun (g, PrincessProver.sort2Type(g.resSort()));
            }
        }

        for (int i = 0; i < sizeOps.length; ++i) {
            sizeOps[i] =
                new PrincessFun(adt.termSize().apply(i), IntType.INSTANCE);
        }
    }

    /**
     * Query the individual ADT types
     */
    public ProverType getType(int typeIndex) {
        return new PrincessADTType (adt.sorts().apply(typeIndex));
    }

    /**
     * Build constructor terms, using the ctorIndex'th constructor
     */
    public ProverExpr mkCtorExpr(int ctorIndex, ProverExpr[] args) {
        return constructors[ctorIndex].mkExpr(args);
    }

    /**
     * Build selector terms, using the selIndex'th selector for the
     * ctorIndex'th constructor
     */
    public ProverExpr mkSelExpr(int ctorIndex, int selIndex, ProverExpr term) {
        return selectors[ctorIndex][selIndex].mkExpr(new ProverExpr[] { term });
    }
    
    /**
     * Build a tester formula, testing whether the given term has the
     * ctorIndex'th constructor as head symbol
     */
    public ProverExpr mkTestExpr(int ctorIndex, ProverExpr term) {
        return new FormulaExpr (adt.hasCtor(((PrincessProverExpr)term).toTerm(),
                                            ctorIndex));
    }

    /**
     * Build a size term for the given ADT term
     */
    public ProverExpr mkSizeExpr(ProverExpr term) {
        final ProverType type = term.getType();

        if (type instanceof PrincessADTType) {
            final int index = ((PrincessADTType)type).getTypeIndex();
            return sizeOps[index].mkExpr(new ProverExpr[] { term });
        }

        throw new IllegalArgumentException();
    }

}
