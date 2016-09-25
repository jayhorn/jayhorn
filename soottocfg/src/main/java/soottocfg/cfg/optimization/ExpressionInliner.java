package soottocfg.cfg.optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.Variable;

public class ExpressionInliner extends CfgUpdater {
	public ExpressionInliner() {
	}

	private Variable toReplace;
	private Expression replaceWith;

	protected Variable getLhsVariable(AssignStatement s) {
		Expression lhs = s.getLeft();
		// DSN - I Assume that it can only be a variable.
		assert (lhs instanceof IdentifierExpression);
		return ((IdentifierExpression) lhs).getVariable();
	}

	// Given an assignment statement, replace the readers with the rhs of the
	// statement
	// TODO make this boolean
	public void inlineExpression(AssignStatement s, Method m) {
		toReplace = getLhsVariable(s);
		replaceWith = s.getRight();

		Set<Statement> definingStmts = computeDefiningStatements(m).get(toReplace);
		if (definingStmts.size() != 1) {
			throw new RuntimeException(
					"Cannot inline: Variable is defined in more than one place: " + toReplace + "\n" + definingStmts);
		}
		// We know that this is the only defn for the variable, so its clearly
		// safe to override.
		for (CfgBlock b : computeBlocksContainingVariable(m).get(toReplace)) {
			processCfgBlock(b);
		}
	}

	@Override
	public boolean updateMethod(Method m) {
		return inlineAllCandidates(m);
	}

	public boolean inlineAllCandidates(Method m) {
		changed = false;
		for (AssignStatement s : candidatesForInlining(m)) {
			inlineExpression(s, m);
		}
		return changed;
	}

	public boolean inlineIfUsesLessThan(Method m, int maxUses) {
		changed = false;
		Map<Variable, Set<Statement>> usingStmts = computeStatementsContainingVariable(m);
		for (AssignStatement s : candidatesForInlining(m)) {
			Variable v = getLhsVariable(s);
			if (usingStmts.get(v).size() < maxUses) {
				inlineExpression(s, m);
			}
		}
		return changed;
	}

	public Set<AssignStatement> candidatesForInlining(Method m) {
		Set<AssignStatement> rval = new HashSet<AssignStatement>();
		for (Entry<Variable, Set<Statement>> entry : computeDefiningStatements(m).entrySet()) {
			if (entry.getValue().size() == 1) {
				Statement s = entry.getValue().iterator().next();
				if (s instanceof AssignStatement) {
					rval.add((AssignStatement) s);
				}
			}
		}
		return rval;
	}

	@Override
	protected Statement processStatement(AssignStatement s) {
		// We don't process the lhs of the assign statement, because otherwise
		// we'd get
		// b:= e
		// turns into
		// e := e
		Expression lhs = s.getLeft();
		Expression rhs = processExpression(s.getRight());
		return new AssignStatement(s.getSourceLocation(), lhs, rhs);
	}

	@Override
	protected Expression processExpression(IdentifierExpression e) {
		if (e.getVariable().equals(toReplace)) {
			changed = true;
			return replaceWith;
		} else {
			return e;
		}
	}

	// Really simple for now: Just get all blocks that define each variable.
	// Don't worry too much about
	// dominators, etc
	// TODO worry about dominators etc
	protected Map<Variable, Set<CfgBlock>> computeDefiningBlocks(Method m) {
		Map<Variable, Set<CfgBlock>> rval = new HashMap<Variable, Set<CfgBlock>>();
		for (CfgBlock b : m.vertexSet()) {
			for (Variable v : b.getDefVariables()) {
				Set<CfgBlock> definingBlocks = rval.get(v);
				if (definingBlocks == null) {
					definingBlocks = new HashSet<CfgBlock>();
					rval.put(v, definingBlocks);
				}
				definingBlocks.add(b);
			}
		}
		return rval;
	}

	protected Map<Variable, Set<Statement>> computeDefiningStatements(Method m) {
		Map<Variable, Set<Statement>> rval = new HashMap<Variable, Set<Statement>>();

		for (Map.Entry<Variable, Set<CfgBlock>> entry : computeDefiningBlocks(m).entrySet()) {
			Variable v = entry.getKey();
			Set<Statement> set = new HashSet<Statement>();
			for (CfgBlock b : entry.getValue()) {
				for (Statement s : b.getStatements()) {
					if (s.getDefVariables().contains(v)) {
						set.add(s);
					}
				}
			}
			rval.put(v, set);
		}
		return rval;
	}

	protected Map<Variable, Set<Statement>> computeUsingStatements(Method m) {
		Map<Variable, Set<Statement>> rval = new HashMap<Variable, Set<Statement>>();

		for (Map.Entry<Variable, Set<CfgBlock>> entry : computeUsingBlocks(m).entrySet()) {
			Variable v = entry.getKey();
			Set<Statement> set = new HashSet<Statement>();
			for (CfgBlock b : entry.getValue()) {
				for (Statement s : b.getStatements()) {
					if (s.getUseVariables().contains(v)) {
						set.add(s);
					}
				}
			}
			rval.put(v, set);
		}
		return rval;
	}

	// Really simple for now: Just get all blocks that define each variable.
	// Don't worry too much about
	// dominators, etc
	// TODO worry about dominators etc
	protected Map<Variable, Set<CfgBlock>> computeUsingBlocks(Method m) {
		Map<Variable, Set<CfgBlock>> rval = new HashMap<Variable, Set<CfgBlock>>();
		for (CfgBlock b : m.vertexSet()) {
			for (Variable v : b.getUseVariables()) {
				Set<CfgBlock> usingBlocks = rval.get(v);
				if (usingBlocks == null) {
					usingBlocks = new HashSet<CfgBlock>();
					rval.put(v, usingBlocks);
				}
				usingBlocks.add(b);
			}
		}
		return rval;
	}

	/**
	 * compute the set of statements that either use or def a variable
	 * @param m
	 * @return
	 */
	protected Map<Variable, Set<Statement>> computeStatementsContainingVariable(Method m) {
		Map<Variable, Set<Statement>> rval = new HashMap<Variable, Set<Statement>>();
		rval.putAll(computeDefiningStatements(m));
		for (Entry<Variable, Set<Statement>> entry : computeUsingStatements(m).entrySet()) {
			if (!rval.containsKey(entry.getKey())) {
				rval.put(entry.getKey(), entry.getValue());
			} else {
				rval.get(entry.getKey()).addAll(entry.getValue());
			}
		}
		return rval;
	}

	/**
	 * compute the set of blocks that either use or def a variable
	 * @param m
	 * @return
	 */
	protected Map<Variable, Set<CfgBlock>> computeBlocksContainingVariable(Method m) {
		Map<Variable, Set<CfgBlock>> rval = new HashMap<Variable, Set<CfgBlock>>();
		rval.putAll(computeDefiningBlocks(m));
		for (Entry<Variable, Set<CfgBlock>> entry : computeUsingBlocks(m).entrySet()) {
			if (!rval.containsKey(entry.getKey())) {
				rval.put(entry.getKey(), entry.getValue());
			} else {
				rval.get(entry.getKey()).addAll(entry.getValue());
			}
		}
		return rval;
	}	
}
