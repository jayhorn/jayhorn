/**
 * 
 */
package soottocfg.test.optimization_test;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;

import soottocfg.cfg.util.DominanceFrontier;
import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.Tree;

/**
 * @author schaef
 *
 */
public class DominatorTest {

	/**
	 * 
	 */
	public DominatorTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void irreducibleTest() {
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);		
		directedGraph.addVertex("1");// src
		directedGraph.addVertex("2"); 
		directedGraph.addVertex("3"); 
		directedGraph.addVertex("4");
		directedGraph.addVertex("5"); 
		directedGraph.addVertex("6"); //sink

		directedGraph.addEdge("1", "2");//makes it irreducible
		directedGraph.addEdge("1", "3");
		directedGraph.addEdge("2", "4");
		directedGraph.addEdge("3", "4");
		directedGraph.addEdge("4", "5");

		directedGraph.addEdge("5", "2");
		
		directedGraph.addEdge("3", "6");
		
		Assert.assertFalse(GraphUtil.isReducibleGraph(directedGraph, "1"));
		directedGraph.removeEdge("1", "2");
		Assert.assertTrue(GraphUtil.isReducibleGraph(directedGraph, "1"));
	}
	
	
	@Test
	public void testTextbookExample() {
		// create a graph with two diamonds.
		DirectedGraph<String, DefaultEdge> directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);		
		directedGraph.addVertex("1");// src
		directedGraph.addVertex("2"); 
		directedGraph.addVertex("3"); 
		directedGraph.addVertex("4");
		directedGraph.addVertex("5"); 
		directedGraph.addVertex("6"); 
		directedGraph.addVertex("7");
		
		directedGraph.addEdge("1", "2");
		directedGraph.addEdge("2", "3");
		directedGraph.addEdge("2", "4");
		
		directedGraph.addEdge("3", "5");
		directedGraph.addEdge("3", "6");

		directedGraph.addEdge("5", "7");
		
		directedGraph.addEdge("6", "7");
		
		directedGraph.addEdge("7", "2");
		
		Dominators<String> dom = new Dominators<String>(directedGraph, "1");
		Tree<String> domTree = dom.getDominatorTree();
		Assert.assertTrue(domTree.getRoot().equals("1"));
		Assert.assertTrue(domTree.getChildrenOf("1").size()==1 && domTree.getChildrenOf("1").iterator().next().equals("2"));
		Assert.assertTrue(domTree.getChildrenOf("2").size()==2 && domTree.getChildrenOf("2").contains("3") && domTree.getChildrenOf("2").contains("4"));
		Assert.assertTrue(domTree.getChildrenOf("3").size()==3 && domTree.getChildrenOf("3").contains("5") && domTree.getChildrenOf("3").contains("7")&& domTree.getChildrenOf("3").contains("6"));
	
		DominanceFrontier<String> df = new DominanceFrontier<String>(dom);
		Assert.assertTrue(df.getDominanceFrontier().get("1").isEmpty());
		Assert.assertTrue(df.getDominanceFrontier().get("2").size()==1 && df.getDominanceFrontier().get("2").iterator().next().equals("2"));
		Assert.assertTrue(df.getDominanceFrontier().get("3").size()==1 && df.getDominanceFrontier().get("3").iterator().next().equals("2"));
		Assert.assertTrue(df.getDominanceFrontier().get("4").isEmpty());
		Assert.assertTrue(df.getDominanceFrontier().get("5").size()==1 && df.getDominanceFrontier().get("5").iterator().next().equals("7"));
		Assert.assertTrue(df.getDominanceFrontier().get("6").size()==1 && df.getDominanceFrontier().get("6").iterator().next().equals("7"));
		Assert.assertTrue(df.getDominanceFrontier().get("7").size()==1 && df.getDominanceFrontier().get("7").iterator().next().equals("2"));
	}
}
