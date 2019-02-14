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

    // FIXME: This is a random number distinct from other serialVersionUIDs. Is this the intended value?
    private static final long serialVersionUID = 2832524893670085097L;

    private final String value;


    public StringLiteral(SourceLocation loc, Variable variable, String value) {
        super(loc, variable);
        this.value = value;
    }

    @Override
    public String toString() {
        // TODO: return StringEscapeUtils.escapeJava(String.format("\"%s\"", value));   // needs Apache Commons
        return String.format("\"%s\"",
                value
                .replace("\"", "\\\"")
                .replace("\\", "\\\\")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
        );
    }


    @Override
    public Set<Variable> getDefVariables() {
        // because this can't happen on the left.
        Set<Variable> used = new HashSet<Variable>();
        return used;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof StringLiteral) {
            return ((StringLiteral) other).getValue().equals(this.value);
        }
        return false;
    }

    @Override
    public IdentifierExpression deepCopy() {
        return new StringLiteral(getSourceLocation(), getVariable(), value);
    }

    public IdentifierExpression substitute(Map<Variable, Variable> subs) {
        return this.deepCopy();
    }

    @Override
    public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
        return this.deepCopy();
    }

}
