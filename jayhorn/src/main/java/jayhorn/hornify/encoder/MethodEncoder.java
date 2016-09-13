package jayhorn.hornify.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jayhorn.Log;
import jayhorn.hornify.ClassTypeEnumerator;
import jayhorn.hornify.HornHelper;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.MethodContract;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.LiveVars;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.Variable;

public class MethodEncoder {

	private final Method method;
	private final HornPredicate precondition, postcondition;

	private final Prover p;

	private final Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();
	private final List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

	private final ExpressionEncoder expEnc;

	public MethodEncoder(Prover p, Method method, ClassTypeEnumerator classEnumerator) {
		this.p = p;
		this.method = method;

		MethodContract mc = HornHelper.hh().getMethodContract(method.getMethodName());
		this.precondition = mc.precondition;
		this.postcondition = mc.postcondition;		
		this.expEnc = new ExpressionEncoder(p, classEnumerator);
	}

	/**
	 * Encodes a method into a set of Horn clauses.
	 */
	public List<ProverHornClause> encode() {
		this.clauses.clear();
		LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();
		makeBlockPredicates(liveVariables);

		if (method.getSource() == null) {
			encodeEmptyMethod();
			return clauses;
		}
		
		final List<ProverExpr> preVars = makeEntryPredicate();
		blocksToHorn(liveVariables, preVars);
		return clauses;
	}

	
	/**
	 * Creates a trivial Horn clause:
	 * pre(x,y,z) -> post(x,y,z)
	 * for methods that do not have a body.
	 */
	private void encodeEmptyMethod() {
		Log.debug("No implementation available for " + method.getMethodName());
		final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
		final List<ProverExpr> entryVars = HornHelper.hh().findOrCreateProverVar(p, precondition.variables, varMap);
		final List<ProverExpr> exitVars = HornHelper.hh().findOrCreateProverVar(p, postcondition.variables, varMap);
		final ProverExpr entryAtom = precondition.predicate.mkExpr(entryVars.toArray(new ProverExpr[entryVars.size()]));
		final ProverExpr exitAtom = postcondition.predicate.mkExpr(exitVars.toArray(new ProverExpr[exitVars.size()]));
		clauses.add(p.mkHornClause(exitAtom, new ProverExpr[] { entryAtom }, p.mkLiteral(true)));	
	}
	
