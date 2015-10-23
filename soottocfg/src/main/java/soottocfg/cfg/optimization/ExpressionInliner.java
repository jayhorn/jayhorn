package soottocfg.cfg.optimization;

import java.util.HashSet;
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



	//Given an assignment statement, replace the readers with the rhs of the statement
	public void inlineExpression(AssignStatement s, Method m)
	{
		Expression lhs = s.getLeft();
		//DSN - I Assume that it can only be a variable.
		assert(lhs instanceof IdentifierExpression);
		toReplace = ((IdentifierExpression) lhs).getVariable();
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

	public void inlineAllCandidates(Method m)
	{
		for(AssignStatement s : candidatesForInlining(m)){
			inlineExpression(s,m);
		}
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
			return replaceWith;
		} else {
			return e;
		}
	}


}
