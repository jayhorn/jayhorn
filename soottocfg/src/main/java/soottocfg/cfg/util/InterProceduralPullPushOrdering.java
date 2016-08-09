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
import soottocfg.util.Pair;

/**
 * @author schaef
 *
 */
public class InterProceduralPullPushOrdering {
	private Map<Method, Pair<FixedPointObject, FixedPointObject>> methodEntryExit;

	static class FixedPointObject {
		public final Set<PushStatement> relevantBefore = new HashSet<PushStatement>();
		public final Set<PushStatement> relevantAfter = new HashSet<PushStatement>();
		public Optional<Statement> stmt = Optional.absent();
		public Method containingMethod = null;
//		public CfgBlock containingCfgBlock = null;

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
				Verify.verify(!pullMap.containsKey(pull), pull.toString() + " already in there. "+fpo.containingMethod);
				pullMap.put(pull, fpo);
			}
		}
	}

	public void debugPrintInfluencingPushs(PullStatement pull) {
		StringBuilder sb = new StringBuilder();
		sb.append("Listing pushs that may influence:\n  ");
		sb.append(pull);
		sb.append("\n");
		for (FixedPointObject fpo : getPushsInfluencing(pull)) {
			sb.append("\t");
			sb.append(fpo.stmt);
			sb.append("\tin Method");
			if (fpo.containingMethod!=null) {
			sb.append(fpo.containingMethod.getMethodName());
			}
			sb.append("\n");
		}
		System.err.println(sb.toString());
	}
	
	public Set<FixedPointObject> getPushsInfluencing(PullStatement pull) {
		
		Set<FixedPointObject> ret = new HashSet<FixedPointObject>();
		
		if (!pullMap.containsKey(pull)) {
			System.err.println("Pull not reachable from program entry: " + pull);
			return ret;
		}
		
		FixedPointObject fpo = pullMap.get(pull);
		Queue<FixedPointObject> todo = new LinkedList<FixedPointObject>();
		todo.addAll(Graphs.predecessorListOf(ipgraph, fpo));
		Set<FixedPointObject> done = new HashSet<FixedPointObject>();
		while (!todo.isEmpty()) {
			FixedPointObject cur = todo.remove();
			done.add(cur);
			if (cur.stmt.isPresent() && cur.stmt.get() instanceof PushStatement) {
				//TODO check if the pull works type wise.
				ret.add(cur);
			} else {
				for (FixedPointObject pre : Graphs.predecessorListOf(ipgraph, cur)) {
					if (!todo.contains(pre) && !done.contains(pre)) {
						todo.add(pre);
					}
				}
			}
		}
		return ret;
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
//					fpo.containingCfgBlock = cur;
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
