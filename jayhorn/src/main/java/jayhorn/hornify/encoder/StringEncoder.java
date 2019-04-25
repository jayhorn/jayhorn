package jayhorn.hornify.encoder;

import com.google.common.base.Verify;
import jayhorn.solver.*;

import java.util.LinkedList;

public class StringEncoder {

    public static final String STRING_EQUALS = "string_equals";
    public static final String STRING_EQUALS_RS = STRING_EQUALS + "_ref_str";
    public static final String STRING_EQUALS_RR = STRING_EQUALS + "_ref_ref";
    public static final String STRING_NOT_EQUALS = "string_not_equals";
    public static final String STRING_NOT_EQUALS_RR = STRING_NOT_EQUALS + "_ref_ref";

    public static final String STRING_CONCAT = "string_concat";
    public static final String STRING_CONCAT_ITERATIVE = STRING_CONCAT + "_iterative";

    private Prover p;
    private ProverADT stringADT;

    public StringEncoder(Prover p, ProverADT stringADT) {
        this.p = p;
        this.stringADT = stringADT;
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

    private ProverFun mkStringEqualsRefRef(ProverType idLeftType, ProverType idRightType) {
        return p.mkHornPredicate(STRING_EQUALS_RR, new ProverType[]{idLeftType, idRightType});
    }

    private ProverFun mkStringEqualsRefStr(ProverType idLeftType) {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(STRING_EQUALS_RS, new ProverType[]{idLeftType, stringADTType});
    }

    private ProverFun mkStringConcatIterativeProverFun() {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(STRING_CONCAT_ITERATIVE, new ProverType[]{
                stringADTType, stringADTType, stringADTType, stringADTType, stringADTType
        });
    }

    private ProverExpr mkStringLiteralAssertion(ProverExpr ref, ProverExpr ste) {
        return p.mkHornPredicate(STRING_EQUALS_RS, new ProverType[]{ref.getType(), ste.getType()})
                .mkExpr(ref, ste);
    }

    public void assertStringLiteral(ProverExpr ref, ProverExpr ste) {
        if (ref instanceof ProverTupleExpr) {
            ProverExpr id = ((ProverTupleExpr) ref).getSubExpr(0);
            p.addAssertion(mkStringLiteralAssertion(id, ste));
        } else {
            throw new RuntimeException("ref must be a ProverTupleExpr");
        }
    }

    private void initializeStringHornClauses() {
        if (!p.isInitializedStringHornClauses()) {
            ProverType stringADTType = stringADT.getType(0);
            LinkedList<ProverHornClause> stringHornClauses = new LinkedList<ProverHornClause>();
            ProverExpr a = p.mkVariable("a", stringADTType);
            ProverExpr b = p.mkVariable("b", stringADTType);
            ProverExpr r = p.mkVariable("r", stringADTType);
            ProverExpr s = p.mkVariable("s", stringADTType);
            ProverExpr t = p.mkVariable("t", stringADTType);
            ProverExpr h = p.mkVariable("h", stringADTType);
            ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
            ProverExpr idLeft = p.mkVariable("idLeft", p.getIntType());
            ProverExpr idRight = p.mkVariable("idRight", p.getIntType());
            // String Equals
            ProverFun predEqRefStr = mkStringEqualsRefStr(idLeft.getType());
            ProverFun predEqRefRef = mkStringEqualsRefRef(idLeft.getType(), idRight.getType());
            // string_equals is reflexive
            stringHornClauses.add(p.mkHornClause(
                    predEqRefRef.mkExpr(idLeft, idLeft),
                    new ProverExpr[0],
                    p.mkLiteral(true)
            ));
            // string_equals is symmetric
            stringHornClauses.add(p.mkHornClause(
                    predEqRefRef.mkExpr(idRight, idLeft),
                    new ProverExpr[] {predEqRefRef.mkExpr(idLeft, idRight)},
                    p.mkLiteral(true)
            ));
            // string_equals is transitive
            stringHornClauses.add(p.mkHornClause(
                    predEqRefRef.mkExpr(idLeft, idRight),
                    new ProverExpr[] {predEqRefStr.mkExpr(idLeft, s), predEqRefStr.mkExpr(idRight, s)},
                    p.mkLiteral(true)
            ));
            // each string object has exactly one string value
            stringHornClauses.add(p.mkHornClause(
                    p.mkLiteral(false),
                    new ProverExpr[] {predEqRefStr.mkExpr(idLeft, s), predEqRefStr.mkExpr(idLeft, t)},
                    p.mkNot(p.mkEq(s, t))
            ));
            // String Concatenation
            ProverFun predConcatIter = mkStringConcatIterativeProverFun();
            // string_concat_iterative initial condition
            stringHornClauses.add(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, a, empty, b),
                    new ProverExpr[0],
                    p.mkLiteral(true)
            ));
            // string_concat_iterative reversing a
            stringHornClauses.add(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, t, cons(h, r), s),
                    new ProverExpr[]{predConcatIter.mkExpr(a, b, cons(h, t), r, s)},
                    p.mkLiteral(true)
            ));
            // string_concat_iterative reversing reverse of a at head of b, results concatenation
            stringHornClauses.add(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, empty, t, cons(h, s)),
                    new ProverExpr[]{predConcatIter.mkExpr(a, b, empty, cons(h, t), s)},
                    p.mkLiteral(true)
            ));

            p.initializeStringHornClauses(stringHornClauses);
        }
    }

    // using initializeStringHornClauses()
