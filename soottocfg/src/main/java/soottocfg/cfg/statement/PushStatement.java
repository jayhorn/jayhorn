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
	
	private final List<Expression> ghostExpressions;
	
	private final int id;	
	private static int nextID = 0;
	
	/**
	 * @param loc
	 */
	public PushStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj, List<Expression> rhs, int id) {
		this(loc, c, obj, rhs, null, id);
	}
	
	public PushStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj, List<Expression> rhs, List<Expression> ghostExpressions, int id) {
		super(loc);
		this.ghostExpressions = new LinkedList<Expression>();
		if (ghostExpressions!=null) {
			this.ghostExpressions.addAll(ghostExpressions);
		}
		classConstant = c;
		object = obj;
		right = new LinkedList<Expression>(rhs);
		this.id = id;
		verifySize();
	}
	
	private void verifySize() {
		if (classConstant.getAssociatedFields().length != right.size()) {
			StringBuilder err = new StringBuilder();
			err.append(object);
			err.append(" has fields ");
			String comma = "";
			err.append("[");
			for (Variable cv : classConstant.getAssociatedFields()) {
				err.append(comma);
				comma = ", ";
				err.append(cv.getName()+":"+cv.getType());
			}
			err.append("] but is assigned to ");
			err.append("[");
			comma = "";
			for (Expression e : right) {
				err.append(comma);
				comma = ", ";
				err.append(e+":"+e.getType());				
			}
			err.append("]");
			Verify.verify(false, err.toString());
		}
	}
	
	public PushStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj, List<Expression> rhs) {
		this(loc, c, obj, rhs, nextID());
	}

    public List<Expression> getGhostExpressions() {
    	return this.ghostExpressions;
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
    
    
	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		for (Expression e : right) {
			used.addAll(e.getUseIdentifierExpressions());	
		}
		for (Expression e: ghostExpressions) {
			used.addAll(e.getUseIdentifierExpressions());
		}
//TODO: I'm not sure if a push is a use or a def of 'object' ... or both?
        used.add(object);
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
//		res.add(object);
		return res;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("push_");
                sb.append(id);
		sb.append("(");
		sb.append(classConstant.getName());
		sb.append(", ");
		sb.append(object);
		for (Expression e : this.ghostExpressions) {
			sb.append(", ");
			sb.append(e);
		}

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
			rightCopy.add(e);
		}
		List<Expression> ghostCopy = new LinkedList<Expression>();
		for (Expression e : ghostExpressions) {
			ghostCopy.add(e);
		}
		return new PushStatement(getSourceLocation(), classConstant, object, rightCopy, ghostCopy, this.id);
	}

	@Override
	public PushStatement substitute(Map<Variable, Variable> subs) {
            boolean changed = false;
            List<Expression> rightCopy = new LinkedList<Expression>();
            for (Expression e : right) {
                Expression newE = e.substitute(subs);
                changed = changed || (newE != e);
                rightCopy.add(newE);
            }
            List<Expression> ghostCopy = new LinkedList<Expression>();
            for (Expression e : ghostExpressions) {
                Expression newE = e.substitute(subs);
                changed = changed || (newE != e);
                ghostCopy.add(newE);
            }

            IdentifierExpression newObj = object.substitute(subs);
            changed = changed || (newObj != object);

            if (changed)
		return new PushStatement(getSourceLocation(), classConstant, newObj, rightCopy, ghostCopy, this.id);
            else
                return this;
	}
	
	@Override
	public PushStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
            boolean changed = false;
            List<Expression> rightCopy = new LinkedList<Expression>();
            for (Expression e : right) {
                Expression newE = e.substituteVarWithExpression(subs);
                changed = changed || (newE != e);
                rightCopy.add(newE);
            }
            List<Expression> ghostCopy = new LinkedList<Expression>();
            for (Expression e : ghostExpressions) {
                Expression newE = e.substituteVarWithExpression(subs);
                changed = changed || (newE != e);
                ghostCopy.add(newE);
            }

            if (changed)
		return new PushStatement(getSourceLocation(), classConstant, object, rightCopy, ghostCopy, this.id);
            else
                return this;
	}	
	
}
