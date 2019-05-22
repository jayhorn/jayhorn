package jayhorn.hornify.encoder;

import com.google.common.base.Verify;
import jayhorn.hornify.HornHelper;
import jayhorn.solver.*;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.Variable;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StringEncoder {

    public static final String REF_TEMPLATE = "$ref(%d)";
    public static final String STRING_REF_TEMPLATE = "$string_ref(%d)";
    public static final String STRING_CONCAT_TEMPLATE = "$string_contact(%d, %d)";

//    public static final String STRING_EQUALS = "string_equals";
//    public static final String STRING_EQUALS_RS = STRING_EQUALS + "_ref_str";
//    public static final String STRING_EQUALS_RR = STRING_EQUALS + "_ref_ref";

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

    private ProverExpr mkStringHornVariable(BigInteger id) {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornVariable(
                String.format(STRING_REF_TEMPLATE, id), stringADTType);
    }

    private ProverExpr mkStringHornVariable(int id) {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornVariable(
                String.format(STRING_REF_TEMPLATE, id), stringADTType);
    }

    private ProverExpr mkStringLiteralEq(ProverExpr id, ProverExpr str) {
        ProverExpr strObject = mkStringHornVariable(id.getIntLiteralValue());
        return mkStringEq(strObject, str);
    }

    private ProverExpr mkStringLiteralEq(int id, ProverExpr str) {
        ProverExpr strObject = mkStringHornVariable(id);
        return mkStringEq(strObject, str);
    }

    public void assertStringLiteral(ProverExpr ref, ProverExpr str) {
        if (ref instanceof ProverTupleExpr) {
            ProverExpr id = p.mkTupleSelect(ref, 0);
            p.addAssertion(mkStringLiteralEq(id, str));
        } else {
            throw new RuntimeException("ref must be a ProverTupleExpr");
        }
    }

    private void initializeStringHornClauses() {
        if (!initializedStringHornClauses) {
            ProverType stringADTType = stringADT.getType(0);
            ProverExpr a = p.mkHornVariable("a", stringADTType);
            ProverExpr b = p.mkHornVariable("b", stringADTType);
            ProverExpr r = p.mkHornVariable("r", stringADTType);
            ProverExpr s = p.mkHornVariable("s", stringADTType);
            ProverExpr t = p.mkHornVariable("t", stringADTType);
            ProverExpr h = p.mkHornVariable("h", stringADTType);
            ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
            ProverType refType = HornHelper.hh().getProverType(p, ReferenceType.instance());
            ProverExpr left = p.mkHornVariable("left", refType);
            ProverExpr right = p.mkHornVariable("right", refType);
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
                    new ProverExpr[] {predConcatIter.mkExpr(a, b, cons(h, t), r, s)},
                    p.mkLiteral(true)
            ));
            // string_concat_iterative reversing reverse of a at head of b, results concatenation
            storeProverHornClause(p.mkHornClause(
                    predConcatIter.mkExpr(a, b, empty, t, cons(h, s)),
                    new ProverExpr[] {predConcatIter.mkExpr(a, b, empty, cons(h, t), s)},
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
            // TODO: Exception java.lang.RuntimeException
            ProverExpr leftString = mkStringHornVariable(idLeft.getIntLiteralValue());
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 0);
//                ProverFun predEqRefRef = mkStringEqualsRefRef(idLeft.getType(), idRight.getType());
                ProverExpr rightString = mkStringHornVariable(idRight.getIntLiteralValue());
                return p.mkOr(p.mkEq(leftString, rightString), p.mkEq(idLeft, idRight));
            } else {
                Verify.verify(right.getType().equals(stringADTType), "right in mkStringEq is not a valid string");
//                ProverFun predEqRefStr = mkStringEqualsRefStr(idLeft.getType());
                return p.mkEq(leftString, right);
            }
        } else {
            Verify.verify(left.getType().equals(stringADTType), "left in mkStringEq is not a valid string");
            if (right instanceof ProverTupleExpr) {
                return p.mkEq(right, left);
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

    private ProverExpr cons(ProverExpr expr1, ProverExpr expr2) {
        return stringADT.mkCtorExpr(1, new ProverExpr[]{expr1, expr2});
    }

    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right, Map<Variable, ProverExpr> varMap) {
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 0);
            ProverExpr leftString = mkStringHornVariable(idLeft.getIntLiteralValue());
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 0);
                ProverExpr rightString = mkStringHornVariable(idRight.getIntLiteralValue());
                int idConcat = HornHelper.hh().newVarNum();
                String concatName = String.format(STRING_CONCAT_TEMPLATE,
                        idLeft.getIntLiteralValue(), idRight.getIntLiteralValue());
                ProverType refType = HornHelper.hh().getProverType(p, ReferenceType.instance());
                ProverExpr concat = p.mkHornVariable(String.format(REF_TEMPLATE, idConcat), refType);
                ProverType stringADTType = stringADT.getType(0);
                ProverExpr concatString = p.mkHornVariable(concatName, stringADTType);
                ProverFun predConcat = mkStringConcatProverFun();
                p.addAssertion(p.mkImplies(predConcat.mkExpr(leftString, rightString, concatString),
                        mkStringLiteralEq(idConcat, concatString)));
                return concat;
            } else {
                throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
            }
        } else {
            throw new RuntimeException("operands in mkStringConcat must be object references; other cases not implemented");
        }
    }

}
