/**
 * 
 */
package soottocfg.cfg.optimization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;

import com.google.common.base.Preconditions;

/**
 * @author schaef
 *
 */
public class UnreachableNodeRemover {

	/**
	 * Removes all nodes and edges from the control-flow graph that are not
	 * connected to the source.
	 * @return true if vertices or edges have been removed.
	 */
	public static <A, B> boolean pruneUnreachableNodes(DirectedGraph<A, B> graph, A source) {
		Preconditions.checkArgument(graph.containsVertex(source), "Source not found in graph");
		int vertCount = graph.vertexSet().size();
		int edgeCount = graph.edgeSet().size();
		
		// collect all unreachable nodes.
		Set<A> verticesToRemove = new HashSet<A>(graph.vertexSet());
		verticesToRemove.removeAll(reachableFromSource(graph, source));
		// collect all unreachable edges
		Set<B> egdesToRemove = new HashSet<B>();
		for (A b : verticesToRemove) {
			for (B edge : graph.incomingEdgesOf(b)) {
				if (verticesToRemove.contains(graph.getEdgeSource(edge))) {
					egdesToRemove.add(edge);
				}
			}
			for (B edge : graph.outgoingEdgesOf(b)) {
				if (verticesToRemove.contains(graph.getEdgeTarget(edge))) {
					egdesToRemove.add(edge);
				}
			}
		}
		graph.removeAllVertices(verticesToRemove);
		graph.removeAllEdges(egdesToRemove);
		
		return !(vertCount == graph.vertexSet().size() && edgeCount == graph.edgeSet().size());
	}

	private static <A, B> Set<A> reachableFromSource(DirectedGraph<A, B> graph, A source) {
		Set<A> res = new HashSet<A>();
		Queue<A> todo = new LinkedList<A>();
		todo.add(source);
		while (!todo.isEmpty()) {
			A current = todo.poll();
			res.add(current);
			for (A succ : Graphs.successorListOf(graph, current)) {
				if (!todo.contains(succ) && !res.contains(succ)) {
					todo.add(succ);
				}
			}
		}
		return res;
	}
	
	/**
	 * Removes all nodes and edges from which the sink of the method is not
	 * reachable. Normally, all nodes and edges should be able to reach the
	 * sink. However, is we remove edges (e.g., when eliminating loops), this
	 * property might be violated and we have to re-establish it.
	 */
	public static <A, B> void removeDangelingPaths(DirectedGraph<A, B> graph, A source, A sink) {
		Preconditions.checkArgument(graph.containsVertex(source), "Source not found in graph");
		Preconditions.checkArgument(graph.containsVertex(sink), "Sink not found in graph");
		Set<B> edgesToRemove = new HashSet<B>();
		for (A b : graph.vertexSet()) {
			if (b != sink && graph.outDegreeOf(b) == 0) {
				NaiveLcaFinder<A, B> lca = new NaiveLcaFinder<A, B>(graph);
				A ancestor = lca.findLca(b, sink);
				List<B> path = DijkstraShortestPath.findPathBetween(graph, ancestor, b);
				Preconditions.checkArgument(path!=null && !path.isEmpty());
				edgesToRemove.add(path.get(0));
			}
		}
		graph.removeAllEdges(edgesToRemove);
		pruneUnreachableNodes(graph, source);
	}
}
