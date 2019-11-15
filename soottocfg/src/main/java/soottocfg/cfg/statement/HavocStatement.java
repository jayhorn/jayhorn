/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.variable.Variable;

public class HavocStatement extends Statement {

    private final IdentifierExpression variable;

    public HavocStatement(SourceLocation loc, IdentifierExpression var) {
        super(loc);
        variable = var;
    }

    public IdentifierExpression getHavocedExpression() {
        return variable;
    }

    public Variable getVariable() {
        return variable.getVariable();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("havoc ");
        sb.append(variable.toString());
        return sb.toString();
    }

    @Override
    public Set<IdentifierExpression> getUseIdentifierExpressions() {
        Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
        return used;
    }
    
    @Override
    public Set<IdentifierExpression> getDefIdentifierExpressions() {
        Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
        res.add(variable);
        return res;
    }

    @Override
    public Statement deepCopy() {
        return new HavocStatement(getSourceLocation(), variable);
    }

    @Override
    public HavocStatement substitute(Map<Variable, Variable> subs) {
        Variable oldVar = variable.getVariable();
        Variable newVar = subs.get(oldVar);
        if (newVar != null && newVar != oldVar)
            return new HavocStatement(
                         getSourceLocation(),
                         new IdentifierExpression(getSourceLocation(), newVar));
        return this;
    }

    @Override
    public HavocStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
        return this;
    }

}
