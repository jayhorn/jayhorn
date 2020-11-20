package jayhorn.solver.princess;

import jayhorn.solver.*;

import ap.theories.ADT;
import ap.types.MonoSortedIFunction;
import scala.Tuple2;
import scala.collection.mutable.ArrayBuffer;

import static jayhorn.solver.princess.PrincessProver.type2Sort;

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

    @Override
    public ProverExpr mkHavocExpr(int typeIndex) {
        throw new RuntimeException("not implemented");
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
        assert (ctorNames.length == ctorTypes.length &&
                ctorNames.length == ctorArgTypes.length &&
                ctorNames.length == selectorNames.length);

        final ArrayBuffer<String> sortNames = new ArrayBuffer<>();
        for (int i = 0; i < typeNames.length; ++i)
            sortNames.$plus$eq(typeNames[i]);

        final ArrayBuffer<Tuple2<String, ADT.CtorSignature>> ctors =
                new ArrayBuffer<>();
        for (int i = 0; i < ctorNames.length; ++i) {
            assert (ctorArgTypes[i].length == selectorNames[i].length);

            final ADT.ADTSort resSort = new ADT.ADTSort(ctorTypes[i]);

            final ArrayBuffer<Tuple2<String, ADT.CtorArgSort>> args =
                    new ArrayBuffer<>();
            for (int j = 0; j < ctorArgTypes[i].length; ++j) {
                final ProverType type = ctorArgTypes[i][j];
                final ADT.CtorArgSort argSort;
                if (type instanceof ADTTempType)
                    argSort = new ADT.ADTSort(((ADTTempType) type).typeIndex);
                else
                    argSort = new ADT.OtherSort(type2Sort(type));
                args.$plus$eq(new Tuple2(selectorNames[i][j], argSort));
            }

            ctors.$plus$eq(new Tuple2(ctorNames[i],
                    new ADT.CtorSignature(args, resSort)));
        }

        final ADT adt = new ADT(sortNames, ctors, ADT.TermMeasure$.MODULE$.Size());
        return new PrincessADT(adt);
    }

}
