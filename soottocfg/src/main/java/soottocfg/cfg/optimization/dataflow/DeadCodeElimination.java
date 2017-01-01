package soottocfg.cfg.optimization.dataflow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.optimization.ExpressionEvaluator;
import soottocfg.cfg.optimization.UnreachableNodeRemover;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.util.SetOperations;

public class DeadCodeElimination  {
	// Given a method, eliminate the dead code in it
	public DeadCodeElimination() {
	}

	public static boolean eliminateDeadCode(Method m) {		
		LiveVars<CfgBlock> blockLiveVars = m.computeBlockLiveVariables();
		boolean changed = false;
		for (CfgBlock block : m.vertexSet()) {
			changed = changed 
					|| eliminateDeadStatements(m, block, blockLiveVars)
					|| eliminateDeadConditions(m, block);
		}
		UnreachableNodeRemover.pruneUnreachableNodes(m, m.getSource());
		return changed;
	}

	protected static boolean isDead(Statement stmt, LiveVars<Statement> liveVars) {
		if (!(stmt instanceof AssignStatement) && !(stmt instanceof PullStatement)) {
			// only assignments and pulls can be dead.
			return false;
		}
		// If a statement writes to only variables that are not live, we can
		// remove it!
		// I.e. if intersection s.lvals, s.live is empty
		return SetOperations.intersect(stmt.getDefVariables(), liveVars.liveOut.get(stmt)).isEmpty();
	}

	protected static boolean eliminateDeadStatements(Method method, CfgBlock block, LiveVars<CfgBlock> blockLiveVars) {
		boolean changed = false;
		List<Statement> rval = new LinkedList<Statement>();
		LiveVars<Statement> stmtLiveVars = block.computeLiveVariables(blockLiveVars);
		for (Statement s : block.getStatements()) {
			if (isDead(s, stmtLiveVars)) {
				// If the statements is dead, just remove it from the list
				changed = true;
			} else {
				// otherwise, it stays in the list
				rval.add(s.deepCopy());
			}
		}
		if (changed) {
			//only replace statements if something changed.
			block.setStatements(rval);
		}
		return changed;
	}

	/**
	 * Simplify the expressions on the edge labels. If an expression simplifies
	 * to True, we can remove the lable. If it simplifies to false, we can remove
	 * the edge.
	 * @param method
	 * @param block
	 * @return
	 */
	protected static boolean eliminateDeadConditions(Method method, CfgBlock block) {
		boolean changed = false;
		Set<CfgEdge> toRemove = new HashSet<CfgEdge>();

		for (CfgEdge edge : method.outgoingEdgesOf(block)) {
			if (edge.getLabel().isPresent()) {
				Expression simpleLabel = ExpressionEvaluator.simplify(edge.getLabel().get());
				if (simpleLabel instanceof BooleanLiteral) {
					if (((BooleanLiteral)simpleLabel).getValue()) {
						//then we can remove the label.
						edge.removeLabel();
					} else {
						//then we can remove the edge.
						toRemove.add(edge);
					}
				} else {
					//TODO : do not set the label for now because it causes
					//princess to fail during type checking.
//					edge.setLabel(simpleLabel);
				}
//				
//				Optional<Object> res = ExpressionEvaluator.eval(edge.getLabel().get());
//				if (res.isPresent()) {
//					if (!(Boolean)res.get()) {
//						// condition false, remove edge
//						toRemove.add(edge);
//					} else {
//						// condition true, remove all other edges?
//					}
//				} else {
//					// TODO?
//				}
			}
		}
				
		if (!toRemove.isEmpty()) {
			method.removeAllEdges(toRemove);
			changed = true;
		}
		return changed;
	}
}