	/**
	 * Creates a Horn clause that makes from the method precondition to 
	 * the first block of the message body.
	 * This method precondition args comprise all method parameters and 
	 * other variables that should be visible to the method. The args of
	 * the predicate of the first block (entryVars) contain all local vars.
	 * 
	 * @return
	 */
	private List<ProverExpr> makeEntryPredicate() {
		// add an entry clause connecting with the precondition
		Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
		final List<ProverExpr> preVars = HornHelper.hh().findOrCreateProverVar(p, precondition.variables, varMap);
		final HornPredicate entryPred = blockPredicates.get(method.getSource());
		final List<ProverExpr> entryVars = HornHelper.hh().findOrCreateProverVar(p, entryPred.variables, varMap);
		final ProverExpr preAtom = precondition.predicate.mkExpr(preVars.toArray(new ProverExpr[0]));
		final List<ProverExpr> allEntryArgs = new ArrayList<ProverExpr>();
		allEntryArgs.addAll(preVars);
		allEntryArgs.addAll(entryVars);
		final ProverExpr entryAtom = entryPred.predicate.mkExpr(allEntryArgs.toArray(new ProverExpr[0]));
		clauses.add(p.mkHornClause(entryAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		return preVars;
	}
	
	/**
	 * Creates one HornPredicate for each block. The predicate contains the
	 * list of live variables
	 * for that block sorted by names and a predicate over the types of
	 * these variables that has
	 * the same name as the block.
	 * 
	 * Note that the ProverFun in the predicate also contains all variables
	 * in precondition.variables. So the arity of the prover fun is
	 * |precondition.variables| + |sortedVars|
	 * 
	 * @param p
	 * @param method
	 */
	private void makeBlockPredicates(LiveVars<CfgBlock> liveVariables) {
		for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.liveIn.entrySet()) {
			Set<Variable> allLive = new HashSet<Variable>();
			allLive.addAll(entry.getValue());
			// sort the list of variables by name to make access
			// and reading easier.
			List<Variable> sortedVars = HornHelper.hh().setToSortedList(allLive);
			String name = entry.getKey().getLabel();
			ProverFun pred = freshHornPredicate(name, sortedVars);
			blockPredicates.put(entry.getKey(), new HornPredicate(name, sortedVars, pred));
		}
	}
	
	private ProverFun freshHornPredicate(String name, List<Variable> sortedVars) {
		final List<Variable> allArgs = new LinkedList<Variable>();
		// add types for the method arguments, which
		// are later needed for the post-conditions
		allArgs.addAll(precondition.variables);
		allArgs.addAll(sortedVars);
		return HornHelper.hh().genHornPredicate(p, method.getMethodName() + "_" + name, allArgs);
	}


	
	/**
	 * Creates Horn clauses for all CfgBlocks in a method.
	 * @param liveVariables
	 * @param preVars
	 */
	private void blocksToHorn(LiveVars<CfgBlock> liveVariables, List<ProverExpr> preVars) {
		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		todo.add(method.getSource());
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		// translate reachable blocks
		while (!todo.isEmpty()) {
			CfgBlock current = todo.remove(0);
			done.add(current);
			/*
			 * Translate the body of the CfgBlock using blockToHorn.
			 * This gives us the exitPred which is the last predicate
			 * used in this basic block.
			 */
			final HornPredicate exitPred = blockToHorn(current, liveVariables.liveOut.get(current), preVars);
			//reset the varMap here. 
			Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
			final List<ProverExpr> exitVars = HornHelper.hh().findOrCreateProverVar(p, exitPred.variables, varMap);

			/*
			 * Now distinguish two cases: 
			 * THEN-CASE: Our block has no successors and leaves the method.
			 *   In this case, we have to connect exitPred to the predicate
			 *   associated with the postcondition of the method. That is,
			 *   we generate a Horn clause of the form
			 *   exitPred(...) -> postPred(...)
			 * ELSE-CASE: Our block has at least one successor. In this case,
			 *   we have to create a clause that connects exitPred with the
			 *   predicate associated with the entry into the next block: succPred.
			 *   And we have to add the next block to the todo list if we haven't
			 *   processed it already.
			 */
			if (method.outgoingEdgesOf(current).isEmpty()) {
				// block ends with a return

				final List<ProverExpr> postVars = HornHelper.hh().findOrCreateProverVar(p, postcondition.variables,
						varMap);
				final ProverExpr postAtom = postcondition.predicate
						.mkExpr(postVars.toArray(new ProverExpr[postVars.size()]));

				final List<ProverExpr> allExitArgs = new ArrayList<ProverExpr>();
				allExitArgs.addAll(postVars.subList(0, precondition.variables.size()));
				allExitArgs.addAll(exitVars);

				final ProverExpr exitAtom = exitPred.predicate
						.mkExpr(allExitArgs.toArray(new ProverExpr[allExitArgs.size()]));

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { exitAtom }, p.mkLiteral(true)));
			} else {
				// link to the successor blocks
				final ProverExpr exitAtom = instPredicate(exitPred, exitVars, preVars);
				for (CfgEdge edge : method.outgoingEdgesOf(current)) {
					CfgBlock succ = method.getEdgeTarget(edge);
					if (!todo.contains(succ) && !done.contains(succ)) {
						todo.add(succ);
					}
					final ProverExpr exitCondExpr;
					if (edge.getLabel().isPresent()) {
						exitCondExpr = expEnc.exprToProverExpr(edge.getLabel().get(), varMap);
					} else {
						exitCondExpr = p.mkLiteral(true);
					}
					final HornPredicate succPred = blockPredicates.get(succ);
					final ProverExpr succAtom = instPredicate(succPred,
							HornHelper.hh().findOrCreateProverVar(p, succPred.variables, varMap), preVars);
					clauses.add(p.mkHornClause(succAtom, new ProverExpr[] { exitAtom }, exitCondExpr));
				}
			}
		}	
	}
	
	
	private ProverExpr instPredicate(HornPredicate pred, List<ProverExpr> args, List<ProverExpr> methodPreExprs) {
		List<ProverExpr> allArgs = new ArrayList<ProverExpr>();
		allArgs.addAll(methodPreExprs);
		allArgs.addAll(args);
		return pred.predicate.mkExpr(allArgs.toArray(new ProverExpr[allArgs.size()]));
	}

	
	/**
	 * Creates the Horn clauses for the statements in a single block.
	 * @param block The block that is to be translated.
	 * @param liveOutVars The set of variables that are live after the block.
	 * @param methodPreExprs
	 * @return 
	 */
	private HornPredicate blockToHorn(CfgBlock block, Set<Variable> liveOutVars,
			List<ProverExpr> methodPreExprs) {
		//get the predicate that is associated with the entry of the block.
		final HornPredicate initPred = blockPredicates.get(block);

		if (block.getStatements().isEmpty()) {
			return initPred;
		}
		
		Map<Statement, Set<Variable>> liveAfter = computeLiveAfterVariables(block, liveOutVars);

		final String initName = initPred.name;
		HornPredicate prePred = initPred;
		int counter = 0;

		for (Statement s : block.getStatements()) {
			final String postName = initName + "_" + (++counter);
			final List<Variable> interVarList = HornHelper.hh().setToSortedList(liveAfter.get(s));
			final HornPredicate postPred = new HornPredicate(postName, interVarList,
					freshHornPredicate(postName, interVarList));

			StatementEncoder senc = new StatementEncoder(p, methodPreExprs, this.expEnc);
			this.clauses.addAll(senc.statementToClause(s, prePred, postPred));

			prePred = postPred;
		}

		return prePred;
	}

	/**
	 * Compute for each statement the set of variables
	 * that are live after the statement.
	 * @param block The current CfgBlock.
	 * @param liveOutVars The set of vars that are live after block.
	 * @return A map that stores for each statement the 
	 * set of variables that are live after the execution
	 * of the statement.
	 */
	private Map<Statement, Set<Variable>> computeLiveAfterVariables(CfgBlock block, Set<Variable> liveOutVars) {

		Map<Statement, Set<Variable>> liveMap = new HashMap<Statement, Set<Variable>>();

		@SuppressWarnings("unchecked")
		final Set<Variable>[] interVars = new Set[block.getStatements().size()];
		interVars[interVars.length - 1] = new HashSet<Variable>();
		interVars[interVars.length - 1].addAll(liveOutVars);

		// add variables used in the outgoing guards, and the
		// method arguments
		for (CfgEdge edge : method.outgoingEdgesOf(block))
			if (edge.getLabel().isPresent())
				interVars[interVars.length - 1].addAll(edge.getLabel().get().getUseVariables());

		for (int i = interVars.length - 1; i > 0; --i) {
			final Statement s = block.getStatements().get(i);
			interVars[i - 1] = new HashSet<Variable>();
			interVars[i - 1].addAll(interVars[i]);
			interVars[i - 1].removeAll(s.getDefVariables());
			interVars[i - 1].addAll(s.getUseVariables());
			liveMap.put(s, interVars[i]);
		}			
		liveMap.put(block.getStatements().get(0), interVars[0]);
		return liveMap;
	}
	
}
