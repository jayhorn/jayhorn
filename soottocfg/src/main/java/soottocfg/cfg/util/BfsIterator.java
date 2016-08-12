/**
 * 
 */
package soottocfg.cfg.util;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;

import com.google.common.base.Preconditions;

/**
 * @author schaef
 *
 */
public class BfsIterator<V> implements Iterator<V> {

	private final Iterator<V> listIterator;
	private final List<V> elements;
	
	/**
	 * Requires that g has a unique source.
	 * @param g
	 */
	public BfsIterator(DirectedGraph<V, ?> g) {
		elements = createOrderedVertexList(g, GraphUtil.getSource(g));
		listIterator = elements.iterator();
	}

	public BfsIterator(DirectedGraph<V, ?> g, V startPos) {
		Preconditions.checkArgument(g.containsVertex(startPos));
		elements = createOrderedVertexList(g, startPos);
		listIterator = elements.iterator();
	}
	
	public List<V> getElements() {
		return new LinkedList<V>(elements);
	}

	private List<V> createOrderedVertexList(DirectedGraph<V, ?> graph, V start) {
		Queue<V> todo = new LinkedList<V>();
		List<V> visited = new LinkedList<V>();
		todo.add(start);
		while (!todo.isEmpty()) {
			V current = todo.poll();
			visited.add(current);
			for (V suc : Graphs.successorListOf(graph, current)) {
				if (!todo.contains(suc) && !visited.contains(suc)) {
					todo.add(suc);
				}
			}
		}
		return visited;
	}
	
	@Override
	public boolean hasNext() {
		return listIterator.hasNext();
	}

	@Override
	public V next() {
		return listIterator.next();
	}

	@Override
    public void remove() {
        throw new UnsupportedOperationException("remove");
    }
	
}
