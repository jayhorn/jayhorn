/**
 * 
 */
package jayhorn.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.Graphs;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;

/**
 * @author schaef
 *
 */
public class SourceLocationUtil {

	/**
	 * Searches for the nearest 'SourceLocation' starting from 'b' and going
	 * backwards.
	 * 
	 * @param m
	 *            The method to search in.
	 * @param b
	 *            The block to start searching from.
	 * @return
	 */
	public static SourceLocation findNearestLocationBackwards(Method m, CfgBlock b) {
		Queue<CfgBlock> todo = new LinkedList<CfgBlock>();
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		todo.add(b);
		while (!todo.isEmpty()) {
			CfgBlock current = todo.poll();
			done.add(current);
			ListIterator<Statement> iter = current.getStatements().listIterator(current.getStatements().size());
			while (iter.hasPrevious()) {
				Statement s = iter.previous();
				if (s.getSourceLocation() != null) {
					return s.getSourceLocation();
				}
			}
			for (CfgBlock pre : Graphs.predecessorListOf(m, current)) {
				if (!todo.contains(pre) && !done.contains(pre)) {
					todo.add(pre);
				}
			}
		}
		return null;
	}

	/**
	 * Searches for the nearest 'SourceLocation' starting from 'b' and going
	 * backwards.
	 * 
	 * @param m
	 *            The method to search in.
	 * @param b
	 *            The block to start searching from.
	 * @return
	 */
	public static SourceLocation findNearestLocationForward(Method m, CfgBlock b) {
		Queue<CfgBlock> todo = new LinkedList<CfgBlock>();
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		todo.add(b);
		while (!todo.isEmpty()) {
			CfgBlock current = todo.poll();
			done.add(current);
			ListIterator<Statement> iter = current.getStatements().listIterator();
			while (iter.hasNext()) {
				Statement s = iter.next();
				if (s.getSourceLocation() != null) {
					return s.getSourceLocation();
				}
			}
			for (CfgBlock suc : Graphs.successorListOf(m, current)) {
				if (!todo.contains(suc) && !done.contains(suc)) {
					todo.add(suc);
				}
			}
		}
		return null;
	}
}
