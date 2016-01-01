/**
 * 
 */
package jayhorn.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graphs;
import org.jgrapht.alg.CycleDetector;

import com.google.common.base.Optional;
import com.google.common.base.Verify;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.LoopFinder;

/**
 * @author schaef
 *
 */
public class LoopRemoval {

	private final Method method;
	private int havocCounter = 0;

	/**
	 * 
	 */
	public LoopRemoval(Method m) {
		method = m;
	}

	/**
	 * Removes all loops in the current method as follows:
	 * - Identify all variables modified in the loop body and
	 *   add a non-deterministic assignment to them to the entry
	 *   of the loop. 
	 * - Create a copy X of the loop header and remove all outgoing
	 *   edges of X that lead into the loop body. 
	 * - Redirect all back-edges of a loop to the copy of the loop
	 *   header. 
	 */
	public void removeLoops() {
		Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, method.getSource());
		LoopFinder<CfgBlock> loopFinder = new LoopFinder<CfgBlock>(dom);
		TreeSet<CfgBlock> lnt = loopFinder.getLoopNestTreeSet();
		Map<CfgBlock, Set<CfgBlock>> loops = loopFinder.getLoops();
		
		while (!lnt.isEmpty()) {
			CfgBlock header = lnt.pollFirst();
			removeLoop(header, loops.get(header), loops);
		}
	}

	/**
	 * Remove a loop by inserting non-deterministic assignments for all variables modified
	 * in the loop body to each loop entry (successors of the header inside the loop), and
	 * by redirecting the back edges to the non-looping successors of the header. If there
	 * are no non-looping successors, just remove the back edges. 
	 * @param header
	 * @param body
	 * @param loops The mapping from header to loop body for all loops. This will be updated 
	 * 			if we introduce new nodes during the loop removal.
	 */
	private void removeLoop(CfgBlock header, Set<CfgBlock> body, Map<CfgBlock, Set<CfgBlock>> loops) {
		Set<CfgBlock> successorsOfHeader = new HashSet<CfgBlock>(Graphs.successorListOf(method, header));
		Set<CfgBlock> entryBlocks = new HashSet<CfgBlock>(successorsOfHeader);
		entryBlocks.retainAll(body);
		addNonDetAssignmentsToBody(header, body, entryBlocks);
		// find the successor of the header that does not enter the loop.
		// assert that there is at most one such block.
		Set<CfgBlock> headerExitBlocks = new HashSet<CfgBlock>(successorsOfHeader);
		headerExitBlocks.removeAll(entryBlocks);
		Verify.verify(headerExitBlocks.size() <= 1,
				"Bad loop: header has more than one successor that does not enter the loop.");
		Optional<CfgBlock> headerExitBlock = Optional.absent();
		if (headerExitBlocks.size()==1) {
			headerExitBlock = Optional.of(headerExitBlocks.iterator().next());
		}
		
		CfgBlock headerClone = null;
		
		for (CfgBlock b : new HashSet<CfgBlock>(body)) {
			if (method.containsEdge(b, header)) {
				method.removeEdge(method.getEdge(b, header));
				if (headerExitBlock.isPresent()) {
					if (headerClone==null) {
						headerClone = header.deepCopy();
						addBlockToLoops(header, headerClone, loops);
						method.addEdge(headerClone, headerExitBlock.get());
					}
					method.addEdge(b, headerClone);
				}
			}
		}
	}

	/**
	 * Update the 'loops' map by adding the block 'newBlock' to all loops that
	 * contain 'header'.
	 * @param header
	 * @param newBlock
	 * @param loops
	 */
	private void addBlockToLoops(CfgBlock header, CfgBlock newBlock, Map<CfgBlock, Set<CfgBlock>> loops) {
		for (Entry<CfgBlock, Set<CfgBlock>> entry : loops.entrySet()) {
			if (entry.getValue().contains(header)) {
				entry.getValue().add(newBlock);
			}
		}
	}
	
	/**
	 * For each loop entry, add statements that assign a fresh local to all variables
	 * that are modified within the loop body.
	 * @param header
	 * @param body
	 * @param entryBlocks
	 */
	private void addNonDetAssignmentsToBody(CfgBlock header, Set<CfgBlock> body, Set<CfgBlock> entryBlocks) {
		Set<Variable> modifiedVariables = new HashSet<Variable>();
		for (CfgBlock b : body) {
			modifiedVariables.addAll(b.getDefVariables());
		}
		Map<Variable, Variable> havocVariables = new HashMap<Variable, Variable>();
		for (Variable v : modifiedVariables) {
			Variable havocVar = new Variable("$havoc" + (havocCounter++), v.getType());
			havocVariables.put(v, havocVar);
			method.getLocals().add(havocVar);
		}
		SourceLocation loc = null;
		for (CfgBlock b : entryBlocks) {
			for (Variable v : modifiedVariables) {
				AssignStatement asgn = new AssignStatement(loc, new IdentifierExpression(loc, v),
						new IdentifierExpression(loc, havocVariables.get(v)));
				b.getStatements().add(0, asgn);
			}
		}
	}

	/**
	 * Debug method to ensure that the loop elimination actually removed all loops.
	 * Throws a VerifyException if the current method still has loops.
	 */
	public void verifyLoopFree() {
		Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, method.getSource());
		LoopFinder<CfgBlock> loopFinder = new LoopFinder<CfgBlock>(dom);		
		Verify.verify(loopFinder.getLoopNestTreeSet().isEmpty());
		
		CycleDetector<CfgBlock, CfgEdge> cycles = new CycleDetector<CfgBlock, CfgEdge>(method);
		Verify.verify(cycles.findCycles().isEmpty());
	}

}
