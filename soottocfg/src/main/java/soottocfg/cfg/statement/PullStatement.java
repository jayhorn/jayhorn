package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class PullStatement extends Statement {

	private static final long serialVersionUID = 9221818898828469398L;
	private final ClassVariable classConstant;
	private IdentifierExpression object;
	private final List<IdentifierExpression> left;

	private final List<Expression> ghostExpressions;
	
	private Set<PushStatement> canAffect;
	
	public PullStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj,
			List<IdentifierExpression> lhs) {
		this(loc, c, obj, lhs, null);
	}
	/**
	 * @param loc
	 */
	public PullStatement(SourceLocation loc, ClassVariable c, IdentifierExpression obj,
			List<IdentifierExpression> lhs, List<Expression> ghostExp) {
		super(loc);
		classConstant = c;
		object = obj;
		left = new LinkedList<IdentifierExpression>(lhs);
		this.ghostExpressions = new LinkedList<Expression>();
		if (ghostExp!=null) {
			this.ghostExpressions.addAll(ghostExp);
		}
		assert (c.getAssociatedFields().length == left.size());
		this.canAffect = new HashSet<PushStatement>();
	}

	public List<IdentifierExpression> getLeft() {
		return left;
	}
	
    public List<Expression> getGhostExpressions() {
    	return this.ghostExpressions;
    }
		
    public ClassVariable getClassSignature() {
        return classConstant;
    }

    public Expression getObject() {
        return object;
    }

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.add(object);
//		for (Expression e: ghostExpressions) {
//			used.addAll(e.getUseIdentifierExpressions());
//		}
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		res.addAll(left);
		for (Expression e: ghostExpressions) {
			res.addAll(e.getUseIdentifierExpressions());
		}

		return res;
	}

	public Set<PushStatement> getAffectingPushes() {
		return canAffect;
	}
	
	public void canAffect(PushStatement push) {
		canAffect.add(push);
	}

	public void canAffect(Set<PushStatement> pushes) {
		canAffect.addAll(pushes);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String comma = "";
		for (IdentifierExpression v : left) {
			sb.append(comma);
			sb.append(v);
			comma = ", ";
		}
		sb.append(" := pull(");
		sb.append(classConstant.getName());
		sb.append(", ");
		sb.append(object);
		for (Expression e : this.ghostExpressions) {
			sb.append(", ");
			sb.append(e);
		}
		sb.append(")");
		sb.append("[");
		comma = "";
		for (PushStatement push : canAffect) {
			sb.append(comma);
			sb.append(push.getID());
			comma = ", ";
		}
		sb.append("]");
		return sb.toString();
	}

	@Override
	public PullStatement deepCopy() {
		List<IdentifierExpression> leftCopy = new LinkedList<IdentifierExpression>();
		for (IdentifierExpression e : left) {
			leftCopy.add(e);
		}
		List<Expression> ghostCopy = new LinkedList<Expression>();
		for (Expression e : ghostExpressions) {
			ghostCopy.add(e);
		}
		return new PullStatement(getSourceLocation(), classConstant, object, leftCopy, ghostCopy);
	}

	@Override
	public PullStatement substitute(Map<Variable, Variable> subs) {
            boolean changed = false;
            List<IdentifierExpression> leftCopy = new LinkedList<IdentifierExpression>();
            for (IdentifierExpression e : left) {
                IdentifierExpression newE = e.substitute(subs);
                changed = changed || (newE != e);
                leftCopy.add(newE);
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
                return new PullStatement(getSourceLocation(), classConstant, newObj, leftCopy, ghostCopy);
            else
                return this;
	}

	@Override
	public PullStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
            boolean changed = false;
            List<IdentifierExpression> leftCopy = new LinkedList<IdentifierExpression>();
            for (IdentifierExpression e : left) {
                leftCopy.add(e);
            }
            List<Expression> ghostCopy = new LinkedList<Expression>();
            for (Expression e : ghostExpressions) {
                Expression newE = e.substituteVarWithExpression(subs);
                changed = changed || (newE != e);
                ghostCopy.add(newE);
            }
		
            if (changed)
		return new PullStatement(getSourceLocation(), classConstant, object, leftCopy, ghostCopy);
            else
                return this;
	}
	
}
