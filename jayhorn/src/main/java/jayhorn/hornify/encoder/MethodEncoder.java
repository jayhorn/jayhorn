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
import soottocfg.cfg.Program;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

public class MethodEncoder {

	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();

	////////////////////////////////////////////////////////////////////////////
	
	private final Method method;
	private final MethodContract methodContract;
	private final Prover p;

	private final Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();
	public final List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

	private final List<Variable> methodPreVariables;
	private final List<ProverExpr> methodPreExprs;

	private final ExpressionEncoder expEnc;
	
	public MethodEncoder(Prover p, Program program, Method method, ClassTypeEnumerator cType) {
		this.p = p;		
		this.method = method;
		this.typeIds = cType.getTypeIds();
		this.methodContract = HornHelper.hh().getMethodContract(method.getMethodName());
		this.methodPreVariables = methodContract.precondition.variables;
		this.expEnc = new ExpressionEncoder(p, program, typeIds);
		
		this.methodPreExprs = new ArrayList<ProverExpr>();
		for (Variable v : methodPreVariables) {
			methodPreExprs.add(HornHelper.hh().createVariable(p, v));
		}
	}

	public void encode() {
		// Log.info("\tEncoding method " + method.getMethodName());
		LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();
		makeBlockPredicates(liveVariables);

		if (method.getSource() == null) {
			Log.debug("No implementation available for " + method.getMethodName());
			final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
			final List<ProverExpr> entryVars = HornHelper.hh().findOrCreateProverVar(p,
					methodContract.precondition.variables, varMap);
			final List<ProverExpr> exitVars = HornHelper.hh().findOrCreateProverVar(p,
					methodContract.postcondition.variables, varMap);

			final ProverExpr entryAtom = methodContract.precondition.predicate
					.mkExpr(entryVars.toArray(new ProverExpr[0]));
			final ProverExpr exitAtom = methodContract.postcondition.predicate
					.mkExpr(exitVars.toArray(new ProverExpr[0]));

			clauses.add(p.mkHornClause(exitAtom, new ProverExpr[] { entryAtom }, p.mkLiteral(true)));

			return;
		}

		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		todo.add(method.getSource());
		Set<CfgBlock> done = new HashSet<CfgBlock>();

		{
			// add an entry clause connecting with the precondition
			final HornPredicate entryPred = blockPredicates.get(method.getSource());

			final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
			final List<ProverExpr> entryVars = HornHelper.hh().findOrCreateProverVar(p, entryPred.variables, varMap);

			final List<ProverExpr> preVars = HornHelper.hh().findOrCreateProverVar(p, methodPreVariables, varMap);

			final ProverExpr preAtom = methodContract.precondition.predicate.mkExpr(preVars.toArray(new ProverExpr[0]));

			final List<ProverExpr> allEntryArgs = new ArrayList<ProverExpr>();
			allEntryArgs.addAll(preVars);
			allEntryArgs.addAll(entryVars);

			final ProverExpr entryAtom = entryPred.predicate.mkExpr(allEntryArgs.toArray(new ProverExpr[0]));

			clauses.add(p.mkHornClause(entryAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		}

		// translate reachable blocks
		while (!todo.isEmpty()) {
			CfgBlock current = todo.remove(0);
			// Log.info("\tEncoding block " + current);

			done.add(current);
			final HornPredicate exitPred = blockToHorn(current, liveVariables);

			// take care of return and successors
			final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
			final List<ProverExpr> exitVars = HornHelper.hh().findOrCreateProverVar(p, exitPred.variables, varMap);

			if (method.outgoingEdgesOf(current).isEmpty()) {
				// block ends with a return

				final List<ProverExpr> postVars = HornHelper.hh().findOrCreateProverVar(p,
						methodContract.postcondition.variables, varMap);

				final ProverExpr postAtom = methodContract.postcondition.predicate
						.mkExpr(postVars.toArray(new ProverExpr[0]));

				final List<ProverExpr> allExitArgs = new ArrayList<ProverExpr>();
				allExitArgs.addAll(postVars.subList(0, methodPreVariables.size()));
				allExitArgs.addAll(exitVars);

				final ProverExpr exitAtom = exitPred.predicate.mkExpr(allExitArgs.toArray(new ProverExpr[0]));

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { exitAtom }, p.mkLiteral(true)));

			} else {
				// link to the successor blocks

				final ProverExpr exitAtom = instPredicate(exitPred, exitVars);

				for (CfgEdge edge : method.outgoingEdgesOf(current)) {
					CfgBlock succ = method.getEdgeTarget(edge);
					if (!todo.contains(succ) && !done.contains(succ))
						todo.add(succ);

					final ProverExpr exitCondExpr;
					if (edge.getLabel().isPresent()) {						
						exitCondExpr = expEnc.exprToProverExpr(edge.getLabel().get(), varMap);
					} else {
						exitCondExpr = p.mkLiteral(true);
					}
					final HornPredicate entryPred = blockPredicates.get(succ);
					final List<ProverExpr> entryVars = HornHelper.hh().findOrCreateProverVar(p, entryPred.variables,
							varMap);

					final ProverExpr entryAtom = instPredicate(entryPred, entryVars);

					clauses.add(p.mkHornClause(entryAtom, new ProverExpr[] { exitAtom }, exitCondExpr));
				}
			}
		}
	}

	/**
	 * Creates one HornPredicate for each block. The predicate contains the
	 * list of live variables
	 * for that block sorted by names and a predicate over the types of
	 * these variables that has
	 * the same name as the block.
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

	private ProverExpr instPredicate(HornPredicate pred, List<ProverExpr> args) {
		List<ProverExpr> allArgs = new ArrayList<ProverExpr>();
		allArgs.addAll(methodPreExprs);
		allArgs.addAll(args);
		return pred.predicate.mkExpr(allArgs.toArray(new ProverExpr[allArgs.size()]));
	}

	private ProverFun freshHornPredicate(String name, List<Variable> sortedVars) {
		final List<Variable> allArgs = new LinkedList<Variable>();
		// add types for the method arguments, which
		// are later needed for the post-conditions
		allArgs.addAll(methodPreVariables);
		allArgs.addAll(sortedVars);
		return HornHelper.hh().genHornPredicate(p, method.getMethodName() + "_" + name, allArgs);
	}


	private HornPredicate blockToHorn(CfgBlock block, LiveVars<CfgBlock> liveVariables) {
		final HornPredicate initPred = blockPredicates.get(block);

		if (block.getStatements().isEmpty())
			return initPred;

		final Set<Variable> liveOutVars = liveVariables.liveOut.get(block);

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
		}

		final String initName = initPred.name;
		HornPredicate prePred = initPred;
		int counter = 0;

		for (Statement s : block.getStatements()) {
			final String postName = initName + "_" + (++counter);
			final List<Variable> interVarList = HornHelper.hh().setToSortedList(interVars[counter - 1]);
			final HornPredicate postPred = new HornPredicate(postName, interVarList,
					freshHornPredicate(postName, interVarList));
			
			StatementEncoder senc = new StatementEncoder(p, this.methodPreExprs, this.expEnc);			
			this.clauses.addAll(senc.statementToClause(s, prePred, postPred));
			
			prePred = postPred;
		}

		return prePred;
	}


}
