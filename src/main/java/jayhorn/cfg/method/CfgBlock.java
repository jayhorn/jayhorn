/**
 * 
 */
package jayhorn.cfg.method;

import java.util.LinkedList;
import java.util.List;

import jayhorn.cfg.Node;
import jayhorn.cfg.expression.Expression;
import jayhorn.cfg.statement.Statement;

/**
 * @author schaef
 *
 */
public class CfgBlock implements Node {

	protected final List<CfgBlock> predecessors, successors;
	protected final List<Statement> statements;
	
	public CfgBlock() {
		this.predecessors = new LinkedList<CfgBlock>();
		this.successors = new LinkedList<CfgBlock>();
		this.statements = new LinkedList<Statement>();
	}
	
	public void addStatement(Statement s) {
		this.statements.add(s);
	}
	
	public void addSuccessor(CfgBlock suc) {
		if (this.successors.contains(suc)) {
			throw new RuntimeException("Already connected");
		}
		this.successors.add(suc);
	}

	public void addConditionalSuccessor(Expression condition, CfgBlock suc) {
		if (this.successors.contains(suc)) {
			throw new RuntimeException("Already connected");
		}
		//TODO: handle the condition.
		this.successors.add(suc);		
	}
	
	public List<CfgBlock> getSuccessors() {
		return this.successors;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Block "+this.hashCode());
		sb.append("\n");
		for (Statement s : this.statements) {
			sb.append("\t");
			sb.append(s.toString());
			sb.append("\n");
		}
		if (!this.successors.isEmpty()) {
			sb.append("\tgoto ");
			for (CfgBlock s : this.successors) {
				sb.append("Block");
				sb.append(s.hashCode());
				sb.append(", ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
