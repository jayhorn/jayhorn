/**
 * 
 */
package soottocfg.cfg.optimization;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.transformers.ArrayTransformer;

/**
 * @author schaef
 *
 */
public class CfgCallInliner {

    /**
     * Verify that a program does not contain identical statement
     * objects multiple times
     */
    public static void noDuplicateStatements(Program program) {
        for (Method method : program.getMethods())
            noDuplicateStatements(method);
    }

    /**
     * Verify that a method does not contain identical statement
     * objects multiple times
     */
    public static void noDuplicateStatements(Method method) {
        Set<Statement> seen = new HashSet<> ();
        for (CfgBlock b : method.vertexSet()) {
            for (Statement s : b.getStatements()) {
                if (!seen.add(s))
                    Verify.verify(false, "duplicate statement in method " + method.getMethodName() + ": " + s);
            }
        }
    }
    
	private int freshInt = 0;

	/*
	 * Note that these guys have to go from String->Integer, because
	 * we have to use the method name since the hashcode of the method
	 * changes while we inline.
	 */
	Map<String, Integer> totalStmts = new HashMap<String, Integer>();
	Map<String, Integer> totalCallsTo = new HashMap<String, Integer>();
	Set<String> alreadyInlined = new HashSet<String>();
	final Program program;

	/**
	 * 
	 */
	public CfgCallInliner(Program p) {
		this.program = p;
		computeStats(p);
	}

	private void computeStats(Program p) {
		// Map<String, Integer> outgoingCalls = new HashMap<String, Integer>();
		for (Method m : p.getMethods()) {
			if (!totalCallsTo.containsKey(m.getMethodName())) {
				totalCallsTo.put(m.getMethodName(), 0);
			}

			for (Method callee : calledMethods(m)) {
				if (!totalCallsTo.containsKey(callee.getMethodName())) {
					totalCallsTo.put(callee.getMethodName(), 0);
				}
				totalCallsTo.put(callee.getMethodName(), totalCallsTo.get(callee.getMethodName()) + 1);
			}

			int stmtCount = 0;
			for (CfgBlock b : m.vertexSet())
				stmtCount += b.getStatements().size();
			totalStmts.put(m.getMethodName(), stmtCount);
		}
	}

