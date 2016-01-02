package jayhorn.old_inconsistency_check.faultlocalization;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.util.VertexPair;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverResult;
import jayhorn.util.SimplCfgToProver;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.EffectualSet;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.PostDominators;
import soottocfg.util.Pair;

public class InconsistencyLocalization {

	private final Prover prover;
	private final Map<ProverExpr, Statement> peToStatement;
	private final Map<ProverExpr, VertexPair<CfgBlock>> nestedDiamondMap;

	public InconsistencyLocalization(Prover p) {
		prover = p;
		peToStatement = new HashMap<ProverExpr, Statement>();
		nestedDiamondMap = new HashMap<ProverExpr, VertexPair<CfgBlock>>();
	}

	public Set<Statement> computeRelevantStatements(SimplCfgToProver s2p, Method graph, CfgBlock source,
			CfgBlock joinAfterSink, Optional<ProverExpr> precondition, Optional<ProverExpr> postcondition) {
		Set<Statement> result = new LinkedHashSet<Statement>();
		if (source.equals(graph.getSource()) && joinAfterSink == null) {
			// this is the original graph and we can use it immediately.
		} else {
			// extract the subgraph from 'source' to 'joinAfterSink'.
			// Where we have to remove all statements from 'joinAfterSink'.
			DirectedGraph<CfgBlock, CfgEdge> subgraph = new DefaultDirectedGraph<CfgBlock, CfgEdge>(
					graph.getEdgeFactory());
			for (CfgBlock v : GraphUtil.getVerticesBetween(graph, source, joinAfterSink)) {
				subgraph.addVertex(v);
			}
			for (CfgEdge e : graph.edgeSet()) {
				if (subgraph.vertexSet().contains(graph.getEdgeSource(e))
						&& subgraph.vertexSet().contains(graph.getEdgeTarget(e))) {
					subgraph.addEdge(graph.getEdgeSource(e), graph.getEdgeTarget(e));
				}
			}
			graph = graph.createMethodFromSubgraph(subgraph, graph.getMethodName() + "__nested");
			CfgBlock newSink = new CfgBlock(graph);
			for (CfgBlock pre : Graphs.predecessorListOf(graph, joinAfterSink)) {
				graph.removeEdge(pre, joinAfterSink);
				graph.addEdge(pre, newSink);
			}
			graph.removeVertex(joinAfterSink);
		}
		List<ProverExpr> proofObligations = generatProofObligations(s2p, graph);
		ProverExpr[] interpolants = computeInterpolants(proofObligations, s2p.generatedAxioms(), precondition,
				postcondition);
		List<Integer> changePositions = findPositionsWhereInterpolantChanges(interpolants);
		if (changePositions.isEmpty()) {
			System.err.println("Nothing changed for " + graph);
			System.err.println("Pre " + precondition);
			System.err.println("Post " + postcondition);
			for (int i = 0; i < interpolants.length; i++) {
				System.err.println("Interpolant " + i + ": " + interpolants[i]);
			}
		}
		for (Integer i : changePositions) {
			Verify.verify(proofObligations.size() > i);
			ProverExpr pe = proofObligations.get(i);
			if (peToStatement.containsKey(pe)) {
				result.add(peToStatement.get(pe));
			} else {
				VertexPair<CfgBlock> diamond = nestedDiamondMap.get(pe);
				List<ProverExpr> newPreCondition = proofObligations.subList(0, i);
				if (postcondition.isPresent()) {
					newPreCondition.add(0, postcondition.get());
				}
				Optional<ProverExpr> interpolantBefore = Optional
						.fromNullable(prover.mkAnd(newPreCondition.toArray(new ProverExpr[newPreCondition.size()])));

				List<ProverExpr> newPostCondition = proofObligations.subList(i+1, proofObligations.size());
				if (postcondition.isPresent()) {
					newPostCondition.add(postcondition.get());
				}
				Optional<ProverExpr> interpolantAfter = Optional
						.fromNullable(prover.mkAnd(newPostCondition.toArray(new ProverExpr[newPostCondition.size()])));
				for (CfgBlock suc : Graphs.successorListOf(graph, diamond.getFirst())) {
					InconsistencyLocalization nestedLoc = new InconsistencyLocalization(prover);
					result.addAll(nestedLoc.computeRelevantStatements(s2p, graph, suc, diamond.getSecond(),
							interpolantBefore, interpolantAfter));
				}
			}
		}
		return result;
	}

