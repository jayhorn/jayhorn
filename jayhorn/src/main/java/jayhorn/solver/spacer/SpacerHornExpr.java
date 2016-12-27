/**
 * 
 */
package jayhorn.solver.spacer;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverType;

/**
 * @author schaef
 *
 */
public class SpacerHornExpr implements ProverHornClause {

	private final ProverExpr head;
	private final List<ProverExpr> body;
	private final ProverExpr constraint;
	
	
	
	public SpacerHornExpr(ProverExpr head, ProverExpr[] body,
			ProverExpr constraint) {
		this.head = head;
		this.body = new LinkedList<ProverExpr>();
		for (int i=0;i<body.length; i++) {
			this.body.add(body[i]);
		}
		this.constraint = constraint;
	}

	public ProverExpr getHead() {
		return this.head;
	}
	
	public ProverExpr[] getBody() {
		return this.body.toArray(new ProverExpr[this.body.size()]);
	}
	
	public ProverExpr getConstraint() {
		return this.constraint;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see jhorn.solver.ProverExpr#getType()
	 */
	@Override
	public ProverType getType() {
		return BoolType.INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jhorn.solver.ProverExpr#getIntLiteralValue()
	 */
	@Override
	public BigInteger getIntLiteralValue() {
		throw new RuntimeException("not implemented");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jhorn.solver.ProverExpr#getBooleanLiteralValue()
	 */
	@Override
	public boolean getBooleanLiteralValue() {
		throw new RuntimeException("not implemented");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.head.toString());
		sb.append(" <- ");
		sb.append(this.constraint.toString());
		if (this.body.size()>0) {
			for (ProverExpr e : this.body) {
				sb.append(" && ");
				sb.append(e.toString());
			}
		}
		return sb.toString();
	}

    /**
     * Get the head predicate symbol.
     */
    public ProverFun getHeadFun() {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the head argument terms.
     */
    public ProverExpr[] getHeadArgs() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get the number of body literals.
     */
    public int getArity() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Get the predicate symbol of the body literal <code>num</code>.
     */
    public ProverFun getBodyFun(int num) {
        throw new UnsupportedOperationException();
    }

    /**
     * Get the arguments of the body literal <code>num</code>.
     */
    public ProverExpr[] getBodyArgs(int num) {
        throw new UnsupportedOperationException();
    }

}