	private List<Method> calledMethods(Method m) {
		List<Method> res = new LinkedList<Method>();
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				if (s instanceof CallStatement) {
					CallStatement cs = (CallStatement) s;
					res.add(cs.getCallTarget());
				}
			}
		}
		return res;
	}

	private Set<Method> reachableMethod(Method main) {
		Set<Method> reachable = new HashSet<Method>();
		List<Method> todo = new LinkedList<Method>();
		todo.add(main);
		while (!todo.isEmpty()) {
			Method m = todo.remove(0);
			reachable.add(m);
			for (Method n : calledMethods(m)) {
				if (!reachable.contains(n) && !todo.contains(n)) {
					todo.add(n);
				}
			}
		}
		return reachable;
	}

	public void inlineFromMain(int maxSize, int maxOccurences) {
		if (maxSize <= 0 && maxOccurences <= 0) {
			return;
		}
		Method mainMethod = program.getEntryPoint();
		inlineCalls(mainMethod, maxSize, maxOccurences);
		FoldStraighLineSeq folder = new FoldStraighLineSeq();
		folder.fold(mainMethod);

		Set<Method> reachable = reachableMethod(mainMethod);
		Set<Method> toRemove = new HashSet<Method>();
		for (Method m : program.getMethods()) {
			if (!reachable.contains(m)) {
				toRemove.add(m);
			}
		}
		program.removeMethods(toRemove);

		// System.err.println(program);
	}

	private boolean canBeInlined(Method caller, Method callee) {
		if (callee.isStub()) {
                    // stubs indicate cases of over-approximation,
                    // which we need to recognise also later
                    // TODO: flag over-approximation in a more efficient way
                    return false;
		}
		boolean res = !callee.equals(caller) && !callee.isConstructor();

		if (soottocfg.Options.v().arrayInv() &&
				callee.getThisVariable() != null
				&& ((ReferenceType) callee.getThisVariable().getType()).getClassVariable().getName().startsWith(ArrayTransformer.arrayTypeName)) {
			//TODO: for Rody's array model we must not inline array stuff.
			return false;
		}
		return res;
	}

	private void inlineCalls(Method method, int maxSize, int maxOccurences) {
		if (alreadyInlined.contains(method.getMethodName())) {
			return;
		}
		alreadyInlined.add(method.getMethodName());
		enforceSingleInlineableCallPerBlock(method, maxSize, maxOccurences);
		List<CfgBlock> toRemove = new LinkedList<CfgBlock>();
		for (CfgBlock b : new HashSet<CfgBlock>(method.vertexSet())) {
			for (Statement s : new LinkedList<Statement>(b.getStatements())) {
				if (s instanceof CallStatement) {
					CallStatement cs = (CallStatement) s;
					Method callee = cs.getCallTarget();

					// first apply inlining to the callee
					inlineCalls(callee, maxSize, maxOccurences);

					if (canBeInlined(method, callee)) {

						if (totalCallsTo.get(callee.getMethodName()) < maxOccurences
								|| totalStmts.get(callee.getMethodName()) < maxSize) {

							// now copy the callee into the caller.
							copyCalleeBody(method, b, cs);
							toRemove.add(b);
							/*
							 * This is a bit hacky: we have to continue because
							 * copyCalleeBody deletes the current block.
							 * However,
							 * we know from enforceSingleInlineableCallPerBlock
							 * that there is only one call per block to inline.
							 */
							// break;
						}
					}
				}
			}
		}
		method.removeAllVertices(toRemove);

	}

	/**
	 * Before inlining, we enforce that each block has at most one
	 * call statement that can be inlined.
	 * 
	 * @param method
	 * @param maxSize
	 * @param maxOccurences
	 */
	private void enforceSingleInlineableCallPerBlock(Method method, int maxSize, int maxOccurences) {
		for (CfgBlock b : new HashSet<CfgBlock>(method.vertexSet())) {
			splitBlockIfNecessary(method, b, maxSize, maxOccurences);
		}
	}

	/**
	 * Splits blocks that contain more than one
	 * inlineable call statement.
	 * 
	 * @param m
	 * @param b
	 * @param maxSize
	 * @param maxOccurences
	 */
	private void splitBlockIfNecessary(Method m, CfgBlock b, int maxSize, int maxOccurences) {
		int inlineableCalls = 0;
		for (Statement s : new LinkedList<Statement>(b.getStatements())) {
			if (s instanceof CallStatement) {
				CallStatement cs = (CallStatement) s;
				Method callee = cs.getCallTarget();
				if (!callee.equals(m) && !callee.isConstructor()) {
					if (totalCallsTo.get(callee.getMethodName()) < maxOccurences
							|| totalStmts.get(callee.getMethodName()) < maxSize) {
						inlineableCalls++;
					}
				}
				if (inlineableCalls > 1) {
					int idx = b.getStatements().indexOf(s);
					// split the block.
					List<Statement> rest = new LinkedList<Statement>(
							b.getStatements().subList(idx, b.getStatements().size()));
					b.removeStatements(rest);
					CfgBlock nextBlock = new CfgBlock(m);
					nextBlock.getStatements().addAll(rest);

					for (CfgBlock suc : Graphs.successorListOf(m, b)) {
						CfgEdge newEdge = new CfgEdge();
						CfgEdge oldEdge = m.getEdge(b, suc);
						if (oldEdge.getLabel().isPresent()) {
							newEdge.setLabel(oldEdge.getLabel().get());
						}
						m.removeEdge(b, suc);
						m.addEdge(nextBlock, suc, newEdge);
					}
					m.addEdge(b, nextBlock);
					splitBlockIfNecessary(m, nextBlock, maxSize, maxOccurences);
					return;
				}
			}
		}
	}

	/**
	 * Copies the body of callee into caller at the
	 * position of call.
	 * 
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
		if (callIdx + 1 < block.getStatements().size()) {
			postBlock.getStatements().addAll(block.getStatements().subList(callIdx + 1, block.getStatements().size()));
		}
		for (CfgBlock pre : new LinkedList<CfgBlock>(Graphs.predecessorListOf(caller, block))) {
			// copy the label over as well
			CfgEdge newEdge = new CfgEdge();
			CfgEdge oldEdge = caller.getEdge(pre, block);
			if (oldEdge.getLabel().isPresent()) {
				newEdge.setLabel(oldEdge.getLabel().get());
			}
			caller.removeEdge(pre, block);
			caller.addEdge(pre, preBlock, newEdge);
		}
		for (CfgBlock post : new LinkedList<CfgBlock>(Graphs.successorListOf(caller, block))) {
			CfgEdge newEdge = new CfgEdge();
			CfgEdge oldEdge = caller.getEdge(block, post);
			if (oldEdge.getLabel().isPresent()) {
				newEdge.setLabel(oldEdge.getLabel().get());
			}
			caller.removeEdge(block, post);
			caller.addEdge(postBlock, post, newEdge);
		}
		Verify.verify(caller.outDegreeOf(block) + caller.inDegreeOf(block) == 0);
		if (caller.getSource().equals(block)) {
			caller.setSource(preBlock);
		} else if (caller.getSink().equals(block)) {
			caller.setSink(postBlock);
		}
		// don't remove yet. Otherwise the numbering of the blocks
		// gets all messed up. remove in inlineCalls

		Method callee = call.getCallTarget();
		Verify.verifyNotNull(callee.getSource());
		/*
		 * Create a map from callee locals and formals to fresh caller locals.
		 */
		Map<Variable, Variable> varSubstitionMap = new HashMap<Variable, Variable>();
		List<Variable> toCopy = new LinkedList<Variable>();
		toCopy.addAll(callee.getInParams());
		toCopy.addAll(callee.getOutParams());
		toCopy.addAll(callee.getLocals());

		for (Variable v : toCopy) {
			Variable local = new Variable("cp_" + v.getName() + "_" + (++freshInt), v.getType());
			caller.addLocalVariable(local);
			varSubstitionMap.put(v, local);
		}
		SourceLocation loc = call.getSourceLocation();
		for (int i = 0; i < callee.getInParams().size(); i++) {
			Variable v = callee.getInParam(i);
			preBlock.addStatement(
					new AssignStatement(loc, varSubstitionMap.get(v).mkExp(loc), call.getArguments().get(i)));
		}
		/*
		 * Add call reachable blocks from the callee to the caller
		 */
		Map<CfgBlock, CfgBlock> cloneMap = new HashMap<CfgBlock, CfgBlock>();

		for (CfgBlock cur : callee.vertexSet()) {
			CfgBlock clone = new CfgBlock(caller);
			for (Statement s : cur.getStatements()) {
                            Statement s2 = s.substitute(varSubstitionMap);
                            // we need to make sure that we get a new object, otherwise
                            // the InterProceduralPullPushOrdering will get confused
                            if (s2 == s)
                                s2 = s2.deepCopy();
                            clone.addStatement(s2);
			}
			cloneMap.put(cur, clone);
		}
		/*
		 * Add all edges for the copied blocks.
		 */
		for (CfgEdge edge : callee.edgeSet()) {
			CfgBlock src = callee.getEdgeSource(edge);
			CfgBlock tgt = callee.getEdgeTarget(edge);
			if (cloneMap.containsKey(src) && cloneMap.containsKey(tgt)) {
				CfgEdge newEdge = new CfgEdge();
				if (edge.getLabel().isPresent()) {
					newEdge.setLabel(edge.getLabel().get().substitute(varSubstitionMap));
					// newEdge.setLabel(edge.getLabel().get());
				}
				caller.addEdge(cloneMap.get(src), cloneMap.get(tgt), newEdge);
			}
		}
		/*
		 * Connect the copies blocks with the caller.
		 */
		caller.addEdge(preBlock, cloneMap.get(callee.getSource()));
		if (cloneMap.containsKey(callee.getSink())) {
			// if callee loops forever, this might not be reached.
			caller.addEdge(cloneMap.get(callee.getSink()), postBlock);
		}

		/*
		 * Now update the out variables
		 */

		for (int i = 0; i < callee.getOutParams().size(); i++) {
			Expression receiver;
			if (i < call.getReceiver().size()) {
				receiver = call.getReceiver().get(i);
				postBlock.addStatement(0, new AssignStatement(loc, receiver,
						varSubstitionMap.get(callee.getOutParams().get(i)).mkExp(loc)));
			} else {
				System.err.println("More outparams than receivers " + call);
			}
		}

	}

}