	private ProverExpr[] computeInterpolants(List<ProverExpr> proofObligations, List<ProverExpr> axioms,
			Optional<ProverExpr> precondition, Optional<ProverExpr> postcondition) {
		prover.push();

		int partition = 0;
		prover.setPartitionNumber(partition++);
		if (precondition.isPresent()) {
			prover.addAssertion(precondition.get());
		} else {
			prover.addAssertion(prover.mkLiteral(true));
		}

		for (ProverExpr pe : proofObligations) {
			prover.setPartitionNumber(partition++);
			prover.addAssertion(pe);
		}
		prover.setPartitionNumber(partition++);

		for (ProverExpr axiom : axioms) {
			prover.addAssertion(axiom);
		}

		if (postcondition.isPresent()) {
			prover.addAssertion(postcondition.get());
		}

		ProverResult proverResult = prover.checkSat(true);
		if (proverResult != ProverResult.Unsat) {
			throw new RuntimeException("Fault localization failed because is "+proverResult+" and not UNSAT");
		}

		int[][] ordering = new int[partition][1];
		for (int i = 0; i < partition; i++) {
			ordering[i][0] = i;
		}
		ProverExpr[] interpolants = prover.interpolate(ordering);
		prover.pop();
		return interpolants;
	}

	/**
	 * Traverses 'interpolants' from left to right and returns the list of all
	 * indices 'i' where !interpolantsAreEqual(interpolants[i-1],
	 * interpolants[i]).
	 * 
	 * @param interpolants
	 * @return
	 */
	private List<Integer> findPositionsWhereInterpolantChanges(ProverExpr[] interpolants) {
		Preconditions.checkArgument(interpolants != null && interpolants.length > 0);
		List<Integer> result = new LinkedList<Integer>();
		ProverExpr current = interpolants[0];
		for (int i = 1; i < interpolants.length; i++) {
			if (!interpolantsAreEqual(current, interpolants[i])) {
				result.add(i - 1); // subtract 1 because we do not want to
									// include the
									// precondition
				current = interpolants[i];
			}
		}
		return result;
	}

	/**
	 * Checks if two ProverExpr are equal.
	 * 
	 * @param i1
	 * @param i2
	 * @return
	 */
	private boolean interpolantsAreEqual(ProverExpr i1, ProverExpr i2) {
		Preconditions.checkNotNull(i1);
		Preconditions.checkNotNull(i2);
		return i1.toString().equals(i2.toString()); // TODO
	}

	/**
	 * Creates a sequence of prover expressions between which we interpolate
	 * for the fault localization. One proof obligation is either the
	 * transition relation of a single statement or a disjunction encoding a
	 * control-flow diamond. E.g.:
	 * x=0;
	 * if (a) x++ else x--;
	 * assert (x==0);
	 * where 'inconsistentBlock' refers to 'x=0', becomes:
	 * x0==0, (a && x1=x0+1)||(!a && x1=x0-1), x1==0
	 * 
	 * @param s2p
	 * @param method
	 * @return
	 */
	private List<ProverExpr> generatProofObligations(SimplCfgToProver s2p, Method method) {
		CfgBlock source = GraphUtil.getSource(method);
		CfgBlock sink = GraphUtil.getSink(method);
		Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, source);
		PostDominators<CfgBlock> pdom = new PostDominators<CfgBlock>(method, sink);
		EffectualSet<CfgBlock> effSet = new EffectualSet<CfgBlock>(dom, pdom);

