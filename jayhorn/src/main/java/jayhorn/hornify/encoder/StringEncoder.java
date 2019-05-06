package jayhorn.hornify.encoder;

import com.google.common.base.Verify;
import jayhorn.solver.*;

import java.util.LinkedList;
import java.util.List;

public class StringEncoder {

    public static final String STRING_EQUALS = "string_equals";
    public static final String STRING_EQUALS_RS = STRING_EQUALS + "_ref_str";
    public static final String STRING_EQUALS_RR = STRING_EQUALS + "_ref_ref";

    public static final String STRING_CONCAT = "string_concat";
    public static final String STRING_CONCAT_ITERATIVE = STRING_CONCAT + "_iterative";

    private Prover p;
    private ProverADT stringADT;

    private LinkedList<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
    private boolean initializedStringHornClauses = false;

    public StringEncoder(Prover p, ProverADT stringADT) {
        this.p = p;
        this.stringADT = stringADT;
        initializeStringHornClauses();
    }

    private void storeProverHornClause(ProverHornClause proverHornClause) {
//        p.addAssertion(proverHornClause);
        clauses.add(proverHornClause);
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

    private ProverFun mkStringConcatProverFun() {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(STRING_CONCAT, new ProverType[]{
                stringADTType, stringADTType, stringADTType
        });
    }

    private ProverExpr mkStringLiteralAssertion(ProverExpr ref, ProverExpr ste) {
        return p.mkHornPredicate(STRING_EQUALS_RS, new ProverType[]{ref.getType(), ste.getType()})
                .mkExpr(ref, ste);
    }

    public void assertStringLiteral(ProverExpr ref, ProverExpr ste) {
        if (ref instanceof ProverTupleExpr) {
            ProverExpr id = p.mkTupleSelect(ref, 0);
            storeProverHornClause(p.mkHornClause(
                    mkStringLiteralAssertion(id, ste),
                    new ProverExpr[] {},
                    p.mkLiteral(true)
            ));
        } else {
            throw new RuntimeException("ref must be a ProverTupleExpr");
        }
    }

    private void initializeStringHornClauses() {
        if (!initializedStringHornClauses) {
            ProverType stringADTType = stringADT.getType(0);
            ProverExpr a = p.mkVariable("a", stringADTType);
            ProverExpr b = p.mkVariable("b", stringADTType);
            ProverExpr r = p.mkVariable("r", stringADTType);
            ProverExpr s = p.mkVariable("s", stringADTType);
            ProverExpr t = p.mkVariable("t", stringADTType);
            ProverExpr h = p.mkVariable("h", stringADTType);
            ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
            ProverExpr left = p.mkVariable("left", p.getTupleType(new ProverType[] {p.getIntType(), p.getIntType(), p.getIntType()}));
            ProverExpr right = p.mkVariable("right", p.getTupleType(new ProverType[] {p.getIntType(), p.getIntType(), p.getIntType()}));
            ProverExpr idLeft = p.mkTupleSelect(left, 0);
            ProverExpr idRight = p.mkTupleSelect(right, 0);
            // String Equals
            ProverFun predEqRefStr = mkStringEqualsRefStr(idLeft.getType());
            ProverFun predEqRefRef = mkStringEqualsRefRef(idLeft.getType(), idRight.getType());
            // string_equals is reflexive
            storeProverHornClause(p.mkHornClause(
                    predEqRefRef.mkExpr(idLeft, idRight),
                    new ProverExpr[0],
                    p.mkEq(idLeft, idRight)
            ));
            // string_equals is symmetric
            storeProverHornClause(p.mkHornClause(
                    predEqRefRef.mkExpr(idRight, idLeft),
                    new ProverExpr[] {predEqRefRef.mkExpr(idLeft, idRight)},
                    p.mkLiteral(true)
            ));
            // string_equals is transitive
            storeProverHornClause(p.mkHornClause(
                    predEqRefRef.mkExpr(idLeft, idRight),
                    new ProverExpr[] {predEqRefStr.mkExpr(idLeft, s), predEqRefStr.mkExpr(idRight, s)},
                    p.mkLiteral(true)
            ));
            // each string object has exactly one string value
            storeProverHornClause(p.mkHornClause(
                    p.mkLiteral(false),
                    new ProverExpr[] {predEqRefRef.mkExpr(idLeft, idRight), predEqRefStr.mkExpr(idLeft, s), predEqRefStr.mkExpr(idRight, t)},
                    p.mkNot(p.mkEq(s, t))
            ));
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
                    predConcatIter.mkExpr(a, b, t, cons(h, r), s),
                    new ProverExpr[]{predConcatIter.mkExpr(a, b, cons(h, t), r, s)},
                    p.mkLiteral(true)
            ));
            // string_concat_iterative reversing reverse of a at head of b, results concatenation
            storeProverHornClause(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, empty, t, cons(h, s)),
                    new ProverExpr[]{predConcatIter.mkExpr(a, b, empty, cons(h, t), s)},
                    p.mkLiteral(true)
            ));
            storeProverHornClause(p.mkHornClause(
                    predConcat.mkExpr(a, b, s),
                    new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, empty, s)},
                    p.mkLiteral(true)
            ));

            initializedStringHornClauses = true;
        }
    }

    public ProverExpr mkStringEq(ProverExpr left, ProverExpr right) {
        ProverType stringADTType = stringADT.getType(0);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 0);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 0);
                ProverFun predEqRefRef = mkStringEqualsRefRef(idLeft.getType(), idRight.getType());
                return predEqRefRef.mkExpr(idLeft, idRight);
            } else {
                Verify.verify(right.getType().equals(stringADTType), "right in mkStringEq is not a valid string");
                ProverFun predEqRefStr = mkStringEqualsRefStr(idLeft.getType());
                return predEqRefStr.mkExpr(idLeft, right);
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

    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right) {
        ProverType stringADTType = stringADT.getType(0);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 0);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 0);
                ProverExpr concat = p.mkVariable("$string_concat_" + idLeft+ "_" + idRight, left.getType());
                ProverFun predConcat = mkStringConcatProverFun();
                storeProverHornClause(p.mkHornClause(
                        predConcat.mkExpr(left, right, concat),
                        new ProverExpr[0],
                        p.mkLiteral(true)
                ));
                return concat;
            } else {
                throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
            }
        } else {
            throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
        }
    }

}
