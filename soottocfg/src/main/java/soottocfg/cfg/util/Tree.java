/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	private boolean isDirty = false;
	private V root = null;
	private Set<V> leaves = new HashSet<V>();

	public Tree() {
		tree = new DefaultDirectedGraph<V, DefaultEdge>(DefaultEdge.class);
	}

	public void addEdge(V parent, V child) {
		tree.addVertex(parent);
		tree.addVertex(child);
		Verify.verify(tree.inDegreeOf(child)==0, "Node already has a parent: "+child);
		tree.addEdge(parent, child);
		isDirty = true;
	}

	public V getRoot() {
		recomputeRootAndLeavesIfDirty();
		return root;
	}

	public V getParentOf(V node) {
		List<V> pred = Graphs.predecessorListOf(tree, node);
		Verify.verify(pred.size()<=1);
		if (pred.isEmpty()) return null;
		return pred.get(0);
	}
	
	public boolean isAncestor(V node, V ancestor) {
		if (node.equals(ancestor)) {
			return true;
		}
		V parent = getParentOf(node);
		if (parent==null) {
			return false;
		}
		return isAncestor(parent, ancestor);
	}
	
	public boolean isDescendant(V node, V descendant) {
		if (node.equals(descendant)) {
			return true;
		}
		for (V c: getChildrenOf(node)) {
			if (isDescendant(c, descendant)) {
				return true;
			}
		}
		return false;
	}
	
	public List<V> getChildrenOf(V node) {
		return Graphs.successorListOf(tree, node);
	}
	
	public Set<V> getLeaves() {
		recomputeRootAndLeavesIfDirty();
		return leaves;
	}
	
	private void recomputeRootAndLeavesIfDirty() {
		if (isDirty) {
			root = null;
			leaves = new HashSet<V>();
			isDirty = false;
			for (V vertex : tree.vertexSet()) {
				if (tree.inDegreeOf(vertex)==0) {
					Verify.verify(root==null, "More than one root in tree.");
					root = vertex;
				}
				if (tree.outDegreeOf(vertex)==0) {
					leaves.add(vertex);
				}

			}
		}
	}
	
}
