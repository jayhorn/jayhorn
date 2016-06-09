package soottocfg.soot.memory_model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
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
	HashMap<SootClass,List<PackUnpackPair>> lists;
	
	/**
	 * Construct new PackingList.
	 * @param m Method which the list is for.
	 */
	public PackingList(SootMethod m) {
		this.m = m;
		this.lists = new HashMap<SootClass,List<PackUnpackPair>>();
		buildOverestimatedLists();
		int merged = minimize();
		if (merged>0)
			System.out.println("Minimization step removed " + merged + " pack-unpack pairs in " + m.getName());
	}
	
	private boolean addPair(PackUnpackPair pup) {
		List<PackUnpackPair> list = lists.get(pup.f.getDeclaringClass());
		if (list==null) {
			list = new LinkedList<PackUnpackPair>();
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
			
			// test code for lengthof expr
//			List<ValueBox> vbs = u.getUseBoxes();
//			for (ValueBox vb : vbs) {
//				Value v = vb.getValue();
//				if (s.toString().contains("lengthof")) {
//					System.out.println("CLASS" + v.getClass());
//				}
//				
//			}
			
			if (s.containsFieldRef()) {
				FieldRef f = s.getFieldRef();
				if (f instanceof InstanceFieldRef) {
					InstanceFieldRef ifr = (InstanceFieldRef) f;
					if (!(m.isConstructor() && ifr.getBase().equals(m.getActiveBody().getThisLocal()))) { // do not pack/unpack 'this' in constructor
						PackUnpackPair pup = new PackUnpackPair(f,f);
						addPair(pup);
						//System.out.println("Added pack/unpack pair at " + s);
					}
				} // else ignore (static field ref)
			}
		}
		
		// add pack at the last access to 'this' for all tails in a constructor
		if (m.isConstructor()) {
			List<Unit> todo = new LinkedList<Unit>(graph.getTails());
			while (!todo.isEmpty()) {
				Unit u = todo.remove(0);;
				Stmt s = (Stmt) u;
				if (s.containsFieldRef()) {
					FieldRef f = s.getFieldRef();
					if (f instanceof InstanceFieldRef) {
						InstanceFieldRef ifr = (InstanceFieldRef) f;
						if (ifr.getBase().equals(m.getActiveBody().getThisLocal())) {
							if (!packAt(f)) {
								PackUnpackPair pup = new PackUnpackPair(f,null);
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
	}

	/**
	 * Minimize the packing list with respect to an alias analysis.
	 * @return the number of removed pairs.
	 */
	// TODO use intelligent points-to-analysis
	private int minimize() {
		
		// count merged pairs
		int count = 0;
		
		// get points-to-analysis
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
//		GeomPointsTo geomPTA = (GeomPointsTo) pta;
		
		// traverse UnitGraph
		UnitGraph graph = new CompleteUnitGraph(m.getActiveBody());
		List<Unit> heads = graph.getHeads();
		Set<PackUnpackPair> open = new HashSet<PackUnpackPair>();
		for (Unit head : heads) {			
			List<Unit> todo = new LinkedList<Unit>(graph.getSuccsOf(head));
			Set<Unit> done = new HashSet<Unit>();
			while (!todo.isEmpty()) {
				Unit current = todo.remove(0);
				Stmt s = (Stmt)current;
				if (s.containsFieldRef()) {
					FieldRef fr = s.getFieldRef();
					boolean merged = false;
					PackUnpackPair justAdded = null;
					
					// if minimization may be possible
					if (unpackAt(fr) && lists.get(fr.getField().getDeclaringClass()).size() > 1) {
						
						// find the pair
findloop:				for (PackUnpackPair pup : lists.get(fr.getField().getDeclaringClass())) {
							if (pup.unpackAt==fr) {
								// if in list of currently unpacked fields, merge PackUnpackPairs
								for (PackUnpackPair pup2 : open) {
									if (pup2.unpackAt.getField().getDeclaringClass().equals(fr.getField().getDeclaringClass())) {
										// check  if variable names are equal, otherwise should not merge
										boolean sameVar = true;
										List<ValueBox> vbs1 = pup.packAt.getUseBoxes();
										List<ValueBox> vbs2 = pup2.packAt.getUseBoxes();
										for (ValueBox vb1 : vbs1) {
											for (ValueBox vb2 : vbs2) {
												if(!vb1.getValue().equals(vb2.getValue()))
													sameVar = false;
											}
										}

										if (sameVar) {
											//merge
											pup2.packAt = fr;
											lists.get(fr.getField().getDeclaringClass()).remove(pup);
											count++;
											merged = true;
											//System.out.println("MERGE! Pack at " + pup2.packAt + " unpack at " + pup2.unpackAt);
											break findloop;
										}
									}
								}
								// not found -> add to list to minimize
								open.add(pup);
								justAdded=pup;
								break findloop;
							}
						}
					}
					
					// use points to analysis to check which objects may remain unpacked
					if (!merged) {
						PointsToSet pointsTo = pta.reachingObjects(fr.getField());
						Set<PackUnpackPair> toRemove = new HashSet<PackUnpackPair>();
						for (PackUnpackPair pup : open) {
							if (pup != justAdded) {
								PointsToSet pointsTo2 = pta.reachingObjects(pup.packAt.getField());
								if (pointsTo.hasNonEmptyIntersection(pointsTo2)) {
									//System.out.println(fr.getField() + " may point to same location as " + pup.packAt.getField());
									toRemove.add(pup);
								}
							}
						}
						open.removeAll(toRemove);
					}
				}

				// use points to analysis to check which objects may remain unpacked
				// TODO it seems this captures the same locations as above, so probably not needed
//				for (ValueBox vb : s.getUseAndDefBoxes()) {
//					Value v = vb.getValue();
//					if (v instanceof Local) {
////						System.out.println("LOCAL FOUND: " + v);
//						PointsToSet pointsTo = pta.reachingObjects((Local) v);
//						for (PackUnpackPair pup : open) {
//							PointsToSet pointsTo2 = pta.reachingObjects(pup.packAt.getField());
//							if (pointsTo.hasNonEmptyIntersection(pointsTo2)) {
//								System.out.println("May point to the same location as " + pup.packAt.getField());
//								open.remove(pup);
//							}
//						}
//					}
//				}
//				
//				// TODO not sure if necessary, can array values point to the same memory location as fields in Jimple?
//				if (s.containsArrayRef()) {
//					ArrayRef ar = s.getArrayRef();
//					for (ValueBox vb : ar.getUseBoxes()) {
//						Value v = vb.getValue();
//						if (v instanceof Local) {
//							System.out.println("AN ARRAY VALUE CAN BE A LOCAL !!!! " + v);
//							// looks like this is not needed
//						}
//					}
//				}
				
				done.add(current);
				for (Unit next : graph.getSuccsOf(current)) {
					if (!todo.contains(next) && !done.contains(next)) {
						todo.add(next);
					}
				}
			}
		}
		
		return count;
	}
	
	/**
	 * Find out whether to pack at a FieldRef. 
	 * @param fr
	 * @return true if we should pack at fr
	 */
	public boolean packAt(FieldRef fr) {
		List<PackUnpackPair> list = lists.get(fr.getField().getDeclaringClass());
		if (list != null) {
			for (PackUnpackPair pup : list) {
				if (pup.packAt==fr)
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
	public boolean unpackAt(FieldRef fr) {
		List<PackUnpackPair> list = lists.get(fr.getField().getDeclaringClass());
		if (list != null) {
			for (PackUnpackPair pup : list) {
				if (pup.unpackAt==fr)
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
	static private class PackUnpackPair {
		SootField f;
		FieldRef packAt;
		FieldRef unpackAt;

		PackUnpackPair(FieldRef packAt, FieldRef unpackAt) {
//			assert(unpackAt==null || packAt.getField()==unpackAt.getField());
			this.f = packAt.getField();
			this.packAt = packAt;
			this.unpackAt = unpackAt;
		}
	}
}
