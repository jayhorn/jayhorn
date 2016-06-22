package soottocfg.soot.memory_model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * 
 * @author rodykers
 *
 * Stores a list of necessary pack and unpack operations.
 */
public class PackingList {

	SootMethod m;
	Set<Stmt> pushes;
	Set<Stmt> pulls;
	
	/**
	 * Construct new PackingList.
	 * @param m Method which the list is for.
	 */
	public PackingList(SootMethod m) {
		this.m = m;
		this.pushes = new HashSet<Stmt>();
		this.pulls = new HashSet<Stmt>();
		buildOverestimatedLists();
	}
	
	private boolean addPush(Stmt s) {
		return pushes.add(s);
	}
	
	private boolean addPull(Stmt s) {
		return pulls.add(s);
	}
	
	private boolean addPair(Stmt s) {
		return addPush(s) && addPull(s);
	}
	
	/**
	 * Over-estimate the packing list. Do not do any aliasing analysis, but pull and push on every FieldRef.
	 */
	private void buildOverestimatedLists() {
		UnitGraph graph = new CompleteUnitGraph(m.getActiveBody());
		for (Unit u : graph) {
			Stmt s = (Stmt) u;
			
			if (s.containsFieldRef()) {
				FieldRef f = s.getFieldRef();
				
				// do not pull/push 'this' in constructor
				if (f instanceof InstanceFieldRef) {
					InstanceFieldRef ifr = (InstanceFieldRef) f;
					boolean inconstr = m.isConstructor() && ifr.getBase().equals(m.getActiveBody().getThisLocal());
					if (!inconstr)
						addPair(s);
				} else if (f instanceof StaticFieldRef) {
					// in static initializer only push at the end
					if (!m.isStaticInitializer())
						addPair(s);
				}
			}
		}
		
		// add push at the last access to 'this' for all tails in a constructor
		if (m.isConstructor()) {
			List<Unit> todo = new LinkedList<Unit>(graph.getTails());
			while (!todo.isEmpty()) {
				Unit u = todo.remove(0);
				Stmt s = (Stmt) u;
				if (s.containsFieldRef()) {
					FieldRef f = s.getFieldRef();
					if (f instanceof InstanceFieldRef) {
						InstanceFieldRef ifr = (InstanceFieldRef) f;
						if (ifr.getBase().equals(m.getActiveBody().getThisLocal())) {
							addPush(s);
//							System.out.println("Added push at end of constructor, at " + s);
							continue;
						}
					}
				}
				todo.addAll(graph.getPredsOf(u));
			}
		}

		// add push at the end of static initializer
		if (m.isStaticInitializer()) {
			List<Unit> todo = new LinkedList<Unit>(graph.getTails());
			while (!todo.isEmpty()) {
				Unit u = todo.remove(0);
				Stmt s = (Stmt) u;
				if (s.containsFieldRef()) {
					FieldRef f = s.getFieldRef();
					if (f instanceof StaticFieldRef) {
						addPush(s);
//						System.out.println("Added push at end of static initializer, at " + s);
						continue;
					}
				}
				todo.addAll(graph.getPredsOf(u));
			}
		}
	}

	/**
	 * Find out whether to pack at a FieldRef. 
	 * @param fr
	 * @return true if we should pack at fr
	 */
	public boolean pushAt(Stmt s) {
		return pushes.contains(s);
	}
	
	/**
	 * Find out whether to unpack at a FieldRef. 
	 * @param fr
	 * @return true if we should unpack at fr
	 */
	public boolean pullAt(Stmt s) {
		return pulls.contains(s);
	}
}