//    public ProverExpr mkStringEq(ProverExpr left, ProverExpr right) {
//        initializeStringHornClauses();
//        ProverType stringADTType = stringADT.getType(0);
//        if (left instanceof ProverTupleExpr) {
//            ProverExpr idLeft = ((ProverTupleExpr) left).getSubExpr(0);
//            if (right instanceof ProverTupleExpr) {
//                ProverExpr idRight = ((ProverTupleExpr) right).getSubExpr(0);
//                ProverFun predEqRefRef = mkStringEqualsRefRef(idLeft.getType(), idRight.getType());
//                return predEqRefRef.mkExpr(idLeft, idRight);
//            } else {
//                Verify.verify(right.getType().equals(stringADTType), "right in mkStringEq is not a valid string");
//                ProverFun predEqRefStr = mkStringEqualsRefStr(idLeft.getType());
//                return predEqRefStr.mkExpr(idLeft, right);
//            }
//        } else {
//            Verify.verify(left.getType().equals(stringADTType), "left in mkStringEq is not a valid string");
//            if (right instanceof ProverTupleExpr) {
//                return mkStringEq(right, left);
//            } else {
//                return p.mkEq(left, right);     // both of stringADTType
//            }
//        }
//    }
    // not using initializeStringHornClauses()
    public ProverExpr mkStringEq(ProverExpr left, ProverExpr right) {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr s = p.mkVariable("s", stringADTType);
        ProverExpr t = p.mkVariable("t", stringADTType);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = ((ProverTupleExpr) left).getSubExpr(0);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = ((ProverTupleExpr) right).getSubExpr(0);
                ProverFun prr = p.mkHornPredicate(STRING_EQUALS_RR, new ProverType[]{idLeft.getType(), idRight.getType()});
                ProverFun nrr = p.mkHornPredicate(STRING_NOT_EQUALS_RR, new ProverType[]{idLeft.getType(), idRight.getType()});
                // TODO: Where is the right place to add this assertion? Is it better to add it once instead of for each mkStringEq?
                // string_equals is reflexive
                p.addAssertion(p.mkHornClause(
                        prr.mkExpr(idLeft, idRight),
                        new ProverExpr[0],
                        p.mkEq(idLeft, idRight)
                ));
                // string_equals is symmetric
                p.addAssertion(p.mkHornClause(
                        prr.mkExpr(idLeft, idRight),
                        new ProverExpr[] {prr.mkExpr(idRight, idLeft)},
                        p.mkNot(p.mkEq(idLeft, idRight))
                ));
                // string_equals is transitive
                p.addAssertion(p.mkHornClause(
                        prr.mkExpr(idLeft, idRight),
                        new ProverExpr[] {mkStringEq(left, s), mkStringEq(right, s)},
                        p.mkLiteral(true)
                ));
                // each string object has exactly one string value
                p.addAssertion(p.mkHornClause(
                        p.mkLiteral(false),
                        new ProverExpr[] {mkStringEq(left, s), mkStringEq(right, t)},
                        p.mkNot(p.mkEq(s, t))
                ));
//                return prr.mkExpr(idLeft, idRight); // TODO: throws java.util.NoSuchElementException: key not found: string_equals_ref_ref/2
                return p.mkOr(p.mkEq(idLeft, idRight), prr.mkExpr(idLeft, idRight));
            } else {
                Verify.verify(right.getType().equals(stringADTType), "right in mkStringEq is not a valid string");
                ProverFun prs = p.mkHornPredicate(STRING_EQUALS_RS, new ProverType[]{idLeft.getType(), right.getType()});
                return prs.mkExpr(idLeft, right);   // right of stringADTType
            }
        } else {
            Verify.verify(left.getType().equals(stringADTType), "left in mkStringEq is not a valid string");
            if (right instanceof ProverTupleExpr) {
                return mkStringEq(right, left);
            } else {
                return p.mkEq(left, right);     // both of stringADTType
            }
        }
    }

    private ProverExpr head(ProverExpr expr) {
        return stringADT.mkSelExpr(1, 0, expr);
    }

    private ProverExpr tail(ProverExpr expr) {
        return stringADT.mkSelExpr(1, 1, expr);
    }

    private ProverExpr cons(ProverExpr expr1, ProverExpr expr2) {
        return stringADT.mkCtorExpr(1, new ProverExpr[]{expr1, expr2});
    }

    // using initializeStringHornClauses()
