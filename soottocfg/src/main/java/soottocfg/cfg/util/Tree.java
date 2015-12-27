/**
 * 
 */
package soottocfg.cfg.util;

import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Verify;

/**
 * @author schaef
 *
 */
public class Tree<V> {

	private final DefaultDirectedGraph<V, DefaultEdge> tree;

	private boolean rootIsDirty = false;
	private V root = null;	

	public Tree() {
		tree = new DefaultDirectedGraph<V, DefaultEdge>(DefaultEdge.class);
	}

	public void addEdge(V parent, V child) {
		tree.addVertex(parent);
		tree.addVertex(child);
		Verify.verify(tree.inDegreeOf(child)==0, "Node already has a parent: "+child);
		tree.addEdge(parent, child);
		rootIsDirty = true;
	}

	public V getRoot() {
		if (rootIsDirty) {
			root = null;
			rootIsDirty = false;
			for (V vertex : tree.vertexSet()) {
				if (tree.inDegreeOf(vertex)==0) {
					Verify.verify(root==null, "More than one root in tree.");
					root = vertex;
				}
			}
		}
		return root;
	}

	public V getParentOf(V node) {
		List<V> pred = Graphs.predecessorListOf(tree, node);
		Verify.verify(pred.size()<=1);
		if (pred.isEmpty()) return null;
		return pred.get(0);
	}
	
	public List<V> getChildrenOf(V node) {
		return Graphs.successorListOf(tree, node);
	}
	
}
