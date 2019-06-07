package jayhorn.hornify.encoder;

import com.google.common.base.Verify;
import jayhorn.hornify.HornHelper;
import jayhorn.solver.*;
import soot.RefType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class StringEncoder {

    public static final String STRING_REF_TEMPLATE = "$string_ref(%s)";
    public static final String STRING_CONCAT_TEMPLATE = "$contact(%s, %s)";

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
        p.addAssertion(proverHornClause);
//        clauses.add(proverHornClause);
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


    public void assertStringLiteral(ProverExpr ref, ProverExpr str) {
        if (ref instanceof ProverTupleExpr) {
            ProverExpr id = p.mkTupleSelect(ref, 3);
            p.addAssertion(mkStringEq(id, str));
        } else {
            throw new RuntimeException("ref must be a ProverTupleExpr with size of 4");
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
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 3);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringEq(idLeft, idRight);
            } else {
                return mkStringEq(idLeft, right);
            }
        } else {
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringEq(left, idRight);
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

    public static ReferenceType mkStringReferenceType(ClassVariable var) {
        if (var == null) {
            // FIXME: java.lang.NullPointerException at lookupClassVariable()
            var = SootTranslationHelpers.v().getMemoryModel().lookupClassVariable(
                    SootTranslationHelpers.v().getClassConstant(RefType.v("java.lang.String")));    // TODO: cache
        }
        LinkedHashMap<String, Type> elementTypes = ReferenceType.mkDefaultElementTypes();
        elementTypes.put("$String", IntType.instance());
        return new ReferenceType(var, elementTypes);
    }

    public static ProverExpr mkNewStringHornVariable(Prover p, String name) {
        int id = HornHelper.hh().newVarNum();
        ProverType refType = HornHelper.hh().getProverType(p, mkStringReferenceType(null));
        if (name == null) {
            name = String.format(STRING_REF_TEMPLATE, id);
        }
        return p.mkHornVariable(name, refType);
    }

    public ProverExpr mkStringConcat(ProverExpr left, ProverExpr right) {
        if (left instanceof ProverTupleExpr) {
            ProverExpr idLeft = p.mkTupleSelect(left, 3);
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringConcat(idLeft, idRight);
            } else {
                return mkStringConcat(idLeft, right);
            }
        } else {
            if (right instanceof ProverTupleExpr) {
                ProverExpr idRight = p.mkTupleSelect(right, 3);
                return mkStringConcat(left, idRight);
            } else {
                String concatName = String.format(STRING_CONCAT_TEMPLATE,
                        left.toString(), right.toString());
                ProverExpr concatString = mkNewStringHornVariable(p, concatName);
                ProverFun predConcat = mkStringConcatProverFun();
                p.addAssertion(predConcat.mkExpr(left, right, concatString));
                return concatString;
            }
        }
    }

}