//    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right) {
//        initializeStringHornClauses();
//        ProverType stringADTType = stringADT.getType(0);
//        ProverExpr a = p.mkVariable("a", stringADTType);
//        ProverExpr b = p.mkVariable("b", stringADTType);
//        ProverExpr c = p.mkVariable("c", stringADTType);
//        ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
//        ProverFun predConcatIter = mkStringConcatIterativeProverFun();
//        if (left instanceof ProverTupleExpr) {
//            ProverExpr idLeft = ((ProverTupleExpr) left).getSubExpr(0);
//            if (right instanceof ProverTupleExpr) {
//                ProverExpr idRight = ((ProverTupleExpr) right).getSubExpr(0);
//                ProverExpr concat = p.mkVariable("$string_concat_" + idLeft+ "_" + idRight, left.getType());
//                ProverExpr idConcat = ((ProverTupleExpr) concat).getSubExpr(0);
//                ProverFun predEqRefStr = mkStringEqualsRefStr(idConcat.getType());
//                p.addAssertion(p.mkHornClause(
//                        predEqRefStr.mkExpr(idConcat, c),
//                        new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, empty, c)},
//                        p.mkLiteral(true)
//                ));
//                return concat;
//            } else {
//                throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
//            }
//        } else {
//            throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
//        }
//    }
    // not using initializeStringHornClauses()
    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right) {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr a = p.mkVariable("a", stringADTType);
        ProverExpr b = p.mkVariable("b", stringADTType);
        ProverExpr r = p.mkVariable("r", stringADTType);
        ProverExpr c = p.mkVariable("c", stringADTType);
        ProverExpr t = p.mkVariable("t", stringADTType);
        ProverExpr h = p.mkVariable("h", stringADTType);
        ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = ((ProverTupleExpr) left).getSubExpr(0);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = ((ProverTupleExpr) right).getSubExpr(0);
                ProverExpr concat = p.mkVariable("concat_" + idLeft+ "_" + idRight, left.getType());
                ProverFun predConcatIter = p.mkHornPredicate(STRING_CONCAT_ITERATIVE, new ProverType[]{
                        a.getType(), b.getType(), a.getType(), empty.getType(), b.getType()
                });
                p.addAssertion(p.mkHornClause(
                        predConcatIter.mkExpr(a, b, a, empty, b),
                        new ProverExpr[] {mkStringEq(left, a), mkStringEq(right, b)},
                        p.mkLiteral(true)
//                        , "CAT1"
                ));
                p.addAssertion(p.mkHornClause(
                        predConcatIter.mkExpr(a, b, t, cons(h, r), c),
                        new ProverExpr[] {predConcatIter.mkExpr(a, b, cons(h, t), r, c)},
                        p.mkLiteral(true)
//                        , "CAT2"
                ));
                p.addAssertion(p.mkHornClause(
                        predConcatIter.mkExpr(a, b, empty, t, cons(h, c)),
                        new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, cons(h, t), c)},
                        p.mkLiteral(true)
//                        , "CAT3"
                ));
                p.addAssertion(p.mkHornClause(
                        mkStringEq(concat, c),
                        new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, empty, c)},
                        p.mkLiteral(true)
//                        , "CAT4"
                ));
                // TODO: The assertions that was just added to the prover are not present at SMT Horn clauses
                return concat;
            } else {
                throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
            }
        } else {
            throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
        }
    }

}
