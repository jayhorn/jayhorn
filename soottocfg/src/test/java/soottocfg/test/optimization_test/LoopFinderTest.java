package soottocfg.test.optimization_test;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.Test;

import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.LoopFinder;

public class LoopFinderTest {

	final DirectedGraph<String, DefaultEdge> graph;
	final Dominators<String> dom;
	final LoopFinder<String> loopFinder;
	
    public LoopFinderTest() {
		/**
		 * Example from Modern Compiler Implementation in Java Fig 18.3a page 381
		 */
		graph = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);
		for (int i=1;i<=12;i++) {
			graph.addVertex(String.valueOf(i));
		}
		graph.addEdge("1", "2");
		
		graph.addEdge("2", "3");
		graph.addEdge("3", "2");

		graph.addEdge("2", "4");
		graph.addEdge("4", "2");
		graph.addEdge("4", "5");
		graph.addEdge("4", "6");
		
		graph.addEdge("6", "7");
		graph.addEdge("7", "11");
		graph.addEdge("11", "12");
		
		graph.addEdge("5", "7");
		graph.addEdge("5", "8");
		
		graph.addEdge("8", "9");
		graph.addEdge("9", "8");
		
		graph.addEdge("9", "10");
		graph.addEdge("10", "5");
		graph.addEdge("10", "12");
		
		dom = new Dominators<String>(graph, "1");
		loopFinder = new LoopFinder<String>(dom);    
	}
	
    @Test
    public void testHeaders() {
		Set<String> expectedHeaders = new HashSet<String>();
		expectedHeaders.add("2");
		expectedHeaders.add("5");
		expectedHeaders.add("8");
		Set<String> headers = loopFinder.getLoopHeaders();
		Assert.assertTrue(headers.equals(expectedHeaders));	
    }
    
    @Test
    public void testLoopFrom2() {
		Set<String> expectedLoopFrom2 = new HashSet<String>();
		expectedLoopFrom2.add("2");
		expectedLoopFrom2.add("3");
		expectedLoopFrom2.add("4");
		Set<String> loopFrom2 = loopFinder.getLoopBody("2");
		Assert.assertTrue(loopFrom2.equals(expectedLoopFrom2));	
    }
    
    @Test
    public void testLoopFrom5() {
		Set<String> expectedLoopFrom5 = new HashSet<String>();
		expectedLoopFrom5.add("5");
		expectedLoopFrom5.add("8");
		expectedLoopFrom5.add("9");
		expectedLoopFrom5.add("10");
		Set<String> loopFrom5 = loopFinder.getLoopBody("5");
		Assert.assertTrue(loopFrom5.equals(expectedLoopFrom5));
    }

    @Test
    public void testLoopFrom8() {
		Set<String> expectedLoopFrom8 = new HashSet<String>();
		expectedLoopFrom8.add("8");
		expectedLoopFrom8.add("9");
		Set<String> loopFrom8 = loopFinder.getLoopBody("8");
		Assert.assertTrue(loopFrom8.equals(expectedLoopFrom8));
	}
    
    @Test 
    public void testLoopNestTree() {
    	TreeSet<String> lnt = loopFinder.getLoopNestTreeSet();

    	Assert.assertTrue(lnt.lower("5").equals("8"));
    	Assert.assertTrue(lnt.higher("8").equals("5"));
    	Assert.assertTrue(lnt.higher("2")==null);

//    	while (!lnt.isEmpty()) {
//		System.err.println(lnt.pollFirst());
    	//TODO more testing needed.
    }
		
}