		Pair<CfgBlock, List<ProverExpr>> pair = generateProofObligations(s2p, method, source, effSet);
		Verify.verify(sink.equals(pair.getFirst()));
		return pair.getSecond();
	}

	/**@formatter:off 
	 * -
	 * Recursive part of 'generatProofObligations' above. 
	 * TODO: this one needs a better description.
	 * 
	 * Example.:
	 *   x=0;
	 *   if (a) x++ else x--;
	 *   assert (x==0);
	 * 
	 * Assume the first block has only 'x=0'. It will turn that into a prover expression
	 * and then see that there are two successors (x++ and x--). It will recursively call
	 * itself for both. The recursive calls will process x++, or x-- resp. and then hit
	 * the next block 'assert(x==0)' which is in a higher equivalence class in the 
	 * effectual set. Thus the recursive calls return the tuples ('assert (x==0)', {a && x'=x+1}) 
	 * and ('assert (x==0)', {!a && x'=x-1}). Since the first element is the same in both cases,
	 * we can merge the second elements to (a && x'=x+1)||(!a && x'=x-1) and continue with the
	 * next block.
	 * If we start from the source of the graph, the procedure returns the sink of the graph
	 * and the transition relation of the graph.
	 * (Note that this only works if all branches are proper diamonds. Otherwise we have to
	 * preprocess the graph)
	 * @param s2p
	 * @param graph
	 * @param block
	 * @param lattice
	 * @return
	 */
	private Pair<CfgBlock, List<ProverExpr>> generateProofObligations(SimplCfgToProver s2p,
			DirectedGraph<CfgBlock, CfgEdge> graph, CfgBlock block, EffectualSet<CfgBlock> effSet) {
		List<ProverExpr> conj = new LinkedList<ProverExpr>();
		Set<CfgBlock> latticeElement = effSet.findInLattice(block);
		while (latticeElement.contains(block)) {
			for (Statement s : block.getStatements()) {
				ProverExpr pe = s2p.statementToTransitionRelation(s);
				// TODO: hack - pe should not be null later.
				if (pe == null)
					continue;
				peToStatement.put(pe, s);
				conj.add(pe);
			}

			CfgBlock next = null;
			List<CfgBlock> successors = Graphs.successorListOf(graph, block);
			if (successors.size() == 1) {
				next = successors.get(0);
			} else if (successors.isEmpty()) {
				return new Pair<CfgBlock, List<ProverExpr>>(block, conj);
			} else {
				Verify.verify(successors.size() == 2);
				CfgBlock join = null;
				List<ProverExpr> disj = new LinkedList<ProverExpr>();
				for (CfgBlock suc : successors) {
					Verify.verify(effSet.isBelowInLattice(suc, latticeElement));
					Pair<CfgBlock, List<ProverExpr>> sub = generateProofObligations(s2p, graph, suc, effSet);
					if (join == null) {
						join = sub.getFirst();
					} else {
						//this is enforced by ConvertToDiamondShape
						Verify.verify(sub.getFirst() == join);
					}
					if (!sub.getSecond().isEmpty()) {
						disj.add(prover.mkAnd(sub.getSecond().toArray(new ProverExpr[sub.getSecond().size()])));
					} else {
						System.err.println("TODO: raise exception");//TODO
					}
				}
				Verify.verify(join != null);
				if (disj.isEmpty()) {
					System.err.println("TODO: raise exception"); //TODO
				}
				ProverExpr pe = prover.mkOr(disj.toArray(new ProverExpr[disj.size()]));
				nestedDiamondMap.put(pe, new VertexPair<CfgBlock>(block, join));
				conj.add(pe);
				next = join;
			}
			if (latticeElement.contains(next)) {
				block = next;
			} else {
				Verify.verify(effSet.isAboveInLattice(next, latticeElement));
				return new Pair<CfgBlock, List<ProverExpr>>(next, conj);
			}

		}
		return new Pair<CfgBlock, List<ProverExpr>>(null, conj);
	}
		
}
