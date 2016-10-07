package soottocfg.cfg.optimization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.UnreachableNodeRemover;
import soottocfg.util.SetOperations;

public class DeadCodeElimination extends CfgUpdater {
	// Given a method, eliminate the dead code in it
	public DeadCodeElimination() {
	}

	private LiveVars<CfgBlock> blockLiveVars;

	@Override
	public boolean updateMethod(Method m) {
		currentMethod = m;
		blockLiveVars = currentMethod.computeBlockLiveVariables();
		changed = false;
		for (CfgBlock block : currentMethod.vertexSet()) {
			processCfgBlock(block);
		}
		UnreachableNodeRemover<CfgBlock, CfgEdge> remover = new UnreachableNodeRemover<CfgBlock, CfgEdge>(
				currentMethod);
		remover.pruneUnreachableNodes(currentMethod.getSource());
		blockLiveVars = null;
		return changed;
	}

	protected boolean isDead(Statement stmt, LiveVars<Statement> liveVars) {
		if (!(stmt instanceof AssignStatement)) {
			// only assignments can be dead.
			return false;
		}
		// If a statement writes to only variables that are not live, we can
		// remove it!
		// I.e. if intersection s.lvals, s.live is empty
		return SetOperations.intersect(stmt.getDefVariables(), liveVars.liveOut.get(stmt)).isEmpty();
	}

	protected void processCfgBlock(CfgBlock block) {
		Preconditions.checkNotNull(currentMethod);
		setCurrentCfgBlock(block);
		List<Statement> rval = new LinkedList<Statement>();
		LiveVars<Statement> stmtLiveVars = block.computeLiveVariables(blockLiveVars);
		for (Statement s : block.getStatements()) {
			if (isDead(s, stmtLiveVars)) {
				// If the statements is dead, just remove it from the list
				changed = true;
			} else {
				// otherwise, it stays in the list
				rval.add(processStatement(s));
			}
		}
		block.setStatements(rval);

		// Now, check if any of the graph itself is dead
		// We can't remove successors as we are iterating over them, so instead
		// I'll keep a set of
		// blocks to remove, then take them out at the end.
		Set<CfgEdge> toRemove = new HashSet<CfgEdge>();

		for (CfgEdge edge : currentMethod.outgoingEdgesOf(block)) {
			if (edge.getLabel().isPresent()) {
				Optional<Object> res = ExpressionEvaluator.eval(edge.getLabel().get());
				if (res.isPresent() && !(Boolean)res.get()) {
//				if (edge.getLabel().get().equals(BooleanLiteral.falseLiteral())) {
					toRemove.add(edge);
				} else {
					// TODO?
				}
			}
		}
		if (!toRemove.isEmpty()) {
			currentMethod.removeAllEdges(toRemove);
			changed = true;
		}

		setCurrentCfgBlock(null);
	}
}
