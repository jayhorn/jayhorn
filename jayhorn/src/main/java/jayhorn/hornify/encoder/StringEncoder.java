package jayhorn.hornify.encoder;

import jayhorn.hornify.HornHelper;
import jayhorn.solver.*;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.literal.StringLiteral;
import soottocfg.cfg.variable.Variable;
import jayhorn.Options;

import com.google.common.base.Verify;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StringEncoder {

    public static enum StringEncoding {
        recursive,
        iterative
    }

    public static final String BOOLEAN_REF_TEMPLATE = "$bool_%d";
    public static final String BOOLEAN_STARTS_WITH_TEMPLATE = "$starts_with(%s, %s)";

    public static final String STRING_REF_TEMPLATE = "$str_%d";
    public static final String STRING_CONCAT_TEMPLATE = "$contact(%s, %s)";

    public static final String STRING_CONCAT = "string_concat";
    public static final String STRING_CONCAT_ITERATIVE = STRING_CONCAT + "_it";

    public static final String STRING_STARTS_WITH = "string_starts_with";

    public static final int MAX_SIZE_HINT = 8;

    public static class EncodingFacts {
        final ProverExpr rely, guarantee, result, constraint;
        public EncodingFacts(ProverExpr rely, ProverExpr guarantee, ProverExpr result, ProverExpr constraint) {
            this.rely = rely;
            this.guarantee = guarantee;
            this.result = result;
            this.constraint = constraint;
        }
    }
    
    private Prover p;
    private ProverADT stringADT;

    private LinkedList<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

    public StringEncoder(Prover p, ProverADT stringADT) {
        this.p = p;
        this.stringADT = stringADT;
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
        return p.mkHornPredicate(mkName(STRING_CONCAT_ITERATIVE),
                                 new ProverType[]{
                                     stringADTType, stringADTType,
                                     stringADTType, stringADTType,
                                     stringADTType
                                 });
    }

    private int nameCounter = 0;

    private String mkName(String base) {
        String res = base + "_" + nameCounter;
        ++nameCounter;
        return res;
        // [Shamakhi] Previously there was a wrong assumption on predicate names:
        //      The base name is sufficient for predicates (mkName function can be removed)
//        return base;
    }
    
    private ProverFun mkStringConcatProverFun() {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(mkName(STRING_CONCAT),
                                 new ProverType[]{
                                     stringADTType, stringADTType,
                                     stringADTType
                                 });
    }

    private ProverFun mkStringStartsWithProverFun() {
        ProverType stringADTType = stringADT.getType(0);
        return p.mkHornPredicate(mkName(STRING_STARTS_WITH),
                new ProverType[]{
                        stringADTType, stringADTType, p.getBooleanType()
                });
    }

    private void considerHintedSizeConcat(ProverFun predConcat) {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr b = p.mkHornVariable("b", stringADTType);
        ProverExpr c = p.mkHornVariable("c", stringADTType);
        ProverExpr left = nil();
        ProverExpr concat = c;
        for (int leftSize = 0; leftSize <= MAX_SIZE_HINT; leftSize++) {
            storeProverHornClause(p.mkHornClause(
                    predConcat.mkExpr(left, b, concat),
                    new ProverExpr[0],
                    p.mkEq(b, c)
            ));
            ProverExpr h = p.mkHornVariable("h" + leftSize, p.getIntType());
            left = cons(h, left);
            concat = cons(h, concat);
        }
    }

    private void considerHintedSizeStartsWith(ProverFun predStartsWith) {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr a = p.mkHornVariable("a", stringADTType);
        ProverExpr z = p.mkHornVariable("z", p.getIntType());
        ProverExpr str = a;
        ProverExpr sub = nil();
        for (int subSize = 0; subSize <= MAX_SIZE_HINT; subSize++) {
            storeProverHornClause(p.mkHornClause(
                    predStartsWith.mkExpr(str, sub, p.mkLiteral(true)),
                    new ProverExpr[0],
                    p.mkLiteral(true)
            ));
            storeProverHornClause(p.mkHornClause(
                    predStartsWith.mkExpr(sub, cons(z, str), p.mkLiteral(false)),
                    new ProverExpr[0],
                    p.mkLiteral(true)
            ));
            ProverExpr h = p.mkHornVariable("h" + subSize, p.getIntType());
            str = cons(h, str);
            sub = cons(h, sub);
        }
    }

    private ProverFun genConcatRec() {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr a = p.mkHornVariable("a", stringADTType);
        ProverExpr b = p.mkHornVariable("b", stringADTType);
        ProverExpr c = p.mkHornVariable("c", stringADTType);
        ProverExpr h = p.mkHornVariable("h", p.getIntType());
        // String Concatenation
        ProverFun predConcat = mkStringConcatProverFun();
        // string_concat nil case
        storeProverHornClause(p.mkHornClause(
                predConcat.mkExpr(nil(), b, b),
                new ProverExpr[0],
                p.mkLiteral(true)
        ));
        ProverExpr ha = cons(h, a);
        ProverExpr hc = cons(h, c);
        // string_concat cons case
        storeProverHornClause(p.mkHornClause(
                predConcat.mkExpr(ha, b, hc),
                new ProverExpr[] {predConcat.mkExpr(a, b, c)},
                p.mkGt(stringADT.mkSizeExpr(ha), p.mkLiteral(MAX_SIZE_HINT))
//                p.mkLiteral(true)
        ));

        return predConcat;
    }

    private ProverFun genStartsWithRec() {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr a = p.mkHornVariable("a", stringADTType);
        ProverExpr b = p.mkHornVariable("b", stringADTType);
        ProverExpr h = p.mkHornVariable("h", p.getIntType());
        ProverExpr j = p.mkHornVariable("j", p.getIntType());
        ProverExpr k = p.mkHornVariable("k", p.getIntType());
        ProverExpr z = p.mkHornVariable("z", p.getIntType());
        ProverExpr ha = cons(h, a);
        ProverExpr hb = cons(h, b);
        ProverExpr ja = cons(j, a);
        ProverExpr kb = cons(k, b);
        // String StartsWith
        ProverFun predStartsWith = mkStringStartsWithProverFun();
        // nil case
        storeProverHornClause(p.mkHornClause(
                predStartsWith.mkExpr(a, nil(), p.mkLiteral(true)),
                new ProverExpr[0],
                p.mkLiteral(true)
        ));
        storeProverHornClause(p.mkHornClause(
                predStartsWith.mkExpr(nil(), cons(z, a), p.mkLiteral(false)),
                new ProverExpr[0],
                p.mkLiteral(true)
        ));
        // cons case
        storeProverHornClause(p.mkHornClause(
                predStartsWith.mkExpr(ha, hb, p.mkLiteral(true)),
                new ProverExpr[] {predStartsWith.mkExpr(a, b, p.mkLiteral(true))},
                p.mkAnd(p.mkGeq(stringADT.mkSizeExpr(ha), stringADT.mkSizeExpr(hb)),
                        p.mkGt(stringADT.mkSizeExpr(hb), p.mkLiteral(MAX_SIZE_HINT)))
//                p.mkLiteral(true)
        ));
        storeProverHornClause(p.mkHornClause(
                predStartsWith.mkExpr(ja, kb, p.mkLiteral(false)),
                new ProverExpr[0],
                p.mkNot(p.mkEq(j, k))
//                p.mkLiteral(true)
        ));


        return predStartsWith;
    }

    private ProverFun genConcatIter() {
        ProverType stringADTType = stringADT.getType(0);
        ProverExpr a = p.mkHornVariable("a", stringADTType);
        ProverExpr b = p.mkHornVariable("b", stringADTType);
        ProverExpr c = p.mkHornVariable("c", stringADTType);
        ProverExpr r = p.mkHornVariable("r", stringADTType);
        ProverExpr t = p.mkHornVariable("t", stringADTType);
        ProverExpr h = p.mkHornVariable("h", p.getIntType());
        // String Concatenation
        ProverFun predConcatIter = mkStringConcatIterativeProverFun();
        ProverFun predConcat = mkStringConcatProverFun();
        // string_concat_iterative initial condition
        storeProverHornClause(p.mkHornClause(
                predConcatIter.mkExpr(a, b, a, nil(), b),   // base case
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
                predConcatIter.mkExpr(a, b, nil(), t, cons(h, c)),
                new ProverExpr[] {predConcatIter.mkExpr(a, b, nil(), cons(h, t), c)},
                p.mkLiteral(true)
        ));
        storeProverHornClause(p.mkHornClause(
                predConcat.mkExpr(a, b, c),
                new ProverExpr[] {predConcatIter.mkExpr(a, b, nil(), nil(), c)},    // (?) problem matching a = nil on base case
                p.mkGt(stringADT.mkSizeExpr(a), p.mkLiteral(MAX_SIZE_HINT))
//                p.mkLiteral(true)
        ));

        return predConcat;
    }

    private ProverExpr head(ProverExpr expr) {
        return stringADT.mkSelExpr(1, 0, expr);
    }

    private ProverExpr tail(ProverExpr expr) {
        return stringADT.mkSelExpr(1, 1, expr);
    }

    private ProverExpr nil() {
        return stringADT.mkCtorExpr(0, new ProverExpr[0]);
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

    public static ProverExpr mkBooleanHornVariable(Prover p, String name) {
        if (name == null) {
            int id = HornHelper.hh().newVarNum();
            name = String.format(BOOLEAN_REF_TEMPLATE, id);
        }
        return p.mkHornVariable(name, p.getBooleanType());
    }

    private ProverExpr selectString(ProverExpr expr) {
        if (expr instanceof ProverTupleExpr) {
            return p.mkTupleSelect(expr, 3);
        } else {
            return expr;
        }
    }

    private ProverExpr selectString(Expression expr, Map<Variable, ProverExpr> varMap) {
        if (expr instanceof StringLiteral) {
            return mkString(((StringLiteral)expr).getValue());
        } else if (expr instanceof IdentifierExpression) {
            ProverExpr pe = varMap.get(((IdentifierExpression)expr).getVariable());
            Verify.verify(pe != null, "cannot extract string from " + expr);
            return selectString(pe);
        } else {
            Verify.verify(false, "cannot extract string from " + expr);
            return null;
        }
    }

    public EncodingFacts mkStringConcat(ProverExpr left, ProverExpr right, ReferenceType stringType) {
        ProverExpr leftString = selectString(left);
        ProverExpr rightString = selectString(right);
        String concatName = String.format(STRING_CONCAT_TEMPLATE, leftString.toString(), rightString.toString());
        ProverExpr concat = mkStringHornVariable(p, concatName, stringType);
        ProverExpr concatString = selectString(concat);
        ProverFun predConcat;
        StringEncoding enc = Options.v().getStringEncoding();
        switch (enc) {
            case recursive:
                predConcat = genConcatRec();
                break;
            case iterative:
                predConcat = genConcatIter();
                break;
            default:
                throw new RuntimeException("unhandled string encoding");
        }
        considerHintedSizeConcat(predConcat);
        ProverExpr guarantee = predConcat.mkExpr(leftString, rightString, concatString);
        return new EncodingFacts(
                null, guarantee, concat,
                // need to make sure that the result is non-null
                p.mkNot(p.mkEq(p.mkTupleSelect(concat, 0), p.mkLiteral(0)))
        );
    }

    public EncodingFacts mkStringStartsWith(ProverExpr left, ProverExpr right) {
        ProverExpr leftString = selectString(left);
        ProverExpr rightString = selectString(right);
        String resultName = String.format(BOOLEAN_STARTS_WITH_TEMPLATE, leftString.toString(), rightString.toString());
        ProverExpr result = mkBooleanHornVariable(p, resultName);
        ProverFun predStartsWith;
        predStartsWith = genStartsWithRec();    // TODO: Iterative
        considerHintedSizeStartsWith(predStartsWith);
        ProverExpr guarantee = predStartsWith.mkExpr(leftString, rightString, result);
        return new EncodingFacts(null, guarantee, result, p.mkLiteral(true));
    }

    public EncodingFacts handleStringExpr(Expression e, Map<Variable, ProverExpr> varMap) {
        if (e instanceof BinaryExpression) {
            final BinaryExpression be = (BinaryExpression) e;
            switch (be.getOp()) {
                case StringConcat: {
                    Expression leftExpr = be.getLeft();
                    Expression rightExpr = be.getRight();
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringConcat(leftPE, rightPE, (ReferenceType)leftExpr.getType());
                }

                case StringEq: {
                    final ProverExpr leftPE = selectString(be.getLeft(), varMap);
                    final ProverExpr rightPE = selectString(be.getRight(), varMap);
                    return new EncodingFacts(null, null, p.mkEq(leftPE, rightPE), p.mkLiteral(true));
                }

                case StartsWith: {
                    Expression leftExpr = be.getLeft();
                    Expression rightExpr = be.getRight();
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringStartsWith(leftPE, rightPE);
                }

                case ToString: {
                    Expression stringContainerExpr = be.getLeft();
                    Expression lhsStringExpr = be.getRight();
                    final ProverExpr internalString = selectString(stringContainerExpr, varMap);
                    if (internalString == null)
                        return null;
                    ProverExpr result = mkStringHornVariable(p, internalString.toString(), (ReferenceType)lhsStringExpr.getType());
                    ProverExpr resultString = selectString(result);
                    return new EncodingFacts(null, null, result,
                            p.mkAnd(
                                    // need to make sure that the result is non-null
                                    p.mkNot(p.mkEq(p.mkTupleSelect(result, 0), p.mkLiteral(0))),
                                    p.mkEq(resultString, internalString)
                            )
                    );
                }

                default:
                    return null;
            }
        }
        if (e instanceof UnaryExpression) {
            final UnaryExpression ue = (UnaryExpression)e;
            switch (ue.getOp()) {
                case Len: {
                    final ProverExpr stringPE = selectString(ue.getExpression(), varMap);
                    return new EncodingFacts(null, null, stringADT.mkSizeExpr(stringPE), p.mkLiteral(true));
                }

                default:
                    return null;
            }
        }

        return null;
    }

}
