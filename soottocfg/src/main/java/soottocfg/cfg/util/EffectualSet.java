/**
 * 
 */
package soottocfg.cfg.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Verify;

/**
 * @author schaef 
 * 
 * Given a directed graph with unique source and unique sink,
 *         perform the following steps: 1) Compute the set of 'inevitable'
 *         vertices for each vertex. Here, a vertex A is inevitable for a vertex
 *         B if any complete path through B must contain A as well. I.e., the
 *         set of 'inevitable' vertices is the union of the dominators and
 *         post-dominators of a vertex.
 * 
 *         2) Group the vertices into equivalence classes, s.t. two vertices are
 *         equivalent iff they have the same set of 'inevitable' vertices, I. We
 *         define a partial order over these equivalence classes: given two
 *         equivalence classes, E1 and E2, with the sets in inevitable vertices
 *         I1 and I2, then E1 <= E2 iff I1 \subseteq I2. From this partial
 *         order, we generate a lattice.
 * 
 *         3) Using the lattice, we compute an 'effectual' set. Which is a
 *         minimal set of nodes that need to be covered to obtain a complete
 *         cover of the original graph. The effectual set is obtained by
 *         selecting one element (non-deterministically) from each equivalence
 *         class E that is maximal wrt our partial order.
 * 
 *         Example:
 * 
 *         TODO
 * 
 * 
 */
public class EffectualSet<A> {

	private final Map<A, Set<A>> inevitable;
	private final DirectedGraph<Set<A>, DefaultEdge> lattice;
	private final Set<A> effectualSet;
	private final Dominators<A> dominators;
	private final PostDominators<A> postDominators;

	/**
	 * 
	 */
	public EffectualSet(Dominators<A> dominators, PostDominators<A> postDominators) {
		this.dominators = dominators;
		this.postDominators = postDominators;
		Map<A, Set<A>> dom = dominators.getDominators();
		Map<A, Set<A>> pdom = postDominators.getDominators();
		inevitable = new HashMap<A, Set<A>>(dom);
		for (Entry<A, Set<A>> entry : pdom.entrySet()) {
			inevitable.get(entry.getKey()).addAll(entry.getValue());
		}

		lattice = buildLattice();
		effectualSet = new HashSet<A>();
		for (Set<A> v : lattice.vertexSet()) {
			if (lattice.outDegreeOf(v) == 0) {
				Verify.verify(!v.isEmpty(), "Lattice construction failed.");
				effectualSet.add(v.iterator().next());
			}
		}
	}

	/**
	 * Returns the lattice of the partial order defined by subset relation
	 * over the set of complete paths that go through a vertex.
	 * That is, two vertices are in the same set in the lattice if they share
	 * the exact same set of complete paths. For example, if a graph has a
	 * unique source and a unique sink, both vertices share the same set of
	 * complete paths. If there is a diamond in the graph, the vertices before
	 * and after the diamond share the same set of complete paths while the
	 * vertices on either side of the diamond share a subset of paths with the
	 * vertices before and after the diamond (i.e., all but those that go through
	 * the other side of the diamond).
	 * 
	 * @return
	 */
	public DirectedGraph<Set<A>, DefaultEdge> getLattice() {
		return lattice;
	}

	/**
	 * Get the dominators that were used to build the effectual set.
	 * @return
	 */
	public Dominators<A> getDominators() {
		return dominators;
	}

	/**
	 * Get the post dominators that were used to build the effectual set.
	 * @return
	 */
	public PostDominators<A> getPostDominators() {
		return postDominators;
	}

	
	/**
	 * Compute an effectual set for the graph. I.e., a minimal set of vertices
	 * that need to be covered to obtain a path cover of the graph.
	 * 
	 * @see http://rd.springer.com/chapter/10.1007%2F978-3-642-27705-4_24#page-1
	 * @return
	 */
	public Set<A> getEffectualSet() {
		return effectualSet;
	}

	/**
	 * Finds the vertex 'b' in the lattice and returns the set of blocks
	 * that share the same set of traces.
	 * @param b
	 * @return
	 */
	public Set<A> findInLattice(A b) {
		BfsIterator<Set<A>> iter = new BfsIterator<Set<A>>(lattice);
		while (iter.hasNext()) {
			Set<A> current = iter.next();
			if (current.contains(b)) {
				return current;
			}
		}	
		throw new RuntimeException("Not found!");
	}

	/**
	 * Check if 'b' is in a lattice element below 'latticeElement'.
	 * @param b
	 * @param latticeElement
	 * @return
	 */
	public boolean isBelowInLattice(A b, Set<A> latticeElement) {
		if (latticeElement.contains(b))
			return false;
		Queue<Set<A>> todo = new LinkedList<Set<A>>(Graphs.successorListOf(lattice, latticeElement));
		Set<Set<A>> done = new HashSet<Set<A>>();
		while (!todo.isEmpty()) {
			Set<A> current = todo.poll();
			if (current.contains(b)) {
				return true;
			}
			done.add(current);
			for (Set<A> next : Graphs.successorListOf(lattice, current)) {
				if (!todo.contains(next) && !done.contains(next)) {
					todo.add(next);
				}
			}
		}
		return false;
	}

