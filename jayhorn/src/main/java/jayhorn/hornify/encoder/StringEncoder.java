package jayhorn.hornify.encoder;

import jayhorn.hornify.HornHelper;
import jayhorn.solver.*;
import soottocfg.cfg.type.ReferenceType;

import java.util.LinkedList;
import java.util.List;

public class StringEncoder {

    public static final String STRING_REF_TEMPLATE = "$str_%d";
    public static final String STRING_CONCAT_TEMPLATE = "$contact(%s, %s)";

    public static final String STRING_CONCAT = "string_concat";
    public static final String STRING_CONCAT_ITERATIVE = STRING_CONCAT + "_iterative";

    private Prover p;
    private ProverADT stringADT;

    private LinkedList<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
    private boolean initializedStringHornClauses = false;

    public StringEncoder(Prover p, ProverADT stringADT) {
        this.p = p;
        this.stringADT = stringADT;
    }

    private void storeProverHornClause(ProverHornClause proverHornClause) {
//        p.addAssertion(proverHornClause);
        clauses.add(proverHornClause);
    }

    private void storeProverAssumption(ProverExpr proverAssumption) {
        ProverHornClause proverHornClause = p.mkHornClause(
                proverAssumption, new ProverExpr[0], p.mkLiteral(true)
        );
        storeProverHornClause(proverHornClause);
    }

    public List<ProverHornClause> getEncodedClauses() {
        return clauses;
    }

    private ProverExpr mkStringFromCharArray(char[] chars, ProverADT listADT) {
        int index = chars.length - 1;
        ProverExpr res = listADT.mkCtorExpr(0, new ProverExpr[0]);
        while (index >= 0) {
            // TODO: support Unicode characters
            res = listADT.mkCtorExpr(1, new ProverExpr[] {
                    p.mkLiteral(((int)chars[index])), res
            });
            --index;
        }
        return res;
    }

    public ProverExpr mkString(String value) {
        // TODO: support different types of stringADT
        return mkStringFromCharArray(value.toCharArray(), stringADT);
    }


    private ProverFun mkStringConcatIterativeProverFun() {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(STRING_CONCAT_ITERATIVE, new ProverType[]{
                stringADTType, stringADTType, stringADTType, stringADTType, stringADTType
        });
    }

    private ProverFun mkStringConcatProverFun() {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(STRING_CONCAT, new ProverType[]{
                stringADTType, stringADTType, stringADTType
        });
    }

    private void initializeStringHornClauses(ReferenceType stringType) {
        if (!initializedStringHornClauses) {
            ProverType stringADTType = stringADT.getType(0);
            ProverExpr a = p.mkHornVariable("a", stringADTType);
            ProverExpr b = p.mkHornVariable("b", stringADTType);
            ProverExpr c = p.mkHornVariable("c", stringADTType);
            ProverExpr r = p.mkHornVariable("r", stringADTType);
            ProverExpr t = p.mkHornVariable("t", stringADTType);
            ProverExpr h = p.mkHornVariable("h", stringADTType);
            ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
            // String Concatenation
            ProverFun predConcatIter = mkStringConcatIterativeProverFun();
            ProverFun predConcat = mkStringConcatProverFun();
            // string_concat_iterative initial condition
            storeProverHornClause(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, a, empty, b),
                    new ProverExpr[0],
                    p.mkLiteral(true)
            ));
            // string_concat_iterative reversing a
            storeProverHornClause(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, t, cons(h, r), c),
                    new ProverExpr[] {predConcatIter.mkExpr(a, b, cons(h, t), r, c)},
                    p.mkLiteral(true)
            ));
            // string_concat_iterative reversing reverse of a at head of b, results concatenation
            storeProverHornClause(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, empty, t, cons(h, c)),
                    new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, cons(h, t), c)},
                    p.mkLiteral(true)
            ));
            storeProverHornClause(p.mkHornClause(
                    predConcat.mkExpr(a, b, c),
                    new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, empty, c)},
                    p.mkLiteral(true)
            ));

            initializedStringHornClauses = true;
        }
    }

    public ProverExpr mkStringEq(ProverExpr left, ProverExpr right, ReferenceType stringType) {
        initializeStringHornClauses(stringType);
        left = selectString(p, left);
        right = selectString(p, right);
        return p.mkEq(left, right);
    }

    private ProverExpr head(ProverExpr expr) {
        return stringADT.mkSelExpr(1, 0, expr);
    }

    private ProverExpr tail(ProverExpr expr) {
        return stringADT.mkSelExpr(1, 1, expr);
    }

    private ProverExpr cons(ProverExpr h, ProverExpr t) {
        return stringADT.mkCtorExpr(1, new ProverExpr[]{h, t});
    }

    public static ProverExpr mkStringHornVariable(Prover p, String name,
                                                  ReferenceType stringType) {
        ProverType refType = HornHelper.hh().getProverType(p, stringType);
        if (name == null) {
            int id = HornHelper.hh().newVarNum();
            name = String.format(STRING_REF_TEMPLATE, id);
        }
        return p.mkHornVariable(name, refType);
    }

    public static ProverExpr selectString(Prover p, ProverExpr expr) {
        if (expr instanceof ProverTupleExpr) {
            return p.mkTupleSelect(expr, 3);
        } else {
            return expr;
        }
    }

    public ProverExpr mkStringConcatAssumption(ProverExpr left, ProverExpr right,
                                               ReferenceType stringType) {
        initializeStringHornClauses(stringType);
        left = selectString(p, left);
        right = selectString(p, right);
        String concatName = String.format(STRING_CONCAT_TEMPLATE,
                left.toString(), right.toString());
        ProverExpr concat = mkStringHornVariable(p, concatName, stringType);
        ProverExpr concatString = selectString(p, concat);
        ProverFun predConcat = mkStringConcatProverFun();
        return predConcat.mkExpr(left, right, concatString);
    }

    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right,
                                     ReferenceType stringType) {
        initializeStringHornClauses(stringType);
        left = selectString(p, left);
        right = selectString(p, right);
        String concatName = String.format(STRING_CONCAT_TEMPLATE,
                left.toString(), right.toString());
        return mkStringHornVariable(p, concatName, stringType);
    }

}
