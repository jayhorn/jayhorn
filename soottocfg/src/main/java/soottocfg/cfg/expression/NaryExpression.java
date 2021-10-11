package soottocfg.cfg.expression;

import com.google.common.base.Verify;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.StringType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NaryExpression extends Expression {

    private static final long serialVersionUID = 1992559247136566989L;

    public enum NaryOperator {
        StartsWithOffset("startsWithOffset"), Substring("substring"), SubstringWithOneIndex("substringWithOneIndex"),
        IndexOfWithOffset("indexOfWithOffset"), IndexOfCharWithOffset("indexOfCharWithOffset"),
        LastIndexOfWithOffset("lastIndexOfWithOffset"), LastIndexOfCharWithOffset("lastIndexOfCharWithOffset");

        private final String name;

        private NaryOperator(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);	// Object.equals(o) returns false when o is null
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private final NaryOperator op;
    private final Expression[] expressions;

    public NaryExpression(SourceLocation loc, NaryOperator op,
                          Expression ... expressions) {
        super(loc);
        this.op = op;
        switch (this.op) {
            case StartsWithOffset:
            case Substring:
            case IndexOfWithOffset:
            case IndexOfCharWithOffset:
            case LastIndexOfWithOffset:
            case LastIndexOfCharWithOffset:
                Verify.verify(expressions.length == 3);
                this.expressions = Arrays.copyOf(expressions, expressions.length);
                break;
            case SubstringWithOneIndex:
                Verify.verify(expressions.length == 2);
                this.expressions = Arrays.copyOf(expressions, expressions.length);
                break;
            default:
                throw new RuntimeException("not implemented");
        }
    }

    public int getArity() {
        return this.expressions.length;
    }

    public Expression getExpression(int index) {
        return this.expressions[index];
    }

    public NaryOperator getOp() {
        return op;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append("<");
        sb.append(this.op);
        sb.append(">");
        for (Expression e: expressions) {
            sb.append(",");
            sb.append(e);
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Set<IdentifierExpression> getUseIdentifierExpressions() {
        Set<IdentifierExpression> ret = new HashSet<IdentifierExpression>();
        for (Expression e: expressions) {
            ret.addAll(e.getUseIdentifierExpressions());
        }
        return ret;
    }

    @Override
    public Set<Variable> getDefVariables() {
        // because this can't happen on the left.
        Set<Variable> used = new HashSet<Variable>();
        return used;
    }

    @Override
    public Type getType() {
        switch (op) {
            case StartsWithOffset:
                return BoolType.instance();
            case Substring:
            case SubstringWithOneIndex:
                return StringType.instance();
            case IndexOfWithOffset:
            case IndexOfCharWithOffset:
            case LastIndexOfWithOffset:
            case LastIndexOfCharWithOffset:
                return IntType.instance();
            default:
                throw new RuntimeException("not implemented");
        }
    }

    @Override
    public Expression substitute(Map<Variable, Variable> subs) {
        Expression[] newExpressions = new Expression[expressions.length];
        boolean same = true;
        for (int i = 0; i < expressions.length; i++) {
            newExpressions[i] = expressions[i].substitute(subs);
            if (expressions[i] != newExpressions[i])
                same = false;
        }
        if (same)
            return this;
        else
            return new NaryExpression(getSourceLocation(), op, newExpressions);
    }

    @Override
    public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
        Expression[] newExpressions = new Expression[expressions.length];
        boolean same = true;
        for (int i = 0; i < expressions.length; i++) {
            newExpressions[i] = expressions[i].substituteVarWithExpression(subs);
            if (expressions[i] != newExpressions[i])
                same = false;
        }
        if (same)
            return this;
        else
            return new NaryExpression(getSourceLocation(), op, newExpressions);
    }
}
