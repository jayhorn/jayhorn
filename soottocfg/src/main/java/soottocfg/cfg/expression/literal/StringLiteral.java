/**
 *
 */
package soottocfg.cfg.expression.literal;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import soot.RefType;
import soot.jimple.ClassConstant;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.StringType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

import javax.annotation.Nullable;


/**
 * will be a reference to a nondetString if its value is null
 */
public class StringLiteral extends IdentifierExpression {

    // TODO: This is a random number distinct from other serialVersionUIDs. Is this the intended value?
    private static final long serialVersionUID = 2832524893670085097L;

    @Nullable
    private final String value;


    public StringLiteral(SourceLocation loc, Variable variable, @Nullable String value) {
        super(loc, variable);
        this.value = value;
    }

    public static String escape(String s) {
        // for all escape characters: return StringEscapeUtils.escapeJava(String.format("\"%s\"", s));   // needs Apache Commons
        return String.format("\"%s\"",
                s
                        .replace("\"", "\\\"")
                        .replace("\\", "\\\\")
                        .replace("\r", "\\r")
                        .replace("\n", "\\n")
                        .replace("\t", "\\t")
                        .replace("\b", "\\b")
                        .replace("\f", "\\f")
        );
    }

    @Override
    public String toString() {
        if (this.value != null)
            return StringLiteral.escape(this.value);
        else
            return "<nondetString>";
    }

    @Override
    public Set<IdentifierExpression> getUseIdentifierExpressions() {
        return new HashSet<IdentifierExpression>();
    }

    @Override
    public Set<Variable> getDefVariables() {
        // because this can't happen on the left.
        return new HashSet<Variable>();
    }

    @Nullable
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        // resolve FindBugs Report:
        //		HE_EQUALS_USE_HASHCODE: Class defines equals() and uses Object.hashCode()
        if (this.value != null)
            return this.value.hashCode();
        else
            return 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StringLiteral) {
            StringLiteral otherSL = (StringLiteral)other;
            if (otherSL.getValue() == null && this.value == null)
                return true;
            return otherSL.getValue().equals(this.value);
        }
        return false;
    }

    @Override
    public StringLiteral substitute(Map<Variable, Variable> subs) {
        if (subs.containsKey(getVariable())) {
            Variable newVar = subs.get(getVariable());
            if (newVar != getVariable())
                return new StringLiteral(getSourceLocation(), newVar, value);
        }
        return this;
    }

//    public static ReferenceType mkStringReferenceType() {
//        LinkedHashMap<String, Type> elementTypes = ReferenceType.mkDefaultElementTypes();
//        elementTypes.put("$String", StringType.instance());
//        RefType t = RefType.v("java.lang.String");
//        return new ReferenceType(SootTranslationHelpers.v().getMemoryModel().lookupClassVariable(
//                ClassConstant.v("java/lang/String")), elementTypes);  // NullPointerException since program is null
//    }
}