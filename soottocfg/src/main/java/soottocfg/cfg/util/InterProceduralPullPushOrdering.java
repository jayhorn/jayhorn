/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Optional;
import com.google.common.base.Verify;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.soot.SootToCfg;
import soottocfg.soot.util.FlowBasedPointsToAnalysis;
import soottocfg.soot.transformers.ArrayTransformer;
import soottocfg.util.Pair;

/**
 * @author schaef
 *
 */
public class InterProceduralPullPushOrdering {
	private Map<Method, Pair<FixedPointObject, FixedPointObject>> methodEntryExit;

	static class FixedPointObject {
		public Optional<Statement> stmt = Optional.absent();
		public Method containingMethod = null;
		// public CfgBlock containingCfgBlock = null;
//		public final Set<PushStatement> pushes = new HashSet<PushStatement>();
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("FPNode: ");
			sb.append(stmt);
			sb.append(" in ");
			sb.append(containingMethod == null ? "null" : this.containingMethod.getMethodName());
			return sb.toString();
		}
	}

	private Map<PullStatement, FixedPointObject> pullMap = new HashMap<PullStatement, FixedPointObject>();

	private final DirectedGraph<FixedPointObject, DefaultEdge> ipgraph;

	public InterProceduralPullPushOrdering(Method entryPoint) {
		methodEntryExit = new HashMap<Method, Pair<FixedPointObject, FixedPointObject>>();
		ipgraph = new DefaultDirectedGraph<FixedPointObject, DefaultEdge>(DefaultEdge.class);
		buildInterProcGraph(entryPoint, ipgraph);

		for (FixedPointObject fpo : ipgraph.vertexSet()) {
			if (fpo.stmt.isPresent() && fpo.stmt.get() instanceof PullStatement) {
				PullStatement pull = (PullStatement) fpo.stmt.get();
				Verify.verify(!pullMap.containsKey(pull),
						pull.toString() + " already in there. " + fpo.containingMethod);
				pullMap.put(pull, fpo);
			}
		}
	}

	public void debugPrintInfluencingPushs(PullStatement pull) {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing pushs that may influence:\n  ");
		sb.append(pull);
		sb.append("\n");
		for (FixedPointObject fpo : getFPOsInfluencing(pull)) {
			sb.append("\t");
			sb.append(fpo.stmt);
			sb.append("\tin Method");
			if (fpo.containingMethod != null) {
				sb.append(fpo.containingMethod.getMethodName());
			}
			sb.append("\n");
		}
		System.err.println(sb.toString());
	}

	/**
	 * TODO: rewrite!
	 * For the lack of a better points-to analysis, this checks for every pull
	 * if there are any previous pushs to a subtype
	 * of the object being pulled. We use this to decide which possible
	 * invariants we have to look for.
	 * As the name suggests, this is only a temporary solution and should be
	 * replaced by something more efficient later.
	 * 
	 * @param pull
	 * @return
	 */
	public Set<ClassVariable> getBrutalOverapproximationOfPossibleType(PullStatement pull) {
		Set<ClassVariable> usedSubtypes = new HashSet<ClassVariable>();
		usedSubtypes.add(pull.getClassSignature());
		if (!pullMap.containsKey(pull)) {
//			System.err.println("Pull not reachable from program entry: " + pull);
			return usedSubtypes;
		}
		FixedPointObject fpo = pullMap.get(pull);
		Queue<FixedPointObject> todo = new LinkedList<FixedPointObject>();
		todo.addAll(Graphs.predecessorListOf(ipgraph, fpo));
		Set<FixedPointObject> done = new HashSet<FixedPointObject>();
		while (!todo.isEmpty()) {
			FixedPointObject cur = todo.remove();
			done.add(cur);
			if (cur.stmt.isPresent() && cur.stmt.get() instanceof PushStatement
					&& (((PushStatement) cur.stmt.get()).getClassSignature().subclassOf(pull.getClassSignature()))) {
				usedSubtypes.add(((PushStatement) cur.stmt.get()).getClassSignature());
			}
			for (FixedPointObject pre : Graphs.predecessorListOf(ipgraph, cur)) {
				if (!todo.contains(pre) && !done.contains(pre)) {
					todo.add(pre);
				}
			}

		}
		return usedSubtypes;
	}

	public Set<PushStatement> getPushsInfluencing(PullStatement pull) {
		Set<PushStatement> ret = new HashSet<PushStatement>();
		Set<FixedPointObject> fpos = getFPOsInfluencing(pull);
		for (FixedPointObject fpo : fpos) {
			if (fpo.stmt.isPresent() && fpo.stmt.get() instanceof PushStatement) {
				//TODO: I guess we can assert that this is reachable.
				ret.add((PushStatement)fpo.stmt.get());
			}
		}
		return ret;
	}

	private Set<FixedPointObject> getFPOsInfluencing(PullStatement pull) {

		Set<FixedPointObject> ret = new HashSet<FixedPointObject>();

		if (!pullMap.containsKey(pull)) {
//			System.err.println("Pull not reachable from program entry: " + pull);
			return ret;
		}

		FixedPointObject fpo = pullMap.get(pull);
		Queue<FixedPointObject> todo = new LinkedList<FixedPointObject>();
		todo.addAll(Graphs.predecessorListOf(ipgraph, fpo));
		Set<FixedPointObject> done = new HashSet<FixedPointObject>();
		while (!todo.isEmpty()) {
			FixedPointObject cur = todo.remove();
			done.add(cur);
			boolean stopTraverse = false;
			if (cur.stmt.isPresent() && cur.stmt.get() instanceof PushStatement) {
				PushStatement push = (PushStatement) cur.stmt.get();
				if (mayAlias(push, pull)) {
					ret.add(cur);
//					System.out.println("Must shadow " + push + " -> " + pull + " ?");
					if (mustShadow(push, pull)) {
//						System.out.println("Yes!");
						stopTraverse = true;
					}
				}
			}
			
			if (!stopTraverse) {
				for (FixedPointObject pre : Graphs.predecessorListOf(ipgraph, cur)) {
					if (!todo.contains(pre) && !done.contains(pre)) {
						todo.add(pre);
					}
				}
			}
		}
//		Verify.verify(!ret.isEmpty(),
//				"Cannot find a push that affects this pull. This would introduce an assume(false): " + pull);		
		return ret;
	}

	/**
	 * Returns true if the objects in the push and pull statement may be aliased.
	 * Current we approximate this by simply checking if their types are compatible.
	 * I.e., this is a heavy over-approximation (i.e., never returns false if alias 
	 * is not possible, but returns true in cases where no alias is possible).
	 * @param push
	 * @param pull
	 * @return true if the objects in pull and push may alias.
	 */
	protected boolean mayAlias(PushStatement push, PullStatement pull) {
		FlowBasedPointsToAnalysis pta = SootToCfg.getPointsToAnalysis();
		if (pta != null) 
			return pta.mayAlias(pull.getObject(), push.getObject());
		return canAffectPull(push, pull);
	}

	/**
	 * Returns true if the push must shadow all preceding pushs that could
	 * influence the pull statement. E.g., 
	 * 
	 * push(Object, o, 3)
	 * p=o
	 * push(Object, p, 42)
	 * pull(Object, o)
	 * 
	 * In this example, the second push shadows the first because any execution of pull 
	 * must go through the second push which overwrites the changes performed by the first
	 * push.
	 * So, mustShadow for the pull and the second push should return true, and mustShadow for the
	 * pull and the push should return false.
	 * 
	 * TODO: Currently, this is soundly approximated by always returning false. 
	 * @param push
	 * @param pull
	 * @return true if push shadows all preceding pushs that may affect pull. False otherwise.
	 */
	protected boolean mustShadow(PushStatement push, PullStatement pull) {
                // the standard check does not work for arrays, where we
                // also have to take indexes into account
                if (push.getObject().getType().toString().startsWith(ArrayTransformer.arrayTypeName))
                        return false;
		FlowBasedPointsToAnalysis pta = SootToCfg.getPointsToAnalysis();
		if (pta != null)
			return pta.mustAlias(pull.getObject(), push.getObject());
		return false;
	}

	private boolean canAffectPull(PushStatement push, PullStatement pull) {
		ClassVariable pushCv = push.getClassSignature();
		ClassVariable pullCv = pull.getClassSignature();
		return pushCv.subclassOf(pullCv) || pushCv.superclassOf(pullCv);
	}

	private Pair<FixedPointObject, FixedPointObject> buildInterProcGraph(Method method,
			DirectedGraph<FixedPointObject, DefaultEdge> ipgraph) {

		if (methodEntryExit.containsKey(method)) {
			return methodEntryExit.get(method);
		}

		FixedPointObject fpEntry = new FixedPointObject();
		FixedPointObject fpExit = new FixedPointObject();
		ipgraph.addVertex(fpEntry);
		ipgraph.addVertex(fpExit);
		Pair<FixedPointObject, FixedPointObject> ret = new Pair<FixedPointObject, FixedPointObject>(fpEntry, fpExit);

		methodEntryExit.put(method, ret);

		Map<CfgBlock, FixedPointObject> entryFpo = new HashMap<CfgBlock, FixedPointObject>();
		Map<CfgBlock, FixedPointObject> exitFpo = new HashMap<CfgBlock, FixedPointObject>();

		// generate FixedPointObjects for each CfgBlock
		for (CfgBlock cur : method.vertexSet()) {

			FixedPointObject fpo;
			if (cur == method.getSource()) {
				fpo = fpEntry;
			} else {
				fpo = new FixedPointObject();
				ipgraph.addVertex(fpo);
			}

			// create the subgraph from the current cfg block
			entryFpo.put(cur, fpo);
			for (Statement st : cur.getStatements()) {
				if (st instanceof CallStatement) {
					CallStatement cs = (CallStatement) st;
					Pair<FixedPointObject, FixedPointObject> pair = buildInterProcGraph(cs.getCallTarget(), ipgraph);
					ipgraph.addEdge(fpo, pair.getFirst());
					fpo = new FixedPointObject();
					ipgraph.addVertex(fpo);
					ipgraph.addEdge(pair.getSecond(), fpo);
				} else if (st instanceof PushStatement || st instanceof PullStatement) {
					fpo.stmt = Optional.of(st);
					// fpo.containingCfgBlock = cur;
					fpo.containingMethod = method;
					FixedPointObject nextFpo = new FixedPointObject();
					ipgraph.addVertex(nextFpo);
					ipgraph.addEdge(fpo, nextFpo);
					fpo = nextFpo;
				} else {
					// do nothing
				}
			}
			exitFpo.put(cur, fpo);
			if (Graphs.successorListOf(method, cur).isEmpty()) {
				ipgraph.addEdge(fpo, fpExit);
			}
		}

		// Now connect the FixedPointObjects
		for (CfgEdge edge : method.edgeSet()) {
			FixedPointObject src = exitFpo.get(ipgraph.getEdgeSource(edge));
			FixedPointObject tgt = entryFpo.get(ipgraph.getEdgeTarget(edge));
			ipgraph.addEdge(src, tgt);
		}
		if (method.edgeSet().isEmpty()) {
			// in case the method has no body.
			ipgraph.addEdge(fpEntry, fpExit);
		}
		return ret;
	}

}
