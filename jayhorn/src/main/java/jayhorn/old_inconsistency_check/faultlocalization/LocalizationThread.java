/**
 * 
 */
package jayhorn.old_inconsistency_check.faultlocalization;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Optional;

import jayhorn.old_inconsistency_check.Inconsistency;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.util.ConvertToDiamondShape;
import jayhorn.util.SimplCfgToProver;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.GraphUtil;

/**
 * @author schaef
 *
 */
public class LocalizationThread implements Runnable {

	// private final Program program;
	private final Inconsistency inconsistency;
	private final Prover prover;
	private final Set<Statement> relevantStatements;
	
	public LocalizationThread(Inconsistency ic, Prover p) {
		// program = prog;
		inconsistency = ic;
		prover = p;
		relevantStatements = new HashSet<Statement>();
	}
	
	public Set<Statement> getRelevantStatements() {
		return relevantStatements;
	}

	@Override
	public void run() {
	
		prover.setConstructProofs(true);
		SimplCfgToProver s2p = new SimplCfgToProver(prover);
		//first generate a subgraph of the method that only contains
		//paths through the inconsistent block and where all branching
		//has diamond shape.
		Method subgraph = createDiamondShapedSliceForFaultLocalization(inconsistency.getMethod(),
				inconsistency.getRootOfInconsistency());
		//now run the fault localization on 'subgraph'. For this to work,
		//we have to be sure that subgraph has no feasible path.
		InconsistencyLocalization il = new InconsistencyLocalization(prover);
		Optional<ProverExpr> pre = Optional.absent();
		Optional<ProverExpr> post = Optional.absent();
		Set<Statement> res = il.computeRelevantStatements(s2p, subgraph, subgraph.getSource(), null, pre, post);
		relevantStatements.addAll(res);
	}
		
	/**
	 * Create a subgraph of all paths going through 'inconsistentBlock'
	 * and enforce that all conditionals are diamond shaped. 
	 * @param method
	 * @param inconsistentBlock
	 * @return
	 */
	private Method createDiamondShapedSliceForFaultLocalization(Method method, CfgBlock inconsistentBlock) {
		Method subgraph = method.createMethodFromSubgraph(
				GraphUtil.computeSubgraphThroughVertex(method, inconsistentBlock), method.getMethodName() + "_slice");
		ConvertToDiamondShape converter = new ConvertToDiamondShape();
		converter.convert(subgraph);
		return subgraph;
	}

}
