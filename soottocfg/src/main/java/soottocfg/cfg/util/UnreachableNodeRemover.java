/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Preconditions;

/**
 * @author schaef
 *
 */
public class UnreachableNodeRemover<A, B extends DefaultEdge> {

	private final DirectedGraph<A, B> graph;
	private final A source, sink;

	/**
	 * Creates a loop processor for a graph g with source src and sink snk.
	 * 
	 * @param g
	 * @param src
	 * @param snk
	 */
	public UnreachableNodeRemover(DirectedGraph<A, B> g, A src, A snk) {
		graph = g;
		source = src;
		sink = snk;
		Preconditions.checkArgument(graph.containsVertex(source), "Source not found in graph");
	}

	/**
	 * Removes all nodes and edges from the control-flow graph that are not
	 * connected to the source.
	 * @return true if vertices or edges have been removed.
	 */
	public boolean pruneUnreachableNodes() {
		int vertCount = graph.vertexSet().size();
		int edgeCount = graph.edgeSet().size();
		
		ConnectivityInspector<A, B> insp = new ConnectivityInspector<A, B>(graph);
		// collect all unreachable nodes.
		Set<A> verticesToRemove = new HashSet<A>(graph.vertexSet());
		verticesToRemove.removeAll(insp.connectedSetOf(source));
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

	/**
	 * Removes all nodes and edges from which the sink of the method is not
	 * reachable. Normally, all nodes and edges should be able to reach the
	 * sink. However, is we remove edges (e.g., when eliminating loops), this
	 * property might be violated and we have to re-establish it.
	 */
	public void removeDangelingPaths() {
		Preconditions.checkArgument(graph.containsVertex(sink), "Sink not found in graph");
		Set<B> edgesToRemove = new HashSet<B>();
		for (A b : graph.vertexSet()) {
			if (b != sink && graph.outDegreeOf(b) == 0) {
				NaiveLcaFinder<A, B> lca = new NaiveLcaFinder<A, B>(graph);
				A ancestor = lca.findLca(b, sink);
				List<B> path = DijkstraShortestPath.findPathBetween(graph, ancestor, b);
				Preconditions.checkArgument(!path.isEmpty());
				edgesToRemove.add(path.get(0));
			}
		}
		graph.removeAllEdges(edgesToRemove);
		pruneUnreachableNodes();
	}
}
