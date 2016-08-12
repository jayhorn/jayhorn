/**
 * 
 */
package soottocfg.soot.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;

/**
 * @author schaef
 *
 */
public class LocalTypeFinder extends ForwardFlowAnalysis<Unit, Map<Local, Set<Type>>> {

	private final Hierarchy hierarchy;
	private final Body body;

	public LocalTypeFinder(DirectedGraph<Unit> graph, Body b) {
		super(graph);
		this.body = b;
		hierarchy = Scene.v().getActiveHierarchy();
		this.doAnalysis();
	}

	public Set<Type> getLocalTypesBefore(Unit u, Local l) {
		Set<Type> ret = new HashSet<Type>(this.getFlowBefore(u).get(l));
		if (ret.isEmpty()) {
			//can only happen if l is not initialized.
			ret.add(l.getType());
		}
		return ret;
	}

	@Override
	protected void flowThrough(Map<Local, Set<Type>> in, Unit u, Map<Local, Set<Type>> out) {
		copy(in, out);
		
		if (u instanceof DefinitionStmt) {
			DefinitionStmt ds = (DefinitionStmt) u;
			if (ds.getLeftOp() instanceof Local) {
				Local l = (Local) ds.getLeftOp();
//				out.put(l, getPossibleRhsTypes(u, ds.getRightOp(), in));
				out.get(l).addAll(getPossibleRhsTypes(u, ds.getRightOp(), in));
			}
		}
	}

	private Set<Type> getPossibleRhsTypes(Unit u, Value rhs, Map<Local, Set<Type>> in) {
		Set<Type> res = new HashSet<Type>();
		if (rhs instanceof Local) {
			// just use the set of the other local.
			if (in.get(rhs).isEmpty()) {
				res.add(rhs.getType());
			} else {
				res.addAll(in.get(rhs));
			}
		} else if (rhs instanceof AnyNewExpr) {
			// only add the type of the NewExpr.
			res.add(rhs.getType());
		} else if (rhs instanceof ThisRef) {
			// TODO: do we need to add the sub types as well?
			res.add(rhs.getType());
		} else if (rhs instanceof ParameterRef || rhs instanceof FieldRef || rhs instanceof CastExpr
				|| rhs instanceof InvokeExpr) {
			if (rhs.getType() instanceof RefType) {
				SootClass sc = ((RefType) rhs.getType()).getSootClass();
				List<SootClass> classes = null;
				if (sc.isInterface()) {
					classes = hierarchy.getImplementersOf(sc);
				} else {
					try {
						classes = hierarchy.getSubclassesOfIncluding(sc);
					} catch (Throwable e) {
						SootClass exceptionClass = Scene.v().getSootClass("java.lang.Exception");
						System.err.print("There is a bug ...");
						classes = hierarchy.getSubclassesOfIncluding(exceptionClass);
					}
				}
				for (SootClass s : classes) {
					res.add(s.getType());
				}
			} else {
				res.add(rhs.getType());
			}
		} else {
			res.add(rhs.getType());
		}
		return res;
	}

	@Override
	protected void copy(Map<Local, Set<Type>> from, Map<Local, Set<Type>> to) {		
		for (Entry<Local, Set<Type>> entry : from.entrySet()) {			
			to.put(entry.getKey(), new HashSet<Type>(entry.getValue()));	
		}
	}

	@Override
	protected void merge(Map<Local, Set<Type>> in1, Map<Local, Set<Type>> in2, Map<Local, Set<Type>> out) {
		out.clear();
		for (Entry<Local, Set<Type>> entry : in1.entrySet()) {
			out.put(entry.getKey(), new HashSet<Type>(entry.getValue()));
		}		
		for (Entry<Local, Set<Type>> entry : in2.entrySet()) {
			if (out.containsKey(entry.getKey())) {
				out.get(entry.getKey()).addAll(entry.getValue());
			} else {
				out.put(entry.getKey(), new HashSet<Type>(entry.getValue()));
			}
		}
	}

	@Override
	protected Map<Local, Set<Type>> newInitialFlow() {		
		Map<Local, Set<Type>> ret = new HashMap<Local, Set<Type>>();
		for (Local l : body.getLocals()) {
			ret.put(l, new HashSet<Type>());
		}
		return ret;
	}

}
