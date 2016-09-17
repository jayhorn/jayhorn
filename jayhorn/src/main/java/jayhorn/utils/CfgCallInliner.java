/**
 * 
 */
package jayhorn.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class CfgCallInliner {

	Map<Method, Integer> totalStmts = new HashMap<Method, Integer>();
	Map<Method, Integer> totalCallsTo = new HashMap<Method, Integer>();
	Set<Method> alreadyInlined = new HashSet<Method>();
	/**
	 * 
	 */
	public CfgCallInliner(Program p) {
		computeStats(p);
	}

	private void computeStats(Program p) {
		Map<Method, Integer> outgoingCalls = new HashMap<Method, Integer>();
		for (Method m : p.getMethods()) {
			int stmtCount = 0;
			int outgoingCallCounter=0;
			for (CfgBlock b : m.vertexSet()) {
				stmtCount+=b.getStatements().size();
				for (Statement s : b.getStatements()) {
					if (s instanceof CallStatement) {
						CallStatement cs = (CallStatement)s;
						outgoingCallCounter++;
						if (!totalCallsTo.containsKey(cs.getCallTarget())) {
							totalCallsTo.put(cs.getCallTarget(), 0);
						}
						totalCallsTo.put(cs.getCallTarget(), totalCallsTo.get(cs.getCallTarget())+1);
					}
				}
			}
			outgoingCalls.put(m, outgoingCallCounter);
			totalStmts.put(m, stmtCount);
		}		
	}
	
	public void inlineCalls(Method method, int maxSize, int maxOccurences) {
		if (alreadyInlined.contains(method)) {
			return;
		}
		alreadyInlined.add(method);
		enforceSingleInlineableCallPerBlock(method, maxSize, maxOccurences);
		for (CfgBlock b : new HashSet<CfgBlock>(method.vertexSet())) {			
			for (Statement s : new LinkedList<Statement>(b.getStatements())) {
				if (s instanceof CallStatement) {
					CallStatement cs = (CallStatement)s;
					Method callee = cs.getCallTarget();
					if (!callee.equals(method)) {
						if (totalCallsTo.get(callee)<maxOccurences || totalStmts.get(callee)<maxSize) {
							//first apply inlining to the callee
							inlineCalls(callee, maxSize, maxOccurences);
							//now copy the callee into the caller.
							copyCalleeBody(method, b, cs);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Before inlining, we enforce that each block has at most one 
	 * call statement that can be inlined. 
	 * @param method
	 * @param maxSize
	 * @param maxOccurences
	 */
	private void enforceSingleInlineableCallPerBlock(Method method, int maxSize, int maxOccurences) {
		for (CfgBlock b : new HashSet<CfgBlock>(method.vertexSet())) {
			int inlineableCalls =0;
			for (Statement s : new LinkedList<Statement>(b.getStatements())) {
				if (s instanceof CallStatement) {
					CallStatement cs = (CallStatement)s;
					Method callee = cs.getCallTarget();
					if (!callee.equals(method)) {
						if (totalCallsTo.get(callee)<maxOccurences || totalStmts.get(callee)<maxSize) {
							inlineableCalls++;
						}
					}
				}
			}
			Verify.verify(inlineableCalls<=1);
		}		
	}
	
	/**
	 * Copies the body of callee into caller at the 
	 * position of call.
	 * @param caller
	 * @param block
	 * @param call
	 * @param callee
	 */
	private void copyCalleeBody(Method caller, CfgBlock block, CallStatement call) {	
		/*
		 * First remove block and replace it by two blocks
		 * preBlock that contains all statements of block up to the call, and
		 * postBlock that contains all statements of block after the call.
		 */
		int callIdx = block.getStatements().indexOf(call);
		CfgBlock preBlock = new CfgBlock(caller);
		preBlock.getStatements().addAll(block.getStatements().subList(0, callIdx));
		CfgBlock postBlock = new CfgBlock(caller);
		if (callIdx+1<block.getStatements().size()) {
			postBlock.getStatements().addAll(block.getStatements().subList(callIdx+1,0));
		}
		for (CfgBlock pre : new LinkedList<CfgBlock>(Graphs.predecessorListOf(caller, block))) {
			caller.removeEdge(pre, block);
			caller.addEdge(pre, preBlock);
		}
		for (CfgBlock post : new LinkedList<CfgBlock>(Graphs.successorListOf(caller, block))) {
			caller.removeEdge(block, post);
			caller.addEdge(postBlock, post);
		}
		caller.removeVertex(block);
		
		Method callee = call.getCallTarget();		
		Verify.verifyNotNull(callee.getSource());
		/*
		 * Add call reachable blocks from the callee to the caller
		 */
		Map<CfgBlock, CfgBlock> cloneMap = new HashMap<CfgBlock, CfgBlock>();
		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		todo.add(callee.getSource());
		while (!todo.isEmpty()) {
			CfgBlock cur = todo.remove(0);			
			CfgBlock clone = new CfgBlock(caller);
			for (Statement s : cur.getStatements()) {
				clone.addStatement(s.deepCopy());
			}
			cloneMap.put(cur, clone);
			for (CfgBlock next : Graphs.successorListOf(callee, cur)) {
				if (!cloneMap.containsKey(next) && !todo.contains(next)) {
					todo.add(next);
				}
			}
		}
		/* 
		 * Add all edges for the copied blocks.
		 */
		for (CfgEdge edge : callee.edgeSet()) {
			CfgBlock src = callee.getEdgeSource(edge);
			CfgBlock tgt = callee.getEdgeTarget(edge);
			if (cloneMap.containsKey(src) && cloneMap.containsKey(tgt)) {
				caller.addEdge(cloneMap.get(src), cloneMap.get(tgt));
			}
		}
		/*
		 * Connect the copies blocks with the caller.
		 */
		caller.addEdge(preBlock, cloneMap.get(callee.getSource()));
		if (cloneMap.containsKey(callee.getSink())) {
			//if callee loops forever, this might not be reached.
			caller.addEdge(postBlock, cloneMap.get(callee.getSink()));
		}

		/* Now update the variables in the cloned blocks.
		 */
		Map<Variable, Expression> varSubstitionMap = new HashMap<Variable, Expression>();
		for (int i = 0; i<callee.getInParams().size(); i++) {
			varSubstitionMap.put(callee.getInParam(i), call.getArguments().get(i));
		}
		
		//TODO;
	}
	

	
}
