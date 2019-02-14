package jayhorn.solver;

import ap.theories.ADT;
import scala.Tuple2;
import scala.collection.mutable.ArrayBuffer;

import static jayhorn.solver.princess.PrincessProver.type2Sort;

/**
 * Interface representing algebraic data-types
 */
public interface ProverADT {

    /**
     * Query the individual ADT types
     */
    ProverType getType(int typeIndex);

    /**
     * Build constructor terms, using the ctorIndex'th constructor
     */
    ProverExpr mkCtorExpr(int ctorIndex, ProverExpr[] args);

    /**
     * Build selector terms, using the selIndex'th selector for the
     * ctorIndex'th constructor
     */
    ProverExpr mkSelExpr(int ctorIndex, int selIndex, ProverExpr term);
    
    /**
     * Build a tester formula, testing whether the given term has the
     * ctorIndex'th constructor as head symbol
     */
    ProverExpr mkTestExpr(int ctorIndex, ProverExpr term);

    /**
     * Build a size term for the given ADT term
     */
    ProverExpr mkSizeExpr(ProverExpr term);

    static ADT getADT(String[]       typeNames,
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

        return new ADT(sortNames, ctors, ADT.TermMeasure$.MODULE$.Size());
    }

}
