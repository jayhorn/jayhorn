package jayhorn.solver.princess;

import jayhorn.solver.ProverADT;
import jayhorn.solver.ProverType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.IntType;

import ap.theories.ADT;
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

    public ProverType getType(int typeIndex) {
        return new PrincessADTType (adt.sorts().apply(typeIndex));
    }

    public ProverExpr mkCtorExpr(int ctorIndex, ProverExpr[] args) {
        return constructors[ctorIndex].mkExpr(args);
    }

    public ProverExpr mkSelExpr(int ctorIndex, int selIndex, ProverExpr term) {
        return selectors[ctorIndex][selIndex].mkExpr(new ProverExpr[] { term });
    }

    public ProverExpr mkTestExpr(int ctorIndex, ProverExpr term) {
        return new FormulaExpr (adt.hasCtor(((PrincessProverExpr)term).toTerm(),
                                            ctorIndex));
    }

    public ProverExpr mkSizeExpr(ProverExpr term) {
        final ProverType type = term.getType();

        if (type instanceof PrincessADTType) {
            final int index = ((PrincessADTType)type).getTypeIndex();
            return sizeOps[index].mkExpr(new ProverExpr[] { term });
        }

        throw new IllegalArgumentException();
    }

    /**
     * Define an algebraic data-type with the given sort, constructor,
     * and selector names and types
     * @param typeNames namings of this ADT
     * @param ctorNames ADT constructors names
     * @param ctorTypes ADT constructors types
     * @param ctorArgTypes argument types of ADT constructors
     * @param selectorNames ADT selectors names
     * @return generated ProverADT
     */
    public static ProverADT mkADT(String[]       typeNames,
                           String[]       ctorNames,
                           int[]          ctorTypes,
                           ProverType[][] ctorArgTypes,
                           String[][]     selectorNames) {
        return new PrincessADT(ProverADT.getADT(typeNames, ctorNames, ctorTypes, ctorArgTypes, selectorNames));
    }

}
