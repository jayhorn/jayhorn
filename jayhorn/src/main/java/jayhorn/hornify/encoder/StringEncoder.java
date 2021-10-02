package jayhorn.hornify.encoder;

import jayhorn.hornify.HornHelper;
import jayhorn.solver.*;
import soottocfg.cfg.expression.*;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.expression.literal.StringLiteral;
import soottocfg.cfg.variable.Variable;
import jayhorn.Options;

import com.google.common.base.Verify;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StringEncoder {

    public enum StringEncoding {
        recursive,
        recursiveWithPrec,
        iterative
    }

    public enum StringDirection {
        ltr,    // Right to Left
        rtl     // Left to Right
    }

    public static final int MAX_SIZE_HINT = 8;

    public static final ProverExpr[] EMPTY_PHC_BODY = {};   // prevent redundant creation of arrays

    public static class EncodingFacts {
        final ProverExpr rely, guarantee, result, constraint;

        public EncodingFacts(ProverExpr rely, ProverExpr guarantee, ProverExpr result, ProverExpr constraint) {
            this.rely = rely;               // preAtom => rely
            this.guarantee = guarantee;     // constraint & guarantee? & preAtom => postAtom
            this.result = result;           // varMap.put(lhs.var, result)
            this.constraint = constraint;
        }
    }

    private Prover p;
    private ProverADT stringADT;
    private StringEncoding stringEncoding;
    private StringDirection stringDirection;
    private static final int STRING_ADT_TYPE_IDX = 0;
//    private ProverType stringADTType;

    private ProverType getStringADTType() {
        return stringADT.getType(STRING_ADT_TYPE_IDX);
    }

    private ProverExpr len(ProverExpr stringPE) {
        return p.mkMinus(stringADT.mkSizeExpr(stringPE), lit(1));
    }

    private ProverExpr lit(int value) {
        return p.mkLiteral(value);
    }

    private ProverExpr lit(char value) {
        return p.mkLiteral(value);
    }

    private ProverExpr lit(boolean value) {
        return p.mkLiteral(value);
    }

    //    private ProverExpr lit(BigInteger value) { return p.mkLiteral(value); }
    private ProverExpr lit(long value) {
        return p.mkLiteral(BigInteger.valueOf(value));
    }

    private ProverExpr stringHornVar(String name, ProverType stringADTType) {
        return p.mkHornVariable(name, stringADTType);
    }

    private ProverExpr intHornVar(String name) {
        return p.mkHornVariable(name, p.getIntType());
    }

    private ProverExpr booleanHornVar(String name) {
        return p.mkHornVariable(name, p.getBooleanType());
    }

    private ProverExpr charHornVar(String name) {
        return p.mkHornVariable(name, p.getIntType());
    }

    private LinkedList<ProverHornClause> clauses = new LinkedList<>();

    public StringEncoder(Prover p, ProverADT stringADT) {
        this.p = p;
        this.stringADT = stringADT;
//        this.stringADTType = stringADT.getType(STRING_ADT_TYPE_IDX);
        this.stringEncoding = Options.v().getStringEncoding();
        this.stringDirection = Options.v().getStringDirection();
//        if (this.stringDirection == StringDirection.rtl)
//            throw new RuntimeException("not implemented");
    }

    private void storeProverHornClause(ProverHornClause proverHornClause) {
        clauses.add(proverHornClause);
    }

    private void addPHC(ProverExpr head, ProverExpr[] body, ProverExpr constraint) {
        clauses.add(p.mkHornClause(head, body, constraint));
    }

    private void addPHC(ProverExpr head, ProverExpr[] body) {
        clauses.add(p.mkHornClause(head, body, lit(true)));
    }

    private void addPHC(ProverExpr tautology) {
        clauses.add(p.mkHornClause(tautology, EMPTY_PHC_BODY, lit(true)));
    }

    public List<ProverHornClause> getEncodedClauses() {
        return clauses;
    }

    private ProverExpr mkStringPEFromCharArray(char[] chars, ProverADT listADT) {
        // TODO: support Unicode characters
        ProverExpr res = listADT.mkCtorExpr(0, new ProverExpr[0]);
        int index;
        if (stringDirection == StringDirection.ltr) {
            index = chars.length - 1;
            while (index >= 0) {
                res = listADT.mkCtorExpr(1, new ProverExpr[]{
                        lit(chars[index]), res
                });
                --index;
            }
        } else {
            index = 0;
            while (index < chars.length) {
                res = listADT.mkCtorExpr(1, new ProverExpr[]{
                        lit(chars[index]), res
                });
                ++index;
            }
        }
        return res;
    }

    public ProverExpr mkStringPE(@Nullable String value) {
        // TODO: support different types of stringADT
        if (value != null)
            return mkStringPEFromCharArray(value.toCharArray(), stringADT);
        else
            return stringADT.mkHavocExpr(STRING_ADT_TYPE_IDX);
    }

    public ProverExpr mkIndexInString(Expression e, Map<Variable, ProverExpr> varMap) {
        if (e instanceof BinaryExpression) {
            final BinaryExpression be = (BinaryExpression) e;
            Expression leftExpr = be.getLeft();
            Expression rightExpr = be.getRight();
            switch (be.getOp()) {
                case IndexInString: {
                    final ProverExpr strPE = selectString(leftExpr, varMap);
                    final ProverExpr indexPE = selectInt(rightExpr, varMap);
                    return p.mkAnd(p.mkGeq(indexPE, lit(0)), p.mkLt(indexPE, len(strPE)));
                }
                default:
                    return null;
            }
        }

        return null;
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

    private ProverFun mkStringConcatIterativeProverFun(ProverType stringADTType) {
        return p.mkHornPredicate(mkName("string_concat_it"),
                new ProverType[]{stringADTType, stringADTType, stringADTType, stringADTType, stringADTType});
    }

    private ProverFun mkStringConcatProverFun(ProverType stringADTType) {
        return p.mkHornPredicate(mkName("string_concat"),
                new ProverType[]{stringADTType, stringADTType, stringADTType});
    }

    private ProverFun mkStringConcatPreconditionProverFun(ProverType stringADTType) {
        return p.mkHornPredicate(mkName("string_concat_prec"),
                new ProverType[]{stringADTType, stringADTType});
    }

    private ProverFun mkIntToStringHelperProverFun(ProverType stringADTType) {
        final String INT_STRING_H = "int_string_h";
        if (stringDirection == StringDirection.ltr) {
            return p.mkHornPredicate(mkName(INT_STRING_H),
                    new ProverType[]{p.getIntType(), p.getIntType(), stringADTType});   // number, remained_number, str_so_far
        } else {
            return p.mkHornPredicate(mkName(INT_STRING_H),
                    new ProverType[]{p.getIntType(), stringADTType, p.getIntType()});   // number, str_so_far, digit_bound
        }
    }

    private ProverFun mkIntToStringProverFun(ProverType stringADTType) {
        return p.mkHornPredicate(mkName("int_string"),
                new ProverType[]{p.getIntType(), stringADTType});
    }

    private ProverFun mkBoolToStringProverFun(ProverType boolType, ProverType stringADTType) {
        return p.mkHornPredicate(mkName("bool_string"),
                new ProverType[]{(boolType instanceof BoolType) ? p.getBooleanType() : p.getIntType(), stringADTType});
    }

    private ProverFun mkCharToStringProverFun(ProverType stringADTType) {
        return p.mkHornPredicate(mkName("char_string"),
                new ProverType[]{p.getIntType(), stringADTType});
    }

    private ProverFun mkStringEdgesWithProverFun(ProverType stringADTType, boolean startEdge) {
        String predicateName = startEdge ? "string_starts_with" : "string_ends_with";
        return p.mkHornPredicate(mkName(predicateName),
                new ProverType[]{stringADTType, stringADTType, p.getBooleanType()});
    }

    private ProverFun mkStringEdgesWithOffsetProverFun(ProverType stringADTType, boolean startEdge) {
        String predicateName = startEdge ? "string_starts_with_offset" : "string_ends_with_offset";
        return p.mkHornPredicate(mkName(predicateName),
                new ProverType[]{stringADTType, stringADTType, p.getIntType(), p.getBooleanType()});
    }

    private ProverFun mkStringCharAtProverFun(ProverType stringADTType) {
        return p.mkHornPredicate(mkName("string_char_at"),
                new ProverType[]{stringADTType, p.getIntType(), p.getIntType()});
    }

    private ProverFun mkStringCompareToProverFun(ProverType stringADTType) {
        final String STRING_COMPARE = "string_compare";
        if (stringDirection == StringDirection.ltr) {
            return p.mkHornPredicate(mkName(STRING_COMPARE),
                    new ProverType[]{stringADTType, stringADTType, p.getIntType()});
        } else {
            return p.mkHornPredicate(mkName(STRING_COMPARE),
                    new ProverType[]{stringADTType, stringADTType, p.getIntType(), p.getBooleanType()});
        }
    }

    private ProverFun mkStringSubstringProverFun(ProverType stringADTType) {
        final String STRING_SUBSTRING = "string_substring";
        return p.mkHornPredicate(mkName(STRING_SUBSTRING), new ProverType[]{stringADTType, p.getIntType(), p.getIntType(), stringADTType});
    }

    private ProverFun mkStringInnerIndexOfProverFun(ProverType stringADTType) {
        final String STRING_INNER_INDEX_OF = "string_inner_index_of";
        return p.mkHornPredicate(
                mkName(STRING_INNER_INDEX_OF),
                new ProverType[]{stringADTType, stringADTType, stringADTType, stringADTType, p.getIntType(), p.getIntType()}
        );
    }

    private ProverFun mkStringIndexOfProverFun(ProverType stringADTType) {
        final String STRING_INDEX_OF = "string_index_of";
        return p.mkHornPredicate(mkName(STRING_INDEX_OF), new ProverType[]{stringADTType, stringADTType, p.getIntType()});
    }

    private ProverFun mkStringIndexOfReverserProverFUn(ProverType stringADTType) {
        final String STRING_REVERSER_INDEX_OF = "string_reverser_index_of";
        return p.mkHornPredicate(mkName(STRING_REVERSER_INDEX_OF), new ProverType[]{stringADTType, stringADTType, stringADTType, stringADTType, stringADTType, stringADTType});
    }

    private void considerHintedSizeConcat(ProverFun predConcat, ProverType stringADTType) {
        ProverExpr b = stringHornVar("b", stringADTType), c = stringHornVar("c", stringADTType);
        ProverExpr exp = nil();
        ProverExpr concat = c;
        for (int leftSize = 0; leftSize <= MAX_SIZE_HINT; leftSize++) {
            ProverExpr headPE;
            if (stringDirection == StringDirection.ltr) {
                headPE = predConcat.mkExpr(exp, b, concat);
            } else {
                headPE = predConcat.mkExpr(b, exp, concat);
            }
            addPHC(
                    headPE,
                    EMPTY_PHC_BODY,
                    p.mkEq(b, c)
            );
            ProverExpr h = intHornVar("h" + leftSize);
            exp = cons(h, exp);
            concat = cons(h, concat);
        }
    }

    private void considerHintedSizeStartsWith(ProverFun predStartsWith, ProverType stringADTType) {
        if (stringDirection == StringDirection.ltr) {
            ProverExpr str = stringHornVar("str", stringADTType), z = intHornVar("z");
            ProverExpr sub = nil();
            for (int subSize = 0; subSize <= MAX_SIZE_HINT; subSize++) {
                addPHC(
                        predStartsWith.mkExpr(str, sub, lit(true))
                );
                addPHC(
                        predStartsWith.mkExpr(sub, cons(z, str), lit(false))
                );
                ProverExpr h = intHornVar("h" + subSize);
                str = cons(h, str);
                sub = cons(h, sub);
            }
        }
    }

    private ProverFun genSubstring(ProverType stringADTType) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr j = intHornVar("j");
        ProverExpr startIndex = intHornVar("startIndex");
        ProverExpr endIndex = intHornVar("endIndex");
        ProverExpr ja = cons(j, a);
        ProverExpr jb = cons(j, b);
        ProverFun predSubstring = mkStringSubstringProverFun(stringADTType);
        addPHC(
                predSubstring.mkExpr(a, startIndex, startIndex, nil()),
                EMPTY_PHC_BODY,
                p.mkAnd(p.mkGeq(startIndex, lit(0)), p.mkLeq(startIndex, len(a)))
                // https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#substring-int-int-
        );
        if (stringDirection == StringDirection.ltr) {
            addPHC(
                    predSubstring.mkExpr(ja, lit(0), p.mkPlus(endIndex, lit(1)), jb),
                    new ProverExpr[]{ predSubstring.mkExpr(a, lit(0), endIndex, b) }
            );
            addPHC(
                    predSubstring.mkExpr(ja, p.mkPlus(startIndex, lit(1)), p.mkPlus(endIndex, lit(1)), b),
                    new ProverExpr[]{ predSubstring.mkExpr(a, startIndex, endIndex, b) }
            );
        }
        else {
            addPHC(
                    predSubstring.mkExpr(ja, startIndex, p.mkPlus(len(a), lit(1)), jb),
                    new ProverExpr[]{ predSubstring.mkExpr(a, startIndex, len(a), b) }
            );
            addPHC(
                    predSubstring.mkExpr(ja, startIndex, endIndex, b),
                    new ProverExpr[]{ predSubstring.mkExpr(a, startIndex, endIndex, b) }
            );
        }
        return predSubstring;
    }

    private ProverFun genConcatRec(ProverType stringADTType) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr c = stringHornVar("c", stringADTType);
        ProverExpr h = intHornVar("h");
        ProverExpr hc = cons(h, c);
        // String Concatenation
        ProverFun predConcat = mkStringConcatProverFun(stringADTType);

        if (stringDirection == StringDirection.ltr) {
            // string_concat nil case
            addPHC(
                    predConcat.mkExpr(nil(), b, b)
            );
            // string_concat cons case
            ProverExpr ha = cons(h, a);
            addPHC(
                    predConcat.mkExpr(ha, b, hc),
                    new ProverExpr[]{predConcat.mkExpr(a, b, c)}
//            , p.mkGt(len(ha), lit(MAX_SIZE_HINT))
            );
        } else {
            // string_concat nil case
            addPHC(
                    predConcat.mkExpr(a, nil(), a)
            );
            // string_concat cons case
            ProverExpr hb = cons(h, b);
            addPHC(
                    predConcat.mkExpr(a, hb, hc),
                    new ProverExpr[]{predConcat.mkExpr(a, b, c)}
//            , p.mkGt(len(hb), lit(MAX_SIZE_HINT))
            );
        }
        return predConcat;
    }

    private ProverFun genConcatRecPrec(ProverType stringADTType, List<ProverFun> helpers) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr c = stringHornVar("c", stringADTType);
        ProverExpr h = intHornVar("h");
        ProverExpr hc = cons(h, c);
        // String Concatenation
        ProverFun predConcat = mkStringConcatProverFun(stringADTType);
        ProverFun prec = mkStringConcatPreconditionProverFun(stringADTType);

        if (stringDirection == StringDirection.ltr) {

            ProverExpr ha = cons(h, a);

            addPHC(
                    predConcat.mkExpr(nil(), a, a),
                    new ProverExpr[]{prec.mkExpr(nil(), a)}
            );
            addPHC(
                    prec.mkExpr(a, b),
                    new ProverExpr[]{prec.mkExpr(ha, b)}
            );
            addPHC(
                    predConcat.mkExpr(ha, b, hc),
                    new ProverExpr[]{prec.mkExpr(ha, b), predConcat.mkExpr(a, b, c)}
            );

        } else {

            ProverExpr hb = cons(h, b);

            addPHC(
                    predConcat.mkExpr(a, nil(), a),
                    new ProverExpr[]{prec.mkExpr(a, nil())}
            );
            addPHC(
                    prec.mkExpr(a, b),
                    new ProverExpr[]{prec.mkExpr(a, hb)}
            );
            addPHC(
                    predConcat.mkExpr(a, hb, hc),
                    new ProverExpr[]{prec.mkExpr(a, hb), predConcat.mkExpr(a, b, c)}
            );

        }

        helpers.add(prec);
        return predConcat;
    }

    private ProverExpr digitToChar(ProverExpr digit) {
        return p.mkPlus(digit, lit('0'));
    }

    private ProverFun genCharToString(ProverType stringADTType) {
        ProverExpr c = charHornVar("c");

        ProverFun predCharToString = mkCharToStringProverFun(stringADTType);

        addPHC(
                predCharToString.mkExpr(c, cons(c, nil())),
                EMPTY_PHC_BODY
        );

        return predCharToString;
    }

    private ProverFun genIntToString(ProverType stringADTType) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr s = stringHornVar("s", stringADTType);
        ProverExpr i = intHornVar("i");
        ProverExpr n = intHornVar("n");
        ProverExpr d = intHornVar("d");

        ProverFun predIntToStringHelper = mkIntToStringHelperProverFun(stringADTType);
        ProverFun predIntToString = mkIntToStringProverFun(stringADTType);

        addPHC(
                predIntToString.mkExpr(i, cons(digitToChar(i), nil())),
                EMPTY_PHC_BODY,
                p.mkAnd(p.mkGeq(i, lit(0)), p.mkLt(i, lit(10)))
        );

        if (stringDirection == StringDirection.ltr) {

            addPHC(
                    predIntToStringHelper.mkExpr(i, i, nil()),
                    EMPTY_PHC_BODY,
                    p.mkGeq(i, lit(10))
            );
            addPHC(
                    predIntToString.mkExpr(i, s),
                    new ProverExpr[]{predIntToStringHelper.mkExpr(i, lit(0), s)},
                    p.mkOr(p.mkLt(i, lit(0)), p.mkGeq(i, lit(10)))
            );
            addPHC(
                    predIntToString.mkExpr(i, cons(lit('-'), a)),
                    new ProverExpr[]{predIntToString.mkExpr(p.mkNeg(i), a)},
                    p.mkLt(i, lit(0))
            );
            ProverExpr lastDigitOfN = p.mkMinus(n, p.mkMult(lit(10), p.mkTDiv(n, lit(10))));
            addPHC(
                    predIntToStringHelper.mkExpr(i, p.mkTDiv(n, lit(10)), cons(digitToChar(lastDigitOfN), s)),
                    new ProverExpr[]{predIntToStringHelper.mkExpr(i, n, s)},
                    p.mkGt(n, lit(0))
            );

        } else {

            long bndLo;
            long bndHi = 1L;
            do {
                bndLo = bndHi;
                bndHi *= 10L;
                addPHC(
                        predIntToStringHelper.mkExpr(i, nil(), lit(bndLo)),
                        EMPTY_PHC_BODY,
                        bndLo <= Long.MAX_VALUE / 10 ?
                                p.mkAnd(p.mkGeq(i, lit(bndLo)), p.mkLt(i, lit(bndHi))) :
                                p.mkAnd(p.mkGeq(i, lit(bndLo)), p.mkLeq(i, lit(Long.MAX_VALUE)))
                );
                addPHC(
                        predIntToStringHelper.mkExpr(i, cons(lit('-'), nil()), lit(-bndLo)),
                        EMPTY_PHC_BODY,
                        bndLo <= Long.MAX_VALUE / 10 ?
                                p.mkAnd(p.mkLeq(i, lit(-bndLo)), p.mkGt(i, lit(-bndHi))) :
                                p.mkAnd(p.mkLeq(i, lit(-bndLo)), p.mkGeq(i, lit(Long.MIN_VALUE)))
                );
            } while (bndLo <= Long.MAX_VALUE / 10);

            addPHC(
                    predIntToString.mkExpr(i, s),
                    new ProverExpr[]{predIntToStringHelper.mkExpr(i, s, lit(0))},
                    p.mkOr(p.mkLt(i, lit(0)), p.mkGeq(i, lit(10)))
            );
            ProverExpr digitToLeft = p.mkTDiv(i, d);    // {XYZd}...
            ProverExpr digit = p.mkMinus(digitToLeft, p.mkMult(lit(10), p.mkTDiv(digitToLeft, lit(10))));
            addPHC(
                    predIntToStringHelper.mkExpr(i, cons(digitToChar(digit), s), p.mkTDiv(d, lit(10))),
                    new ProverExpr[]{predIntToStringHelper.mkExpr(i, s, d)},
                    p.mkNot(p.mkEq(d, lit(0)))
            );

        }

        return predIntToString;
    }

    private ProverFun genBoolToString(ProverType boolType, ProverType stringADTType) {
        ProverExpr b = (boolType instanceof BoolType) ? booleanHornVar("b") : intHornVar("b");
        ProverExpr TRUE = (boolType instanceof BoolType) ? lit(true) : lit(1);

        ProverFun predBoolToString = mkBoolToStringProverFun(boolType, stringADTType);

        addPHC(
                predBoolToString.mkExpr(b, mkStringPE("true")),
                EMPTY_PHC_BODY,
                p.mkEq(b, TRUE)
        );
        addPHC(
                predBoolToString.mkExpr(b, mkStringPE("false")),
                EMPTY_PHC_BODY,
                p.mkNot(p.mkEq(b, TRUE))
        );

        return predBoolToString;
    }

    private ProverFun genEdgesWithRec(ProverType stringADTType, boolean startEdge) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr h = intHornVar("h");
        ProverExpr j = intHornVar("j");
        ProverExpr k = intHornVar("k");
        ProverExpr ha = cons(h, a);
        ProverExpr hb = cons(h, b);
        ProverExpr ja = cons(j, a);
        ProverExpr kb = cons(k, b);

        ProverFun predEdgesWith = mkStringEdgesWithProverFun(stringADTType, startEdge);

        addPHC(
                predEdgesWith.mkExpr(a, nil(), lit(true))
        );
        addPHC(
                predEdgesWith.mkExpr(a, a, lit(true))
        );
        addPHC(
                predEdgesWith.mkExpr(nil(), b, lit(false)),
                EMPTY_PHC_BODY,
                p.mkGt(len(b), lit(0))
        );
        addPHC(
                predEdgesWith.mkExpr(ja, kb, lit(false)),
                new ProverExpr[]{predEdgesWith.mkExpr(a, b, lit(false))}
        );

        if ((startEdge && stringDirection == StringDirection.ltr)
                || (!startEdge && stringDirection == StringDirection.rtl)) {
            addPHC(
                    predEdgesWith.mkExpr(ha, hb, lit(true)),
                    new ProverExpr[]{predEdgesWith.mkExpr(a, b, lit(true))}
            );
            addPHC(
                    predEdgesWith.mkExpr(ja, kb, lit(false)),
                    EMPTY_PHC_BODY,
                    p.mkNot(p.mkEq(j, k))
                    //            lit(true)
            );
        } else {
            addPHC(
                    predEdgesWith.mkExpr(ha, b, lit(true)),
                    new ProverExpr[]{predEdgesWith.mkExpr(a, b, lit(true))}
            );
            addPHC(
                    predEdgesWith.mkExpr(cons(j, a), cons(k, a), lit(false)),
                    EMPTY_PHC_BODY,
                    p.mkNot(p.mkEq(j, k))
                    //            lit(true)
            );
            addPHC(
                    predEdgesWith.mkExpr(ha, b, lit(false)),
                    new ProverExpr[]{predEdgesWith.mkExpr(a, b, lit(false))},
                    p.mkGeq(len(a), len(b))
            );
            addPHC(
                    predEdgesWith.mkExpr(a, hb, lit(false)),
                    new ProverExpr[]{predEdgesWith.mkExpr(a, b, lit(false))}
            );
        }

        return predEdgesWith;
    }

    private ProverFun genEdgesWithOffsetRec(ProverType stringADTType, boolean startEdge) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr h = intHornVar("h");
        ProverExpr j = intHornVar("j");
        ProverExpr k = intHornVar("k");
        ProverExpr f = intHornVar("f");
        ProverExpr ha = cons(h, a);
        ProverExpr hb = cons(h, b);
        ProverExpr ja = cons(j, a);
        ProverExpr kb = cons(k, b);

        ProverFun predEdgesWithOffset = mkStringEdgesWithOffsetProverFun(stringADTType, startEdge);

        addPHC(
                predEdgesWithOffset.mkExpr(a, nil(), f,
                        p.mkOr(
                                p.mkAnd(p.mkGeq(f, lit(0)), p.mkLt(f, len(a))),
                                p.mkAnd(p.mkEq(a, nil()), p.mkEq(f, lit(0)))
                        )
                )
        );
        addPHC(
                predEdgesWithOffset.mkExpr(a, a, f, p.mkEq(f, lit(0)))
        );
        addPHC(
                predEdgesWithOffset.mkExpr(a, b, f, lit(false)),
                EMPTY_PHC_BODY,
                p.mkAnd(
                        p.mkGt(len(b), lit(0)),
                        p.mkOr(p.mkLt(f, lit(0)), p.mkGeq(f, len(a)))
                )

        );
        addPHC(
                predEdgesWithOffset.mkExpr(ja, kb, f, lit(false)),
                new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(false))},
                p.mkAnd(p.mkGeq(f, lit(0)), p.mkLt(f, len(a)))
        );

        if ((startEdge && stringDirection == StringDirection.ltr)
                || (!startEdge && stringDirection == StringDirection.rtl)) {
            addPHC(
                    predEdgesWithOffset.mkExpr(ha, hb, p.mkPlus(f, lit(1)), lit(true)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(true))},
                    p.mkAnd(p.mkGeq(f, lit(0)), p.mkLt(f, len(a)))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ja, kb, lit(0), lit(false)),
                    EMPTY_PHC_BODY,
                    p.mkNot(p.mkEq(j, k))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ha, b, p.mkPlus(f, lit(1)), lit(false)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(false))},
                    p.mkAnd(p.mkGeq(f, lit(0)), p.mkLt(f, len(a)))
            );
        } else {
            addPHC(
                    predEdgesWithOffset.mkExpr(ha, cons(h, nil()), len(a), lit(true))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ha, hb, f, lit(true)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(true))},
                    p.mkAnd(p.mkGt(len(b), lit(0)), p.mkEq(len(a), p.mkPlus(len(b), f)))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ha, b, f, lit(true)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(true))}
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ja, kb, f, lit(false)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(true))},
                    p.mkAnd(p.mkNot(p.mkEq(j, k)), p.mkEq(len(a), p.mkPlus(len(b), f)))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ja, cons(k, nil()), len(a), lit(false)),
                    EMPTY_PHC_BODY,
                    p.mkNot(p.mkEq(j, k))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ja, kb, f, lit(false)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(false))},
                    p.mkAnd(p.mkGt(len(b), lit(0)), p.mkEq(len(a), p.mkPlus(len(b), f)))
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(ha, b, f, lit(false)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(false))},
                    p.mkAnd(
                            p.mkGeq(len(a), p.mkPlus(len(b), f)),
                            p.mkGeq(f, lit(0)), p.mkLt(f, len(a))
                    )
            );
            addPHC(
                    predEdgesWithOffset.mkExpr(a, hb, f, lit(false)),
                    new ProverExpr[]{predEdgesWithOffset.mkExpr(a, b, f, lit(false))},
                    p.mkAnd(
                            p.mkLeq(len(a), p.mkPlus(len(b), f)),
                            p.mkGeq(f, lit(0)), p.mkLt(f, len(a))
                    )
            );
        }

        return predEdgesWithOffset;
    }

    private ProverFun genCharAtRec(ProverType stringADTType) {
        ProverExpr t = stringHornVar("t", stringADTType);
        ProverExpr h = intHornVar("h");
        ProverExpr i = intHornVar("i");
        ProverExpr c = intHornVar("c");
        ProverFun predCharAt = mkStringCharAtProverFun(stringADTType);
        if (stringDirection == StringDirection.ltr) {
            addPHC(
                    predCharAt.mkExpr(cons(h, t), lit(0), h)
            );
            // induction
            addPHC(
                    predCharAt.mkExpr(cons(h, t), p.mkPlus(i, lit(1)), c),
                    new ProverExpr[]{predCharAt.mkExpr(t, i, c)},
                    p.mkAnd(p.mkGeq(i, lit(0)), p.mkLt(i, len(t)))
            );
        } else {
            addPHC(
                    predCharAt.mkExpr(cons(h, t), len(t), h)
            );
            // induction
            addPHC(
                    predCharAt.mkExpr(cons(h, t), p.mkMinus(len(t), i), c),             // needs len(t) to make progress
                    new ProverExpr[]{predCharAt.mkExpr(t, p.mkMinus(len(t), i), c)},
                    p.mkAnd(p.mkGeq(i, lit(0)), p.mkLt(i, len(t)))
            );
        }

        return predCharAt;
    }

    private ProverFun genConcatIter(ProverType stringADTType) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr c = stringHornVar("c", stringADTType);
        ProverExpr r = stringHornVar("r", stringADTType);
        ProverExpr t = stringHornVar("t", stringADTType);
        ProverExpr h = intHornVar("h");
        // String Concatenation
        ProverFun predConcatIter = mkStringConcatIterativeProverFun(stringADTType);
        ProverFun predConcat = mkStringConcatProverFun(stringADTType);
        // TODO: fix occasional StackOverflow
        if (stringDirection == StringDirection.ltr) {
            // string_concat_iterative initial condition
            addPHC(
                    predConcatIter.mkExpr(a, b, a, nil(), b)    // base case
            );
        } else {
            // string_concat_iterative initial condition
            addPHC(
                    predConcatIter.mkExpr(a, b, b, nil(), a)    // base case
            );
        }
        // string_concat_iterative reversing a
        addPHC(
                predConcatIter.mkExpr(a, b, t, cons(h, r), c),
                new ProverExpr[]{predConcatIter.mkExpr(a, b, cons(h, t), r, c)}
        );
        // string_concat_iterative reversing reverse of a at head of b, results concatenation
        addPHC(
                predConcatIter.mkExpr(a, b, nil(), t, cons(h, c)),
                new ProverExpr[]{predConcatIter.mkExpr(a, b, nil(), cons(h, t), c)}
        );
        addPHC(
                predConcat.mkExpr(a, b, c),
                new ProverExpr[]{predConcatIter.mkExpr(a, b, nil(), nil(), c)}     // (?) problem matching a = nil on base case
//            , p.mkGt(len(a), lit(MAX_SIZE_HINT))
        );

        return predConcat;
    }

    private ProverFun genCompareToRec(ProverType stringADTType) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr h = intHornVar("h");
        ProverExpr j = intHornVar("j");
        ProverExpr k = intHornVar("k");
        ProverExpr c = intHornVar("c");

        ProverFun predCompareTo = mkStringCompareToProverFun(stringADTType);


        if (stringDirection == StringDirection.ltr) {
            addPHC(
                    predCompareTo.mkExpr(a, nil(), len(a))
            );
            addPHC(
                    predCompareTo.mkExpr(nil(), b, p.mkNeg(len(b)))
            );
            addPHC(
                    predCompareTo.mkExpr(a, a, lit(0))
            );
            addPHC(
                    predCompareTo.mkExpr(cons(j, a), cons(k, b), p.mkMinus(j, k)),
                    EMPTY_PHC_BODY,
                    p.mkNot(p.mkEq(j, k))
            );
            addPHC(
                    predCompareTo.mkExpr(cons(h, a), cons(h, b), c),
                    new ProverExpr[]{predCompareTo.mkExpr(a, b, c)}
            );
        } else {
            addPHC(
                    predCompareTo.mkExpr(a, nil(), len(a), lit(false))
            );
            addPHC(
                    predCompareTo.mkExpr(nil(), b, p.mkNeg(len(b)), lit(false))
            );
            addPHC(
                    predCompareTo.mkExpr(a, a, lit(0), lit(false))
            );
            addPHC(
                    predCompareTo.mkExpr(cons(h, a), b, p.mkPlus(c, lit(1)), lit(false)),
                    new ProverExpr[]{predCompareTo.mkExpr(a, b, c, lit(false))},
                    p.mkGeq(len(a), len(b))
            );
            addPHC(
                    predCompareTo.mkExpr(a, cons(h, b), p.mkMinus(c, lit(1)), lit(false)),
                    new ProverExpr[]{predCompareTo.mkExpr(a, b, c, lit(false))},
                    p.mkLeq(len(a), len(b))
            );
            addPHC(
                    predCompareTo.mkExpr(cons(j, a), cons(k, a), p.mkMinus(j, k), lit(true)),
                    EMPTY_PHC_BODY,
                    p.mkNot(p.mkEq(j, k))
            );
            addPHC(
                    predCompareTo.mkExpr(cons(h, a), b, c, lit(true)),
                    new ProverExpr[]{predCompareTo.mkExpr(a, b, c, lit(true))}
            );
            addPHC(
                    predCompareTo.mkExpr(a, cons(h, b), c, lit(true)),
                    new ProverExpr[]{predCompareTo.mkExpr(a, b, c, lit(true))}
            );
        }

        return predCompareTo;
    }

    private ProverFun genIndexOf(ProverType stringADTType, ProverExpr leftString, ProverExpr rightString, ProverExpr startOffset, boolean isIndexOf) {
        ProverExpr a = stringHornVar("a", stringADTType);
        ProverExpr b = stringHornVar("b", stringADTType);
        ProverExpr aCopy = stringHornVar("a_copy", stringADTType);
        ProverExpr bCopy = stringHornVar("b_copy", stringADTType);
        ProverExpr aRev = stringHornVar("a_rev", stringADTType);
        ProverExpr bRev = stringHornVar("b_rev", stringADTType);
        ProverExpr aRevCopy = stringHornVar("a_rev_copy", stringADTType);
        ProverExpr bRevCopy = stringHornVar("b_rev_copy", stringADTType);
        ProverExpr j = intHornVar("j");
        ProverExpr k = intHornVar("k");
        ProverExpr si = intHornVar("si");
        ProverExpr i = intHornVar("i");

        final ProverExpr ONE = lit(1);
        final ProverExpr ZERO = lit(0);
        final ProverExpr MINUS_ONE = lit(-1);
        final ProverExpr rightStringLength = len(rightString);
        final ProverExpr leftStringLength = len(leftString);
        ProverExpr siIsInLimit = p.mkAnd(p.mkGeq(p.mkMinus(leftStringLength, rightStringLength), si), p.mkGeq(si, ZERO));
        ProverExpr siExceededLimit = (isIndexOf) ? p.mkGt(si, p.mkMinus(leftStringLength, rightStringLength)) : p.mkGt(ZERO, si);
        ProverExpr nextSi = (isIndexOf) ? p.mkPlus(si, ONE) : p.mkMinus(si, ONE);

        ProverFun finalPredIndexOf = mkStringIndexOfProverFun(stringADTType);
        ProverFun predIndexOf = mkStringInnerIndexOfProverFun(stringADTType);
        ProverFun reverserPredIndexOf = mkStringIndexOfReverserProverFUn(stringADTType);
        addPHC(
                reverserPredIndexOf.mkExpr(a, b, a, b, nil(), nil()),
                EMPTY_PHC_BODY,
                p.mkAnd(p.mkEq(len(a), leftStringLength), p.mkEq(len(b), rightStringLength), p.mkNot(p.mkEq(rightStringLength, ZERO)))
        );
        if (isIndexOf) {
            addPHC(
                    finalPredIndexOf.mkExpr(a, b, ZERO),
                    EMPTY_PHC_BODY,
                    p.mkAnd(p.mkEq(rightStringLength, ZERO), p.mkLt(startOffset, ZERO))
            );
            addPHC(
                    finalPredIndexOf.mkExpr(a, b, startOffset),
                    EMPTY_PHC_BODY,
                    p.mkAnd(p.mkEq(rightStringLength, ZERO), p.mkGeq(startOffset, ZERO), p.mkLt(startOffset, leftStringLength))
            );
            addPHC(
                    finalPredIndexOf.mkExpr(a, b, leftStringLength),
                    EMPTY_PHC_BODY,
                    p.mkAnd(p.mkEq(rightStringLength, ZERO), p.mkGeq(startOffset, leftStringLength))
            );
            addPHC(
                    predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, ZERO),
                    new ProverExpr[]{predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, startOffset)},
                    p.mkLt(startOffset, ZERO)
            );
        } else {
            addPHC(
                    finalPredIndexOf.mkExpr(a, b, leftStringLength),
                    EMPTY_PHC_BODY,
                    p.mkAnd(p.mkEq(rightStringLength, ZERO), p.mkGeq(startOffset, leftStringLength))
            );
            addPHC(
                    finalPredIndexOf.mkExpr(a, b, startOffset),
                    EMPTY_PHC_BODY,
                    p.mkAnd(p.mkEq(rightStringLength, ZERO), p.mkLt(startOffset, leftStringLength), p.mkGeq(startOffset, ZERO))
            );
            addPHC(
                    finalPredIndexOf.mkExpr(a, b, MINUS_ONE),
                    EMPTY_PHC_BODY,
                    p.mkAnd(p.mkEq(rightStringLength, ZERO), p.mkLt(startOffset, ZERO))
            );
            addPHC(
                    predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, p.mkMinus(leftStringLength, rightStringLength)),
                    new ProverExpr[]{predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, startOffset)},
                    p.mkGt(startOffset, p.mkMinus(leftStringLength, rightStringLength))
            );
        }
        if (stringDirection == StringDirection.ltr) {
            //reversing strings
            addPHC(
                    reverserPredIndexOf.mkExpr(a, b, aCopy, bCopy, cons(j, aRev), nil()),
                    new ProverExpr[]{reverserPredIndexOf.mkExpr(a, b, cons(j, aCopy), bCopy, aRev, nil())}
            );
            addPHC(
                    reverserPredIndexOf.mkExpr(a, b, nil(), bCopy, aRev, cons(j, bRev)),
                    new ProverExpr[]{reverserPredIndexOf.mkExpr(a, b, nil(), cons(j, bCopy), aRev, bRev)}
            );
            addPHC(
                    predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, startOffset),
                    new ProverExpr[]{reverserPredIndexOf.mkExpr(a, b, nil(), nil(), aRev, bRev)}
            );
            addPHC(
                    predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, nextSi),
                    new ProverExpr[]{
                            predIndexOf.mkExpr(a, b, cons(k, aRevCopy), cons(j, bRevCopy), i, si),
                            reverserPredIndexOf.mkExpr(a, b, nil(), nil(), aRev, bRev)
                    },
                    p.mkAnd(p.mkLt(si, p.mkMinus(leftStringLength, i)),
                            p.mkLeq(p.mkMinus(leftStringLength, i), p.mkPlus(si, rightStringLength)),
                            p.mkNot(p.mkEq(k, j)),
                            siIsInLimit
                    )
            );
        } else {
            addPHC(
                    predIndexOf.mkExpr(a, b, a, b, ZERO, startOffset),
                    new ProverExpr[]{reverserPredIndexOf.mkExpr(a, b, a, b, nil(), nil())}
            );
            addPHC(
                    predIndexOf.mkExpr(a, b, a, b, ZERO, nextSi),
                    new ProverExpr[]{
                            predIndexOf.mkExpr(a, b, cons(k, aRevCopy), cons(j, bRevCopy), i, si),
                            reverserPredIndexOf.mkExpr(a, b, a, b, nil(), nil())
                    },
                    p.mkAnd(p.mkLt(si, p.mkMinus(leftStringLength, i)),
                            p.mkLeq(p.mkMinus(leftStringLength, i), p.mkPlus(si, rightStringLength)),
                            p.mkNot(p.mkEq(k, j)),
                            siIsInLimit
                    )
            );
        }
        addPHC(
                predIndexOf.mkExpr(a, b, aRevCopy, bRevCopy, p.mkPlus(i, ONE), si),
                new ProverExpr[]{predIndexOf.mkExpr(a, b, cons(j, aRevCopy), bRevCopy, i, si)},
                p.mkAnd(p.mkGt(p.mkMinus(leftStringLength, i), p.mkPlus(si, rightStringLength)), siIsInLimit)
        );
        addPHC(
                predIndexOf.mkExpr(a, b, aRevCopy, bRevCopy, p.mkPlus(i, ONE), si),
                new ProverExpr[]{predIndexOf.mkExpr(a, b, cons(j, aRevCopy), cons(j, bRevCopy), i, si)},
                p.mkAnd(p.mkLt(si, p.mkMinus(leftStringLength, i)), p.mkLeq(p.mkMinus(leftStringLength, i), p.mkPlus(si, rightStringLength)), siIsInLimit)
        );
        addPHC(
                finalPredIndexOf.mkExpr(a, b, MINUS_ONE),
                new ProverExpr[]{predIndexOf.mkExpr(a, b, aRev, bRev, ZERO, si)},
                siExceededLimit
        );
        addPHC(
                finalPredIndexOf.mkExpr(a, b, si),
                new ProverExpr[]{predIndexOf.mkExpr(a, b, aRevCopy, nil(), p.mkMinus(leftStringLength, si), si)}
        );
        return finalPredIndexOf;
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

    private ProverExpr mkRefHornVariable(String name, ReferenceType refType) {
        ProverType proverType = HornHelper.hh().getProverType(p, refType);
//        if (name == null) {
//            int id = HornHelper.hh().newVarNum();
//            name = String.format(STRING_REF_TEMPLATE, id);
//        }
        return p.mkHornVariable(name, proverType);
    }

    public static ProverExpr proverExprFromIdExpr(IdentifierExpression ie, Map<Variable, ProverExpr> varMap) {
        return varMap.get(ie.getVariable());
    }

    private ProverExpr selectString(ProverExpr pe) {
        if (pe instanceof ProverTupleExpr) {
            return p.mkTupleSelect(pe, 3);
        } else {
            return pe;
        }
    }

    private ProverExpr selectString(Expression expr, Map<Variable, ProverExpr> varMap) {
        if (expr instanceof StringLiteral) {
            return mkStringPE(((StringLiteral) expr).getValue());
        } else if (expr instanceof IdentifierExpression) {
            ProverExpr pe = proverExprFromIdExpr((IdentifierExpression) expr, varMap);
            Verify.verify(pe != null, "cannot extract string from " + expr);
            return selectString(pe);
        } else {
            Verify.verify(false, "cannot extract string from " + expr);
            throw new RuntimeException();
        }
    }

    private ProverExpr selectInt(Expression expr, Map<Variable, ProverExpr> varMap) {
        if (expr instanceof IntegerLiteral) {
            long num = ((IntegerLiteral) expr).getValue();
            return lit(num);
        } else {
            ProverExpr pe = proverExprFromIdExpr((IdentifierExpression) expr, varMap);
            Verify.verify(pe != null, "cannot extract int from " + expr);
            return pe;
        }
    }

    private ProverExpr selectBool(Expression expr, Map<Variable, ProverExpr> varMap) {
        if (expr instanceof BooleanLiteral) {   // TODO: does not happen; should completely ignore this case?
            boolean b = ((BooleanLiteral) expr).getValue();
            return lit(b);
        } else if (expr instanceof IntegerLiteral) {
            long num = ((IntegerLiteral) expr).getValue();
            return lit(num);
        } else {
            ProverExpr pe = proverExprFromIdExpr((IdentifierExpression) expr, varMap);
            Verify.verify(pe != null, "cannot extract boolean from " + expr);
            return pe;
        }
    }

    private ProverExpr mkNotNullConstraint(ProverExpr refPE) {
        return p.mkNot(p.mkEq(p.mkTupleSelect(refPE, 0), lit(0)));
    }

    public EncodingFacts mkStringSubstring(ProverExpr leftString, ProverExpr startIndex, ProverExpr endIndex, ReferenceType stringRefType) {
        String substringName = String.format("$substring(%s, %s, %s)", leftString.toString(), startIndex.toString(), endIndex.toString());
        ProverExpr substring = mkRefHornVariable(substringName, stringRefType);
        ProverExpr substringString = selectString(substring);
        ProverFun predSubstring = genSubstring(getStringADTType());
        ProverExpr guarantee = predSubstring.mkExpr(leftString, startIndex, endIndex, substringString);
        return new EncodingFacts(null, guarantee, substring, mkNotNullConstraint(substring));
    }

    public EncodingFacts mkStringSubstring(ProverExpr leftString, ProverExpr index, ReferenceType stringRefType) {
        String substringName = String.format("$substring(%s, %s)", leftString.toString(), index.toString());
        ProverExpr substring = mkRefHornVariable(substringName, stringRefType);
        ProverExpr substringString = selectString(substring);
        ProverFun predSubstring = genSubstring(getStringADTType());
        ProverExpr guarantee = predSubstring.mkExpr(leftString, index, len(leftString), substringString);
        return new EncodingFacts(null, guarantee, substring, mkNotNullConstraint(substring));
    }

    public EncodingFacts mkStringIndexOf(ProverExpr leftString, ProverExpr rightString, ProverExpr startOffset) {
        String resultName = String.format("$indexOf(%s, %s)", leftString.toString(), rightString.toString());
        ProverExpr result = intHornVar(resultName);
        ProverFun predIndexOf = genIndexOf(getStringADTType(), leftString, rightString, startOffset, true);
        ProverExpr guarantee = predIndexOf.mkExpr(leftString, rightString, result);
        return new EncodingFacts(null, guarantee, result, lit(true));
    }

    public EncodingFacts mkStringLastIndexOf(ProverExpr leftString, ProverExpr rightString, ProverExpr startOffset) {
        String resultName = String.format("$lastIndexOf(%s, %s)", leftString.toString(), rightString.toString());
        ProverExpr result = intHornVar(resultName);
        ProverFun predIndexOf = genIndexOf(getStringADTType(), leftString, rightString, startOffset, false);
        ProverExpr guarantee = predIndexOf.mkExpr(leftString, rightString, result);
        return new EncodingFacts(null, guarantee, result, lit(true));
    }

    public EncodingFacts mkStringConcat(ProverExpr leftString, ProverExpr rightString, ReferenceType stringRefType) {
        ProverType stringADTType = getStringADTType();
        String concatName = String.format("$concat(%s, %s)", leftString.toString(), rightString.toString());
        ProverExpr concat = mkRefHornVariable(concatName, stringRefType);
        ProverExpr concatString = selectString(concat);
        ProverFun predConcat;
        ProverFun prec;
        switch (stringEncoding) {
            case recursive:
                predConcat = genConcatRec(stringADTType);
                prec = null;
                break;
            case recursiveWithPrec:
                LinkedList<ProverFun> helpers = new LinkedList<ProverFun>();
                predConcat = genConcatRecPrec(stringADTType, helpers);
                prec = helpers.get(0);
                break;
            case iterative:
                predConcat = genConcatIter(stringADTType);
                prec = null;
                break;
            default:
                throw new RuntimeException("unhandled string encoding");
        }
//        considerHintedSizeConcat(predConcat, stringADTType);
        ProverExpr guarantee = predConcat.mkExpr(leftString, rightString, concatString);
        ProverExpr rely;
        if (prec != null) {
            rely = prec.mkExpr(leftString, rightString);
        } else {
            rely = null;
        }
        return new EncodingFacts(rely, guarantee, concat, mkNotNullConstraint(concat));
    }

    public EncodingFacts mkStringCompareTo(ProverExpr leftString, ProverExpr rightString, ReferenceType stringRefType) {
        ProverType stringADTType = getStringADTType();
        String resultName = String.format("$compare(%s, %s)", leftString.toString(), rightString.toString());
        ProverExpr result = intHornVar(resultName);
        ProverFun predCompareTo;
        switch (stringEncoding) {
            case recursive:
            default:
                predCompareTo = genCompareToRec(stringADTType);
                break;
            // TODO: other encodings
//            case recursiveWithPrec:
//                throw new RuntimeException("not implemented");
//            case iterative:
//                throw new RuntimeException("not implemented");
//            default:
//                throw new RuntimeException("unhandled string encoding");
        }
        ProverExpr guarantee;
        if (stringDirection == StringDirection.ltr) {
            guarantee = predCompareTo.mkExpr(leftString, rightString, result);
        } else {
            guarantee = predCompareTo.mkExpr(leftString, rightString, result, booleanHornVar(mkName("$tmp")));
        }
        return new EncodingFacts(null, guarantee, result, lit(true));
    }

    public EncodingFacts mkIntToString(ProverExpr intPE, ReferenceType stringRefType) {
        ProverType stringADTType = getStringADTType();
        String resultName = mkName(String.format("str_i(%s)", intPE.toString()));
        ProverExpr result = mkRefHornVariable(resultName, stringRefType);
        ProverExpr resultString = selectString(result);
        ProverFun predIntToString = genIntToString(stringADTType);
//        considerHintedSizeIntString(predIntToString);
        ProverExpr guarantee = predIntToString.mkExpr(intPE, resultString);
        return new EncodingFacts(null, guarantee, result, mkNotNullConstraint(result));
    }

    public EncodingFacts mkCharToString(ProverExpr charPE, ReferenceType stringRefType) {
        ProverType stringADTType = getStringADTType();
        String resultName = mkName(String.format("str_c(%s)", charPE.toString()));
        ProverExpr result = mkRefHornVariable(resultName, stringRefType);
        ProverExpr resultString = selectString(result);
        ProverFun predCharToString = genCharToString(stringADTType);
//        considerHintedSizeIntString(predCharToString);
        ProverExpr guarantee = predCharToString.mkExpr(charPE, resultString);
        return new EncodingFacts(null, guarantee, result, mkNotNullConstraint(result));
    }

    public EncodingFacts mkBoolToString(ProverExpr boolPE, ReferenceType stringRefType) {
        ProverType stringADTType = getStringADTType();
        String resultName = mkName(String.format("str_b(%s)", boolPE.toString()));
        ProverExpr result = mkRefHornVariable(resultName, stringRefType);
        ProverExpr resultString = selectString(result);
        ProverFun predBoolToString = genBoolToString(boolPE.getType(), stringADTType);
        ProverExpr guarantee = predBoolToString.mkExpr(boolPE, resultString);
        return new EncodingFacts(null, guarantee, result, mkNotNullConstraint(result));
    }

    public EncodingFacts mkStringEdgesWith(ProverExpr leftString, ProverExpr rightString, boolean startEdge) {
        ProverType stringADTType = getStringADTType();
        String resultName = String.format(
                startEdge ? "$starts_with(%s, %s)" : "$ends_with(%s, %s)",
                leftString.toString(), rightString.toString()
        );
        ProverExpr result = booleanHornVar(resultName);
        ProverFun predStartsWith;
        predStartsWith = genEdgesWithRec(stringADTType, startEdge);    // TODO: Iterative
        ProverExpr guarantee = predStartsWith.mkExpr(leftString, rightString, result);
        return new EncodingFacts(null, guarantee, result, lit(true));
    }

    public EncodingFacts mkStringEdgesWithOffset(ProverExpr leftString, ProverExpr rightString,
                                                 ProverExpr offset, boolean startEdge) {
        ProverType stringADTType = getStringADTType();
        String resultName = mkName(String.format(
                startEdge ? "$starts_with_offset(%s, %s, %s)" : "$ends_with_offset(%s, %s, %s)",
                leftString.toString(), rightString.toString(), offset.toString()
        ));
        ProverExpr result = booleanHornVar(resultName);
        ProverFun predStartsWithOffset;
        predStartsWithOffset = genEdgesWithOffsetRec(stringADTType, startEdge);    // TODO: Iterative
        ProverExpr guarantee = predStartsWithOffset.mkExpr(leftString, rightString, offset, result);
        return new EncodingFacts(null, guarantee, result, lit(true));
    }

    public EncodingFacts mkStringCharAt(ProverExpr strPE, ProverExpr indexPE) {
        ProverType stringADTType = getStringADTType();
        String chName = String.format("$char_at(%s, %s)", strPE.toString(), indexPE.toString());
        ProverExpr ch = intHornVar(chName);
        ProverFun predCharAt;
        predCharAt = genCharAtRec(stringADTType);           // TODO: Iterative
        ProverExpr guarantee = predCharAt.mkExpr(strPE, indexPE, ch);
        return new EncodingFacts(null, guarantee, ch, lit(true));
    }

    public EncodingFacts mkStringLengthFromExpression(Expression strExpr, Map<Variable, ProverExpr> varMap) {
        if (strExpr instanceof StringLiteral) {
            String str = ((StringLiteral) strExpr).getValue();
            Verify.verify(str != null, "unsupported expression");
            return new EncodingFacts(null, null, lit(str.length()), lit(true));
        } else {
            final ProverExpr pe = proverExprFromIdExpr((IdentifierExpression) strExpr, varMap);
            if (pe == null)
                return null;
            final ProverExpr strPE = selectString(pe);
            return new EncodingFacts(null, null, len(strPE), mkNotNullConstraint(pe));
        }
    }

    public EncodingFacts mkToStringFromExpression(Expression stringableExpr, Expression lhsRefExpr,
                                                  Map<Variable, ProverExpr> varMap) {
        ReferenceType lhsRefExprType = (ReferenceType) lhsRefExpr.getType();
        if (stringableExpr.getType() == soottocfg.cfg.type.IntType.instance()) {
            ProverExpr pe = selectInt(stringableExpr, varMap);
            return mkIntToString(pe, lhsRefExprType);
        } else {
            final ProverExpr internalString = selectString(stringableExpr, varMap);
            if (internalString == null)
                return null;
            ProverExpr result = mkRefHornVariable(internalString.toString(), lhsRefExprType);
            ProverExpr resultString = selectString(result);
            return new EncodingFacts(null, null, result,
                    p.mkAnd(mkNotNullConstraint(result), p.mkEq(resultString, internalString))
            );
        }
    }

    public EncodingFacts mkBoolToStringFromExpression(Expression stringableExpr, Expression lhsRefExpr,
                                                      Map<Variable, ProverExpr> varMap) {
        ReferenceType lhsRefExprType = (ReferenceType) lhsRefExpr.getType();
        if (stringableExpr.getType() == soottocfg.cfg.type.BoolType.instance() ||
                stringableExpr.getType() == soottocfg.cfg.type.IntType.instance()) {
            ProverExpr pe = selectBool(stringableExpr, varMap);
            return mkBoolToString(pe, lhsRefExprType);
        } else {
            return null;
        }
    }

    public EncodingFacts mkCharToStringFromExpression(Expression stringableExpr, Expression lhsRefExpr,
                                                      Map<Variable, ProverExpr> varMap) {
        ReferenceType lhsRefExprType = (ReferenceType) lhsRefExpr.getType();
        ProverExpr pe = selectInt(stringableExpr, varMap);
        return mkCharToString(pe, lhsRefExprType);
    }

    public EncodingFacts handleStringExpr(Expression e, Map<Variable, ProverExpr> varMap) {
        if (e instanceof BinaryExpression) {
            final BinaryExpression be = (BinaryExpression) e;
            Expression leftExpr = be.getLeft();
            Expression rightExpr = be.getRight();
            switch (be.getOp()) {
                case StringConcat: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringConcat(leftPE, rightPE, (ReferenceType) leftExpr.getType());
                }

                case StringIndexOf: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringIndexOf(leftPE, rightPE, lit(0));
                }

                case StringLastIndexOf: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringLastIndexOf(leftPE, rightPE, p.mkMinus(len(leftPE), len(rightPE)));
                }

                case StringIndexOfChar: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = mkStringPE(((char) selectInt(rightExpr, varMap).getIntLiteralValue().intValue()) + "");
                    return mkStringIndexOf(leftPE, rightPE, lit(0));
                }

                case StringLastIndexOfChar: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = mkStringPE(((char) selectInt(rightExpr, varMap).getIntLiteralValue().intValue()) + "");
                    return mkStringLastIndexOf(leftPE, rightPE, p.mkMinus(len(leftPE), len(rightPE)));
                }

                case StringCompareTo: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringCompareTo(leftPE, rightPE, (ReferenceType) leftExpr.getType());
                }

                case StringEq: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return new EncodingFacts(null, null, p.mkEq(leftPE, rightPE), lit(true));
                }

                case StartsWith: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringEdgesWith(leftPE, rightPE, true);
                }

                case EndsWith: {
                    final ProverExpr leftPE = selectString(leftExpr, varMap);
                    final ProverExpr rightPE = selectString(rightExpr, varMap);
                    return mkStringEdgesWith(leftPE, rightPE, false);
                }

                case CharAt: {
                    final ProverExpr strPE = selectString(leftExpr, varMap);
                    final ProverExpr indexPE = selectInt(rightExpr, varMap);
                    return mkStringCharAt(strPE, indexPE);
                }

                case ToString: {
                    return mkToStringFromExpression(leftExpr /* stringable */, rightExpr /* lhsRef */, varMap);
                }

                case BoolToString: {
                    return mkBoolToStringFromExpression(leftExpr, rightExpr, varMap);
                }

                case CharToString: {
                    return mkCharToStringFromExpression(leftExpr, rightExpr, varMap);
                }

                default:
                    return null;
            }
        } else if (e instanceof UnaryExpression) {
            final UnaryExpression ue = (UnaryExpression) e;
            switch (ue.getOp()) {
                case Len: {
                    Expression strExpr = ue.getExpression();
                    return mkStringLengthFromExpression(strExpr, varMap);
                }

                default:
                    return null;
            }
        } else if (e instanceof NaryExpression) {
            final NaryExpression te = (NaryExpression) e;
            switch (te.getOp()) {
                case StartsWithOffset: {
                    final ProverExpr leftPE = selectString(te.getExpression(0), varMap);
                    final ProverExpr rightPE = selectString(te.getExpression(1), varMap);
                    final ProverExpr offsetPE = selectInt(te.getExpression(2), varMap);
                    return mkStringEdgesWithOffset(leftPE, rightPE, offsetPE, true);
                }
                case Substring: {
                    final ProverExpr leftPE = selectString(te.getExpression(0), varMap);
                    final ProverExpr startIndexPE = selectInt(te.getExpression(1), varMap);
                    final ProverExpr endIndexPE = selectInt(te.getExpression(2), varMap);
                    return mkStringSubstring(leftPE, startIndexPE, endIndexPE, (ReferenceType) te.getExpression(0).getType());
                }
                case SubstringWithOneIndex: {
                    final ProverExpr leftPE = selectString(te.getExpression(0), varMap);
                    final ProverExpr indexPE = selectInt(te.getExpression(1), varMap);
                    return mkStringSubstring(leftPE, indexPE, (ReferenceType) te.getExpression(0).getType());
                }
                case IndexOfWithOffset:
                case LastIndexOfWithOffset: {
                    final ProverExpr leftPE = selectString(te.getExpression(0), varMap);
                    final ProverExpr rightPE = selectString(te.getExpression(1), varMap);
                    final ProverExpr offsetPE = selectInt(te.getExpression(2), varMap);
                    return (te.getOp().equals(NaryExpression.NaryOperator.IndexOfWithOffset)) ?
                            mkStringIndexOf(leftPE, rightPE, offsetPE)
                            : mkStringLastIndexOf(leftPE, rightPE, offsetPE);
                }
                case IndexOfCharWithOffset:
                case LastIndexOfCharWithOffset: {
                    final ProverExpr leftPE = selectString(te.getExpression(0), varMap);
                    final ProverExpr rightPE = mkStringPE(((char) selectInt(te.getExpression(1), varMap).getIntLiteralValue().intValue()) + "");
                    final ProverExpr offsetPE = selectInt(te.getExpression(2), varMap);
                    return (te.getOp().equals(NaryExpression.NaryOperator.IndexOfCharWithOffset)) ?
                            mkStringIndexOf(leftPE, rightPE, offsetPE)
                            : mkStringLastIndexOf(leftPE, rightPE, offsetPE);
                }
                default:
                    return null;
            }
        }

        return null;
    }

}
