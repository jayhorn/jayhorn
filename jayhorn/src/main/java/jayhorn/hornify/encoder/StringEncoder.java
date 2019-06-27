package jayhorn.hornify.encoder;

import jayhorn.hornify.HornHelper;
import jayhorn.solver.*;
import soottocfg.cfg.type.ReferenceType;

import java.util.LinkedList;
import java.util.List;

public class StringEncoder {

    public static final String STRING_REF_TEMPLATE = "$string_ref(%s)";
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

    private void storeProverAssertion(ProverExpr proverAssertion) {
        ProverHornClause proverHornClause = p.mkHornClause(
                p.mkLiteral(false), new ProverExpr[0], p.mkNot(proverAssertion)
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
        return p.mkHornPredicate(STRING_CONCAT, new ProverType[]{
                IntType.INSTANCE, IntType.INSTANCE, IntType.INSTANCE
        });
    }


    public void assertStringLiteral(ProverExpr ref, ProverExpr str, ReferenceType stringType) {
        if (ref instanceof ProverTupleExpr) {
            ProverExpr idStr = p.mkTupleSelect(ref, 3);
            storeProverAssertion(mkStringEq(idStr, str, stringType));
            // FIXME:
            //          Horn:   false :- $string("a")_260#3 != cons(97, nil).
            //          SMT2:   (assert (forall ((var0 List[Int])) (= var0 (cons 97 nil))))
        } else {
            throw new RuntimeException("ref must be a ProverTupleExpr with size of 4");
        }
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
            ProverExpr left = mkNewStringHornVariable(p, "left", stringType);
            ProverExpr leftString = p.mkHornVariable("leftString", stringADTType);
            ProverExpr idLeft = p.mkTupleSelect(left, 3);
            ProverExpr right = mkNewStringHornVariable(p, "right", stringType);
            ProverExpr rightString = p.mkHornVariable("rightString", stringADTType);
            ProverExpr idRight = p.mkTupleSelect(right, 3);
            ProverExpr concat = mkNewStringHornVariable(p, "concat", stringType);
            ProverExpr concatString = p.mkHornVariable("concatString", stringADTType);
            ProverExpr idConcat = p.mkTupleSelect(concat, 3);
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
                    predConcat.mkExpr(idLeft, idRight, idConcat),
                    new ProverExpr[] {predConcatIter.mkExpr(leftString, rightString, empty, empty, concatString)},
                    p.mkAnd(p.mkEq(idLeft, leftString), p.mkEq(idRight, rightString),
                            p.mkEq(idConcat, concatString))
            ));

            initializedStringHornClauses = true;
        }
    }

    public ProverExpr mkStringEq(ProverExpr left, ProverExpr right, ReferenceType stringType) {
        initializeStringHornClauses(stringType);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 3);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringEq(idLeft, idRight, stringType);
            } else {
                return mkStringEq(idLeft, right, stringType);
            }
        } else {
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringEq(left, idRight, stringType);
            } else {
                return p.mkEq(left, right);
            }
        }
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

    public static ProverExpr mkNewStringHornVariable(Prover p, String name,
                                                     ReferenceType stringType) {
        int id = HornHelper.hh().newVarNum();
        ProverType refType = HornHelper.hh().getProverType(p, stringType);
        if (name == null) {
            name = String.format(STRING_REF_TEMPLATE, id);
        }
        return p.mkHornVariable(name, refType);
    }

    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right,
                                     ReferenceType stringType) {
        initializeStringHornClauses(stringType);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 3);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringConcat(idLeft, idRight, stringType);
            } else {
                return mkStringConcat(idLeft, right, stringType);
            }
        } else {
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringConcat(left, idRight, stringType);
            } else {
                String concatName = String.format(STRING_CONCAT_TEMPLATE,
                        left.toString(), right.toString());
                ProverExpr concatString = mkNewStringHornVariable(p, concatName, stringType);
                ProverExpr idConcat = p.mkTupleSelect(concatString, 3);
                ProverFun predConcat = mkStringConcatProverFun();
                storeProverAssertion(predConcat.mkExpr(left, right, idConcat));
                return concatString;
            }
        }
    }

}
