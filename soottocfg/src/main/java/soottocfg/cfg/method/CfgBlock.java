/**
 * 
 */
package soottocfg.cfg.method;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Node;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.statement.Statement;
import soottocfg.soot.util.SootTranslationHelpers;
import soottocfg.util.SetOperations;

import java.util.Set;

/**
 * @author schaef
 *
 */
public class CfgBlock implements Node {

	protected final String label;
	
	protected final List<CfgBlock> predecessors;
	protected final List<CfgBlock> successors;
	protected List<Statement> statements;
	protected final Map<CfgBlock, Expression> successorConditions;
	protected final Method method;
	
	public CfgBlock(Method m) {
		this.label = "Block"+(SootTranslationHelpers.v().getUniqueNumber());
		
		this.successors = new LinkedList<CfgBlock>();
		this.statements = new LinkedList<Statement>();
		this.successorConditions = new HashMap<CfgBlock, Expression>();
		this.predecessors = new LinkedList<CfgBlock>();
		this.method = m;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void addStatement(Statement s) {
		this.statements.add(s);
	}
	
	public List<Statement> getStatements() {
		return this.statements;
	}
		
	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	public void addPredecessor(CfgBlock pred)
	{
		if (this.predecessors.contains(pred)) {
			throw new RuntimeException("Already connected: " + pred);
		}
		this.predecessors.add(pred);
	}
	
	public void addSuccessor(CfgBlock suc) {
		if (this.successors.contains(suc)) {
			throw new RuntimeException("Already connected");
		}
		this.successors.add(suc);
		suc.addPredecessor(this);
	}

	public void addConditionalSuccessor(Expression condition, CfgBlock suc) {
		this.addSuccessor(suc);
		this.successorConditions.put(suc, condition);
	}
	
	public void updateConditionalSuccessor(Expression cond, CfgBlock suc)
	{
		assert(successorConditions.containsKey(suc));
		assert(successors.contains(suc));
		successorConditions.put(suc,cond);
	}
	
	public Map<CfgBlock, Expression> getSuccessorConditions() {
		return successorConditions;
	}

	public List<CfgBlock> getSuccessors() {
		return this.successors;
	}
	
	public List<CfgBlock> getPredecessors() {
		return predecessors;
	}

	/**
     * Return the condition associated with the exit edge from this
     * block, or <code>null</code> if no condition exists.
     */
    public Expression getSuccessorCondition(CfgBlock succ) {
        return successorConditions.get(succ);
    }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.label);
		sb.append(":\n");
		for (Statement s : this.statements) {
			sb.append("(ln ");
			sb.append(s.getJavaSourceLine());
			sb.append(")\t");
			sb.append(s.toString());
			sb.append("\n");
		}
		if (!this.successors.isEmpty()) {
			sb.append("\tgoto:\n");
			for (CfgBlock suc : this.successors) {
				sb.append("\t  ");
				if (this.successorConditions.containsKey(suc)) {
					sb.append("if ");
					sb.append(this.successorConditions.get(suc));
					sb.append(": ");
				}
				sb.append(suc.getLabel());
				sb.append("\n");
			}
		} else {
			sb.append("\treturn\n");
		}
		return sb.toString();
	}
	
	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (Statement s : statements) {
			used.addAll(s.getUsedVariables());
		}
		//TODO: do the variables in the conditional belong to this block?
		for (Entry<CfgBlock, Expression> entry : successorConditions.entrySet()) {
			used.addAll(entry.getValue().getUsedVariables());
		}
		return used;
	}
	
	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (Statement s : statements) {
			used.addAll(s.getLVariables());
		}
		return used;
	}	
	

	
	//Calculates the live-in variables for each statement
	public LiveVars<Statement> computeLiveVariables(LiveVars<CfgBlock> vars) {

		//Reserve the necessary size in the hashmap
		Map<Statement,Set<Variable>> in = new HashMap<Statement,Set<Variable>>(getStatements().size());
		Map<Statement,Set<Variable>> out = new HashMap<Statement,Set<Variable>>(getStatements().size());

		//Start by initializing in to empty.  
		for (Statement s: getStatements()){
			in.put(s, new HashSet<Variable>());
		}

		//Start with the variables that are live out of the block are also live out of the last statement
		Set<Variable> currentLiveOut = vars.liveOut.get(this);
		
		//Go through the statements in reverse order 
		for (ListIterator<Statement> li = getStatements().listIterator(getStatements().size()); li.hasPrevious(); ){
			Statement stmt = li.previous();
			out.put(stmt, currentLiveOut);
			Set<Variable> liveIn = SetOperations.union(stmt.getUsedVariables(), SetOperations.minus(currentLiveOut, stmt.getLVariables()));
			in.put(stmt, liveIn);
			currentLiveOut = liveIn;
		}
		
		//The live in of the 0th statement should be the same as the live in of the whole block
		assert(currentLiveOut.equals(vars.liveIn.get(this)));
		return new LiveVars<Statement>(in, out);
	}
	
	public Set<Variable> computeLiveOut(Map<CfgBlock,Set<Variable>> in)
	{
		Set<Variable> out = new HashSet<Variable>();
		for(CfgBlock s : getSuccessors()){
			out.addAll(in.get(s));
		}
		return out;
	}
	
}
