package soottocfg.cfg.optimization;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;

public class ExpressionInliner extends CfgUpdater {
	ExpressionInliner(){}

	private Variable toReplace;
	private Expression replaceWith;

	protected Variable getLhsVariable(AssignStatement s){
		Expression lhs = s.getLeft();
		//DSN - I Assume that it can only be a variable.
		assert(lhs instanceof IdentifierExpression);
		return ((IdentifierExpression) lhs).getVariable();
	}
	
	//Given an assignment statement, replace the readers with the rhs of the statement
	//TODO make this boolean
	public void inlineExpression(AssignStatement s, Method m)
	{
		toReplace = getLhsVariable(s);
		replaceWith = s.getRight();

		Set<Statement> definingStmts = m.computeDefiningStatements().get(toReplace);
		if(definingStmts.size() != 1){
			throw new RuntimeException("Cannot inline: Variable is defined in more than one place: " + toReplace + "\n" + definingStmts);
		}
		//We know that this is the only defn for the variable, so its clearly safe to override.
		for(CfgBlock b : m.computeUsingBlocks().get(toReplace)){
			processCfgBlock(b);
		}
	}

	@Override
	public boolean updateMethod(Method m){
		return inlineAllCandidates(m);
	}
	
	public boolean inlineAllCandidates(Method m)
	{
		changed = false;
		for(AssignStatement s : candidatesForInlining(m)){
			inlineExpression(s,m) ;
		}
		return changed;
	}
	
	public boolean inlineIfUsesLessThan(Method m, int maxUses)
	{
		changed = false;
		Map<Variable,Set<Statement>> usingStmts = m.computeUsingStatements();
		for(AssignStatement s : candidatesForInlining(m)){
			Variable v = getLhsVariable(s);
			if(usingStmts.get(v).size() < maxUses){
				inlineExpression(s,m);
			}
		}
		return changed;
	}
	
	public Set<AssignStatement> candidatesForInlining(Method m)
	{
		Set<AssignStatement> rval = new HashSet<AssignStatement>();
		for(Entry<Variable,Set<Statement>> entry: m.computeDefiningStatements().entrySet()){
			if(entry.getValue().size() == 1){
				Statement s = entry.getValue().iterator().next();
				if (s instanceof AssignStatement){
					rval.add((AssignStatement)s);	
				}
			}
		}
		return rval;
	}

	@Override 
	protected Expression processExpression(IdentifierExpression e) {
		if(e.getVariable().equals(toReplace)){
			changed = true;
			return replaceWith;
		} else {
			return e;
		}
	}


}
