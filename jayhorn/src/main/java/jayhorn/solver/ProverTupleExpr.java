package jayhorn.solver;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Stack;
import java.math.BigInteger;

public class ProverTupleExpr implements ProverExpr {
    
    /**
     * Eliminate tuples by expanding them.
     */
    public static ProverExpr[] flatten(ProverExpr[] exprs) {
        ArrayList<ProverExpr> res = new ArrayList<ProverExpr>();
        
        Stack<ProverExpr> todo = new Stack<ProverExpr>();
        for (int i = exprs.length - 1; i >= 0; --i)
            todo.push(exprs[i]);

        while (!todo.isEmpty()) {
            ProverExpr next = todo.pop();
            if (next instanceof ProverTupleExpr) {
                ProverTupleExpr tnext = (ProverTupleExpr)next;
                for (int i = tnext.getArity() - 1; i >= 0; --i)
                    todo.push(tnext.getSubExpr(i));
            } else {
                res.add(next);
            }
        }

        return res.toArray(new ProverExpr[0]);
    }

    private final ProverExpr[] subExprs;
    private ProverTupleType type = null;
    
    public ProverTupleExpr(ProverExpr[] subExprs) {
        this.subExprs = subExprs;
    }
    
    public int getArity() {
        return subExprs.length;
    }

    public ProverExpr getSubExpr(int ind) {
        return subExprs[ind];
    }

    public ProverTupleType getType() {
        if (type == null) {
            final ProverType[] subTypes = new ProverType[subExprs.length];
            for (int i = 0; i < subExprs.length; ++i)
                subTypes[i] = subExprs[i].getType();
            type = new ProverTupleType(subTypes);
        }
        return type;
    }

    public BigInteger getIntLiteralValue() {
        throw new UnsupportedOperationException();
    }

    public boolean getBooleanLiteralValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append("[");
        String sep = "";
        for (int i = 0; i < getArity(); ++i) {
            res.append(getSubExpr(i));
            res.append(sep);
            sep = ", ";
        }
        res.append("]");
        return res.toString();
    }

    @Override
    public int hashCode() {
        return 12345 * Arrays.hashCode(subExprs);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProverTupleExpr other = (ProverTupleExpr) obj;
        return Arrays.equals(this.subExprs, other.subExprs);
    }

}