	/**
	 * Check if 'b' is in a lattice element above 'latticeElement'.
	 * @param b
	 * @param latticeElement
	 * @return
	 */
	public boolean isAboveInLattice(A b, Set<A> latticeElement) {
		if (latticeElement.contains(b))
			return false;
		Queue<Set<A>> todo = new LinkedList<Set<A>>(Graphs.predecessorListOf(lattice, latticeElement));
		Set<Set<A>> done = new HashSet<Set<A>>();
		while (!todo.isEmpty()) {
			Set<A> current = todo.poll();
			if (current.contains(b)) {
				return true;
			}
			done.add(current);
			for (Set<A> next : Graphs.predecessorListOf(lattice, current)) {
				if (!todo.contains(next) && !done.contains(next)) {
					todo.add(next);
				}
			}
		}
		return false;
	}	
	
	private DirectedGraph<Set<A>, DefaultEdge> buildLattice() {
		DirectedGraph<Set<A>, DefaultEdge> lattice = new DefaultDirectedGraph<Set<A>, DefaultEdge>(DefaultEdge.class);
		Set<EquivalentVertices<A>> poset = new HashSet<EquivalentVertices<A>>();
		/*
		 * Group vertices into equivalence classes using the equivalence
		 * relation that two vertices are equivalent iff they have the same set
		 * of inevitable vertices.
		 */
		for (Entry<A, Set<A>> entry : inevitable.entrySet()) {
			/*
			 * Check if another POElement exists that has the same inevitable
			 * vertices.
			 */
			EquivalentVertices<A> found = null;
			for (EquivalentVertices<A> poe : poset) {
				if (poe.inevitableVertices.equals(entry.getValue())) {
					found = poe;
					break;
				}
			}
			/*
			 * If a POElement with the same inevitable vertices exists, add the
			 * current vertex the this POElement (which is the equivalence class
			 * of all vertices that have the same set of inevitable vertices.
			 */
			if (found != null) {
				found.equivalentVertices.add(entry.getKey());
			} else {
				poset.add(new EquivalentVertices<A>(entry.getKey(), entry.getValue()));
			}
		}

		Map<EquivalentVertices<A>, Set<EquivalentVertices<A>>> successors = new HashMap<EquivalentVertices<A>, Set<EquivalentVertices<A>>>();
		for (EquivalentVertices<A> x : poset) {
			successors.put(x, findImmediateSuccessorsOf(x, poset));
		}

		// Add all vertices to the lattice first
		for (EquivalentVertices<A> x : poset) {
			lattice.addVertex(x.equivalentVertices);
		}

		// Add all edges for the lattice.
		for (EquivalentVertices<A> x : poset) {
			for (EquivalentVertices<A> succ : successors.get(x)) {
				lattice.addEdge(x.equivalentVertices, succ.equivalentVertices);
			}
		}
		return lattice;
	}

	/**
	 * Find all immediate successors of el in elements using the partial order
	 * defined by el.lessThan. Probably not the most efficient way of doing
	 * this, but it works.
	 * 
	 * @param el
	 * @param elements
	 * @return
	 */
	private Set<EquivalentVertices<A>> findImmediateSuccessorsOf(EquivalentVertices<A> el,
			Set<EquivalentVertices<A>> elements) {
		Set<EquivalentVertices<A>> res = new HashSet<EquivalentVertices<A>>();
		for (EquivalentVertices<A> x : elements) {
			if (x == el)
				continue;
			if (el.lessThan(x)) {
				// check if we have something better already
				// or if this entry replaces others that are worse.
				boolean subsumed = false;
				Set<EquivalentVertices<A>> subsumes = new HashSet<EquivalentVertices<A>>();
				for (EquivalentVertices<A> y : res) {
					if (y.lessThan(x)) {
						subsumed = true;
						break;
					}
					if (x.lessThan(y)) {
						subsumes.add(y);
					}
				}
				if (!subsumed) {
					res.removeAll(subsumes);
					res.add(x);
				}
			}
		}

		return res;
	}

	public static class EquivalentVertices<A> {

		public final Set<A> equivalentVertices;
		public final Set<A> inevitableVertices;

		public EquivalentVertices(A eq, Set<A> vertices) {
			this.equivalentVertices = new HashSet<A>();
			this.equivalentVertices.add(eq);
			this.inevitableVertices = vertices;
		}

		public EquivalentVertices(Set<A> equiv, Set<A> vertices) {
			this.equivalentVertices = equiv;
			this.inevitableVertices = vertices;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\"");
			sb.append("Equivalent blocks " + this.equivalentVertices.size());
			sb.append(", inevitable blocks " + this.inevitableVertices.size());
			sb.append("\"");
			return sb.toString();
		}

		public boolean lessThan(EquivalentVertices<A> other) {
			if (other == null) {
				throw new NullPointerException();
			}
			return other.inevitableVertices.containsAll(this.inevitableVertices);
		}
	}

	public void latticToDot(File dotFile) {
		try (FileOutputStream fileStream = new FileOutputStream(dotFile);
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");) {
			DOTExporter<Set<A>, DefaultEdge> dot = new DOTExporter<Set<A>, DefaultEdge>(
					new StringNameProvider<Set<A>>() {
						@Override
						public String getVertexName(Set<A> vertex) {
							StringBuilder sb = new StringBuilder();
							sb.append("\"{");
							String comma = "";
							for (A s : vertex) {
								sb.append(comma);
								sb.append(s.toString());
								comma = ", ";
							}
							sb.append("}\"");
							return sb.toString();
						}
					}, null, null);
			dot.export(writer, lattice);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
