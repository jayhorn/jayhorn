/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.Graphs;

import com.google.common.base.VerifyException;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverResult;
import jayhorn.util.EdgeLabelToAssume;
import jayhorn.util.ICfgToProver;
import jayhorn.util.LoopRemoval;
import jayhorn.util.SimplCfgToProver;
import jayhorn.util.SsaTransformer;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.UnreachableNodeRemover;

/**
 * @author schaef
 *
 */
public class InconsistencyThread implements Runnable {

	public boolean debugMode = false;

	private final Method method;
	private final Prover prover;
	private final Program program;
	
	private final Map<CfgBlock, ProverExpr> blockVars = new LinkedHashMap<CfgBlock, ProverExpr>();

	private final Set<CfgBlock> inconsistentBlocks = new HashSet<CfgBlock>();

	public Set<CfgBlock> getInconsistentBlocks() {
		return this.inconsistentBlocks;
	}

	/**
	 * 
	 */
	public InconsistencyThread(Program prog, Method m, Prover p) {
		method = m;
		prover = p;
		program = prog;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		inconsistentBlocks.clear();		
		if (method.vertexSet().isEmpty()) {
			System.out.println("Nothing to do for " + method.getMethodName());
			return;
		} else {
			System.out.println("Analyzing " + method.getMethodName());
		}		
		
		UnreachableNodeRemover<CfgBlock, CfgEdge> unr = new UnreachableNodeRemover<CfgBlock, CfgEdge>(method, method.getSource(), method.getSink());
		if (unr.pruneUnreachableNodes()) {
			System.err.println("removed unreachable nodes for "+method.getMethodName());
		}
		
		EdgeLabelToAssume etoa = new EdgeLabelToAssume(method);
		etoa.turnLabeledEdgesIntoAssumes();
		
		try {
			LoopRemoval lr = new LoopRemoval(method);
			lr.removeLoops();
			lr.verifyLoopFree();// TODO: run only in debug mode.
		} catch (VerifyException e) {
			if (!GraphUtil.isReducibleGraph(method, method.getSource())) {
				Log.error(method.getMethodName() + " has an irreducible CFG with loops and cannot be checked."); 
				//TODO dont give up ... make the thing reducible instead.
				return;
			} else {
				throw new RuntimeException(e);
			}
		}
		
		GraphUtil.getSink(method);
		
		SsaTransformer ssa = new SsaTransformer(program, method);
		ssa.eliminatePhiStatements();

//		{
//		SsaPrinter printer = new SsaPrinter();
//		StringBuilder sb = new StringBuilder();
//		printer.printMethod(sb, method);
//		System.out.println(sb);
//		}

		createVerificationCondition();

		Set<ProverExpr> enablingClause = new HashSet<ProverExpr>();
		Map<ProverExpr, CfgBlock> blocks2cover = new HashMap<ProverExpr, CfgBlock>();
		for (Entry<CfgBlock, ProverExpr> entry : blockVars.entrySet()) {
			blocks2cover.put(entry.getValue(), entry.getKey());
			enablingClause.add(entry.getValue());
		}
		Set<CfgBlock> covered = new HashSet<CfgBlock>();

		ProverResult result = prover.checkSat(true);
		// prover.push();

		while (result == ProverResult.Sat) {
			// prover.pop();
			Set<ProverExpr> conj = new HashSet<ProverExpr>();
			if (debugMode)
				System.err.print("Path containing ");
			for (Entry<ProverExpr, CfgBlock> entry : blocks2cover.entrySet()) {
				if (prover.evaluate(entry.getKey()).getBooleanLiteralValue()) {
					conj.add(entry.getKey());
					covered.add(entry.getValue());
					enablingClause.remove(entry.getKey());
					if (debugMode)
						System.err.print(entry.getValue().getLabel() + " ");
				} else {
					conj.add(prover.mkNot(entry.getKey()));
				}
			}
			if (debugMode)
				System.err.println(".");
			// if (enablingClause.isEmpty()) break;

			// prover.push();
			// ProverExpr enabling = prover.mkOr(enablingClause.toArray(new
			// ProverExpr[enablingClause.size()]));
			// prover.addAssertion(enabling);
			ProverExpr blocking = prover.mkNot(prover.mkAnd(conj.toArray(new ProverExpr[conj.size()])));
			prover.addAssertion(blocking);			
			result = prover.checkSat(true);
		}
		// prover.pop();
		
		Set<CfgBlock> notCovered = new HashSet<CfgBlock>(blockVars.keySet());
		notCovered.removeAll(covered);

		inconsistentBlocks.addAll(notCovered);

		return;
	}


	private void createVerificationCondition() {
		
		ICfgToProver cfg2prover = new SimplCfgToProver(prover);
		
		// first create a boolean variable for each block.
		for (CfgBlock b : method.vertexSet()) {
			blockVars.put(b, prover.mkVariable(b.getLabel(), prover.getBooleanType()));
		}
		// assert that the boolean var for the root must be true
		prover.addAssertion(blockVars.get(method.getSource()));

		for (CfgBlock b : method.vertexSet()) {
			List<ProverExpr> conj = new LinkedList<ProverExpr>();

			// ensure that only complete paths can be in a model
			List<ProverExpr> comeFrom = new LinkedList<ProverExpr>();
			for (CfgBlock pre : Graphs.predecessorListOf(method, b)) {
				comeFrom.add(blockVars.get(pre));
			}
			if (!comeFrom.isEmpty()) {
				conj.add(prover.mkOr(comeFrom.toArray(new ProverExpr[comeFrom.size()])));
			}

			for (Statement s : b.getStatements()) {
				ProverExpr c = cfg2prover.statementToTransitionRelation(s);
				if (c == null) {					
					continue; // TODO: hack, remove later
				}
				conj.add(c);
			}
			List<ProverExpr> disj = new LinkedList<ProverExpr>();
			for (CfgBlock succ : Graphs.successorListOf(method, b)) {
				// This assumes that all edge labels have been turned into
				// assumes.
				disj.add(blockVars.get(succ));
			}
			if (!disj.isEmpty()) {
				conj.add(prover.mkOr(disj.toArray(new ProverExpr[disj.size()])));
			}
			ProverExpr tr;
			if (conj.isEmpty()) {
				tr = prover.mkLiteral(true);
			} else {
				tr = prover.mkAnd(conj.toArray(new ProverExpr[conj.size()]));
			}
			ProverExpr blockTransitionFormula = prover.mkImplies(blockVars.get(b), tr);			
			prover.addAssertion(blockTransitionFormula);
		}

		//TODO: hack
		for (ProverExpr axiom : ((SimplCfgToProver)cfg2prover).generateParamTypeAxioms(this.method)) {
			prover.addAssertion(axiom);
			System.err.println(axiom);
		}
		
		for (ProverExpr axiom : cfg2prover.generatedAxioms()) {
			prover.addAssertion(axiom);
		}
		
	}



}
