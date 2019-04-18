package jayhorn.hornify.encoder;

import com.google.common.base.Verify;
//import jayhorn.solver.*;
import jayhorn.solver.*;

public class StringEncoder {

    public static final String STRING_EQUALS = "string_equals";
    public static final String STRING_EQUALS_RS = STRING_EQUALS + "_ref_str";
    public static final String STRING_EQUALS_RR = STRING_EQUALS + "_ref_ref";
    public static final String STRING_NOT_EQUALS = "string_not_equals";
    public static final String STRING_NOT_EQUALS_RR = STRING_NOT_EQUALS + "_ref_ref";

    public static final String STRING_CONCAT = "string_concat";
    public static final String STRING_CONCAT_CONT = STRING_CONCAT + "_cont";

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
                p.addAssertion(p.mkHornClause(
                        prr.mkExpr(idLeft, idRight),
                        new ProverExpr[] {mkStringEq(left, s), mkStringEq(right, s)},
                        p.mkLiteral(true)
                ));
                p.addAssertion(p.mkHornClause(
                        nrr.mkExpr(idLeft, idRight),
                        new ProverExpr[] {mkStringEq(left, s), mkStringEq(right, t)},
                        p.mkNot(p.mkEq(s, t))
                ));
                return p.mkOr(p.mkEq(idLeft, idRight), prr.mkExpr(idLeft, idRight));
            } else {
                Verify.verify(right.getType().equals(stringADTType), "right in mkStringEq is not a valid string");
                ProverFun prs = p.mkHornPredicate(STRING_EQUALS_RS, new ProverType[]{idLeft.getType(), right.getType()});
                return prs.mkExpr(idLeft, right);
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
        ProverExpr a = p.mkVariable("a", stringADTType);
        ProverExpr b = p.mkVariable("b", stringADTType);
//        ProverExpr s1 = p.mkVariable("s1", stringADTType);
        ProverExpr s2 = p.mkVariable("s2", stringADTType);
        ProverExpr s3 = p.mkVariable("s3", stringADTType);
        ProverExpr ts = p.mkVariable("ts", stringADTType);
        ProverExpr hs = p.mkVariable("hs", stringADTType);
        ProverExpr empty = stringADT.mkCtorExpr(0, new ProverExpr[0]);
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = ((ProverTupleExpr) left).getSubExpr(0);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = ((ProverTupleExpr) right).getSubExpr(0);
                ProverExpr concat = p.mkVariable("concat_" + idLeft+ "_" + idRight, left.getType());
                ProverExpr idConcat = ((ProverTupleExpr) concat).getSubExpr(0);
                ProverFun pred = p.mkHornPredicate(STRING_CONCAT_CONT, new ProverType[]{
                        a.getType(), b.getType(), a.getType(), empty.getType(), b.getType()
                });
                p.addAssertion(p.mkHornClause(
                        pred.mkExpr(a, b, a, empty, b),
                        new ProverExpr[] {mkStringEq(left, a), mkStringEq(right, b)},
                        p.mkLiteral(true)
                ));
                p.addAssertion(p.mkHornClause(
                        pred.mkExpr(a, b, ts, cons(hs, s2), s3),
                        new ProverExpr[] {pred.mkExpr(a, b, cons(hs, ts), s2, s3)},
                        p.mkLiteral(true)
                ));
                p.addAssertion(p.mkHornClause(
                        pred.mkExpr(a, b, empty, ts, cons(hs, s3)),
                        new ProverExpr[] {pred.mkExpr(a, b, empty, cons(hs, ts), s3)},
                        p.mkLiteral(true)
                ));
                ProverFun prs = p.mkHornPredicate(STRING_EQUALS_RS, new ProverType[]{idConcat.getType(), s3.getType()});
                p.addAssertion(p.mkHornClause(
                        prs.mkExpr(idConcat, s3),
                        new ProverExpr[] {pred.mkExpr(a, b, empty, empty, s3)},
                        p.mkLiteral(true)
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
