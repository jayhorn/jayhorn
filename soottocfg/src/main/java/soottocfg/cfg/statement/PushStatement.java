/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class PushStatement extends Statement {

	private static final long serialVersionUID = 5776310555422969945L;
	private final ClassVariable classConstant;
	private IdentifierExpression object;
	private final List<Expression> right;
	
	private final int id;	
	private static int nextID = 0;
	
	/**
	 * @param loc
	 */
	public PushStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj, List<Expression> rhs, int id) {
		super(loc);
		classConstant = c;
		object = obj;
		right = new LinkedList<Expression>(rhs);
		if (c.getAssociatedFields().length != right.size()) {
			StringBuilder err = new StringBuilder();
			err.append(obj);
			err.append(" has fields ");
			String comma = "";
			err.append("[");
			for (Variable cv : c.getAssociatedFields()) {
				err.append(comma);
				comma = ", ";
				err.append(cv.getName());
			}
			err.append("] but is assigned to ");
			err.append(rhs);
			Verify.verify(false, err.toString());
		}
		this.id = id;
	}
	
	public PushStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj, List<Expression> rhs) {
		this(loc, c, obj, rhs, nextID());
	}

	// had to put this in a method to silence findBugs...
	private static int nextID() {
		return ++nextID;
	}
	
    public ClassVariable getClassSignature() {
        return classConstant;
    }

    public Expression getObject() {
        return object;
    }

    public List<Expression> getRight() {
        return right;
    }
    
    public int getID() {
    	return id;
    }
    
    public void addGhostField(Expression e) {
    	right.add(e);
    }
    
	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		for (Expression e : right) {
			used.addAll(e.getUseIdentifierExpressions());	
		}
                used.add(object);
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		res.add(object);
		return res;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("push(");
		sb.append(classConstant.getName());
		sb.append(", ");
		sb.append(object);
		sb.append(", [");
		String comma = "";
		for (Expression v : right) {
			sb.append(comma);
			sb.append(v);
			comma = ", ";
		}
		sb.append("]");
		sb.append(")");
		return sb.toString();
	}

	// TODO check where this is used and what to do with ID
	@Override
	public Statement deepCopy() {
		List<Expression> rightCopy = new LinkedList<Expression>();
		for (Expression e : right) {
			rightCopy.add(e.deepCopy());
		}
		return new PushStatement(getSourceLocation(), classConstant, object.deepCopy(), rightCopy, this.id);
	}

	@Override
	public void substitute(Map<Variable, Expression> subs) {
		for (int i=0;i<this.right.size(); i++) {
			this.right.set(i, this.right.get(i).substitute(subs));
		}
		this.object = (IdentifierExpression)this.object.substitute(subs);
	}
}
