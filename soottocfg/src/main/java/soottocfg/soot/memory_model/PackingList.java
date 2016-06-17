package soottocfg.soot.memory_model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import soot.SootClass;
import soot.SootField;
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
	HashMap<SootClass,List<PushPullPair>> lists;
	
	/**
	 * Construct new PackingList.
	 * @param m Method which the list is for.
	 */
	public PackingList(SootMethod m) {
		this.m = m;
		this.lists = new HashMap<SootClass,List<PushPullPair>>();
		buildOverestimatedLists();
	}
	
	private boolean addPair(PushPullPair pup) {
		List<PushPullPair> list = lists.get(pup.f.getDeclaringClass());
		if (list==null) {
			list = new LinkedList<PushPullPair>();
			lists.put(pup.f.getDeclaringClass(),list);
		}
		return list.add(pup);
	}
	
	/**
	 * Over-estimate the packing list. Do not do any aliasing analysis, but unpack and pack on every FieldRef.
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
					if (!inconstr) { 
						PushPullPair pup = new PushPullPair(f,f);
						addPair(pup);
					} 
				} else if (f instanceof StaticFieldRef) {
					// in static initializer only push at the end
					if (!m.isStaticInitializer()) {
						PushPullPair pup = new PushPullPair(f,f);
						addPair(pup);
					}
				}
			}
		}
		
		// add pack at the last access to 'this' for all tails in a constructor
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
							if (!pushAt(f)) {
								PushPullPair pup = new PushPullPair(f,null);
								addPair(pup);
//								System.out.println("Added pack at end of constructor, at " + s);
							}
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
						if (!pushAt(f)) {
							PushPullPair pup = new PushPullPair(f,null);
							addPair(pup);
//							System.out.println("Added push at end of static initializer, at " + s);
						}
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
	public boolean pushAt(FieldRef fr) {
		List<PushPullPair> list = lists.get(fr.getField().getDeclaringClass());
		if (list != null) {
			for (PushPullPair pup : list) {
				if (pup.pushAt==fr)
					return true;
			}
		}
		return false;
	}
	
	/**
	 * Find out whether to unpack at a FieldRef. 
	 * @param fr
	 * @return true if we should unpack at fr
	 */
	public boolean pullAt(FieldRef fr) {
		List<PushPullPair> list = lists.get(fr.getField().getDeclaringClass());
		if (list != null) {
			for (PushPullPair pup : list) {
				if (pup.pullAt==fr)
					return true;
			}
		}
		return false;
	}

	/**
	 * Stores a pair of pack and unpack operations.
	 * @author rodykers
	 *
	 */
	static private class PushPullPair {
		SootField f;
		FieldRef pushAt;
		FieldRef pullAt;

		PushPullPair(FieldRef pushAt, FieldRef pullAt) {
			this.f = pushAt.getField();
			this.pushAt = pushAt;
			this.pullAt = pullAt;
		}
	}
}
