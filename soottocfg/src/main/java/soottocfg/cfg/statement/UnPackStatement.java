/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.type.ClassSignature;

/**
 * @author schaef
 *
 */
public class UnPackStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9221818898828469398L;
	private final ClassSignature classConstant;
	private final IdentifierExpression object;
	private final List<IdentifierExpression> left;

	/**
	 * @param loc
	 */
	public UnPackStatement(SourceLocation loc, ClassSignature c, IdentifierExpression obj,
			List<IdentifierExpression> lhs) {
		super(loc);
		classConstant = c;
		object = obj;
		left = new LinkedList<IdentifierExpression>(lhs);
		assert (c.getAssociatedFields().length == left.size());
	}

	public List<IdentifierExpression> getLeft() {
		return left;
	}
	
    public ClassSignature getClassSignature() {
        return classConstant;
    }

    public Expression getObject() {
        return object;
    }

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.add(object);
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		res.addAll(left);
		return res;
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
		sb.append(" := unpack(");
		sb.append(classConstant.getName());
		sb.append(", ");
		sb.append(object);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Statement deepCopy() {
		List<IdentifierExpression> leftCopy = new LinkedList<IdentifierExpression>();
		for (IdentifierExpression e : left) {
			leftCopy.add(e.deepCopy());
		}
		return new UnPackStatement(getSourceLocation(), classConstant, object.deepCopy(), leftCopy);
	}

}
