/**
 * 
 */
package soottocfg.test.optimization_test;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;

import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.EffectualSet;
import soottocfg.cfg.util.PostDominators;

/**
 * @author schaef
 *
 */
public class EffectualSetTest {

	/**
	 * 
	 */
	public EffectualSetTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testWithLoops() {
		// create a graph with two diamonds.
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		directedGraph.addVertex("0"); // src
		directedGraph.addVertex("1");
		directedGraph.addVertex("2"); // in effectual set
		directedGraph.addVertex("3"); // in effectual set
		directedGraph.addVertex("4");

		directedGraph.addEdge("0", "1");
		directedGraph.addEdge("1", "2");
		directedGraph.addEdge("1", "3");

		directedGraph.addEdge("3", "0"); // strange loop

		directedGraph.addEdge("2", "4");
		directedGraph.addEdge("3", "4");

		directedGraph.addEdge("4", "2"); // strange loop

		Set<String> expectedResult = new HashSet<String>();
		expectedResult.add("2");
		expectedResult.add("3");

		Dominators<String> dom = new Dominators<String>(directedGraph, "0");
		PostDominators<String> pdom = new PostDominators<String>(directedGraph, "4");
		EffectualSet<String> effectualSet = new EffectualSet<String>(dom, pdom);
		Assert.assertTrue(effectualSet.getEffectualSet().equals(expectedResult));
	}

	@Test
	public void testSequentialDiamonds() {
		int numberOfDiamonds = 5;
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);

		String src = "S";
		String node = src;
		nodeNumberer = 0;
		Set<String> expectedResult = new HashSet<String>();
		directedGraph.addVertex(node);
		for (int i = 0; i < numberOfDiamonds; i++) {
			String l = String.format("%d", nodeNumberer++);
			String r = String.format("%d", nodeNumberer++);
			directedGraph.addVertex(l);
			directedGraph.addVertex(r);
			directedGraph.addEdge(node, l);
			directedGraph.addEdge(node, r);
			String nextNode = String.format("%d", nodeNumberer++);
			directedGraph.addVertex(nextNode);
			directedGraph.addEdge(l, nextNode);
			directedGraph.addEdge(r, nextNode);
			expectedResult.add(l);
			expectedResult.add(r);
			node = nextNode;
		}
		String snk = node;

		Dominators<String> dom = new Dominators<String>(directedGraph, src);
		PostDominators<String> pdom = new PostDominators<String>(directedGraph, snk);
		EffectualSet<String> effectualSet = new EffectualSet<String>(dom, pdom);

		Assert.assertTrue(effectualSet.getEffectualSet().equals(expectedResult));
	}

	@Test
	public void testNestedDiamonds() {
		int depthOfNestedDiamonds = 4;
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		final String src = "S";
		final String snk = "X";
		directedGraph.addVertex(src);
		directedGraph.addVertex(snk);
		nodeNumberer = 0;
		Set<String> expectedResult = createdNestedDiamonds(directedGraph, src, snk, depthOfNestedDiamonds);
		Dominators<String> dom = new Dominators<String>(directedGraph, src);
		PostDominators<String> pdom = new PostDominators<String>(directedGraph, snk);
		EffectualSet<String> effectualSet = new EffectualSet<String>(dom, pdom);

		// effectualSet.latticToDot(new File("lattice.dot"));
		// try (FileOutputStream fileStream = new FileOutputStream(new
		// File("prog.dot"));
		// OutputStreamWriter writer = new OutputStreamWriter(fileStream,
		// "UTF-8");) {
		// DOTExporter<String, DefaultEdge> dot = new DOTExporter<String,
		// DefaultEdge>(new StringNameProvider<String>(), null, null);
		// dot.export(writer, directedGraph);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		Assert.assertTrue(effectualSet.getEffectualSet().equals(expectedResult));
	}

	private int nodeNumberer = 0;

	private Set<String> createdNestedDiamonds(DirectedGraph<String, DefaultEdge> graph, String src, String snk,
			int depth) {
		Set<String> result = new HashSet<String>();
		final String label = String.format("%d", nodeNumberer++);
		graph.addVertex(label);
		graph.addEdge(src, label);
		if (depth > 0) {
			result.addAll(createdNestedDiamonds(graph, label, snk, depth - 1));
			result.addAll(createdNestedDiamonds(graph, label, snk, depth - 1));
		} else {
			graph.addEdge(label, snk);
			result.add(label);
		}
		return result;
	}

}
