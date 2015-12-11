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

	public final Map<Unit, Map<Local, Set<Type>>> localTypes;

	private final Hierarchy hierarchy;

	public LocalTypeFinder(DirectedGraph<Unit> graph) {
		super(graph);
		localTypes = new HashMap<Unit, Map<Local, Set<Type>>>();
		hierarchy = Scene.v().getActiveHierarchy();
		this.doAnalysis();
	}

	public Set<Type> getLocalTypesBefore(Unit u, Local l) {
		return localTypes.get(u).get(l);
	}

	@Override
	protected void flowThrough(Map<Local, Set<Type>> in, Unit u, Map<Local, Set<Type>> out) {
		if (!localTypes.containsKey(u)) {
			localTypes.put(u, new HashMap<Local, Set<Type>>());
		}
		Map<Local, Set<Type>> current = localTypes.get(u);
		for (Entry<Local, Set<Type>> entry : in.entrySet()) {
			if (!current.containsKey(entry.getKey())) {
				current.put(entry.getKey(), new HashSet<Type>(entry.getValue()));
			} else {
				current.get(entry.getKey()).addAll(entry.getValue());
			}
		}

		copy(in, out);
		if (u instanceof DefinitionStmt) {
			DefinitionStmt ds = (DefinitionStmt) u;
			if (ds.getLeftOp() instanceof Local) {
				Local l = (Local) ds.getLeftOp();
				out.put(l, getPossibleRhsTypes(u, ds.getRightOp(), in));
			}
		}
	}

	private Set<Type> getPossibleRhsTypes(Unit u, Value rhs, Map<Local, Set<Type>> in) {
		Set<Type> res = new HashSet<Type>();
		if (rhs instanceof Local) {
			// just use the set of the other local.
			res.addAll(in.get(rhs));
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
			// System.err.println("**** "+rhs.getClass());
			res.add(rhs.getType());
		}
		return res;
	}

	@Override
	protected void copy(Map<Local, Set<Type>> from, Map<Local, Set<Type>> to) {
		to.clear();
		for (Entry<Local, Set<Type>> entry : from.entrySet()) {
			to.put(entry.getKey(), new HashSet<Type>(entry.getValue()));
		}
	}

	@Override
	protected void merge(Map<Local, Set<Type>> in1, Map<Local, Set<Type>> in2, Map<Local, Set<Type>> out) {
		out.clear();
		out.putAll(in1);
		for (Entry<Local, Set<Type>> entry : in2.entrySet()) {
			if (out.containsKey(entry.getKey())) {
				out.get(entry.getKey()).addAll(entry.getValue());
			} else {
				out.put(entry.getKey(), entry.getValue());
			}
		}
	}

	@Override
	protected Map<Local, Set<Type>> newInitialFlow() {
		return new HashMap<Local, Set<Type>>();
	}

}
