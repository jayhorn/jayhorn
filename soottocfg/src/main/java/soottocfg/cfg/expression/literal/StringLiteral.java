/**
 *
 */
package soottocfg.cfg.expression.literal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.variable.Variable;

public class StringLiteral extends IdentifierExpression {

    // TODO: This is a random number distinct from other serialVersionUIDs. Is this the intended value?
    private static final long serialVersionUID = 2832524893670085097L;

    private final String value;


    public StringLiteral(SourceLocation loc, Variable variable, String value) {
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
        return StringLiteral.escape(this.value);
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

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        // resolve FindBugs Report:
        //		HE_EQUALS_USE_HASHCODE: Class defines equals() and uses Object.hashCode()
        return this.value.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StringLiteral) {
            return ((StringLiteral) other).getValue().equals(this.value);
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

}