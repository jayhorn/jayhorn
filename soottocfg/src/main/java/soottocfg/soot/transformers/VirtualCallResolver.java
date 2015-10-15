/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.Hierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.tagkit.Host;
import soot.toolkits.graph.CompleteUnitGraph;
import soottocfg.soot.util.LocalTypeFinder;
import soottocfg.util.Pair;

/**
 * @author schaef
 *
 */
public class VirtualCallResolver extends AbstractTransformer {

	private final Hierarchy hierarchy;
	

	private LocalTypeFinder ltf;
	
	/**
	 * 
	 */
	public VirtualCallResolver() {
		hierarchy = Scene.v().getActiveHierarchy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.BodyTransformer#internalTransform(soot.Body, java.lang.String,
	 * java.util.Map)
	 */
	@Override
	protected void internalTransform(Body body, String arg1, Map<String, String> arg2) {
		
		ltf = new LocalTypeFinder(new CompleteUnitGraph(body));
		
		Map<Unit, Pair<InstanceInvokeExpr, List<SootMethod>>> callsToResolve = new HashMap<Unit, Pair<InstanceInvokeExpr, List<SootMethod>>>();

		for (Unit u : body.getUnits()) {
			Stmt s = (Stmt) u;
			if (s.containsInvokeExpr()) {
				InvokeExpr ie = s.getInvokeExpr();
				if (ie instanceof InstanceInvokeExpr) {
					List<SootMethod> callees = getPossibleCallees(body, s, (InstanceInvokeExpr) ie);
					if (callees.isEmpty()) {
						throw new RuntimeException("Failed to resolve virutal call " + ie);
					} else if (callees.size() == 1) {
						// nothing to do.
					} else {
						callsToResolve.put(s,
								new Pair<InstanceInvokeExpr, List<SootMethod>>((InstanceInvokeExpr) ie, callees));
					}
				}
			}
		}

		for (Entry<Unit, Pair<InstanceInvokeExpr, List<SootMethod>>> entry : callsToResolve.entrySet()) {			
			Unit originalCall = entry.getKey();
			InstanceInvokeExpr ivk = entry.getValue().getFirst();
			List<SootMethod> callees = entry.getValue().getSecond();
			
//			System.err.println(originalCall + " may have base");
//			if (ivk.getBase() instanceof Local) {
//				for (Type t : ltf.getLocalTypesBefore(originalCall, (Local)ivk.getBase())) {
//					System.err.println("\t"+t);
//				}
//			} else {
//				System.err.println("\t"+ivk.getBase() + " "+ivk.getBase().getType());
//			}
			
			for (SootMethod callee : callees) {
				List<Unit> vcall = createVirtualCall(body, callee, originalCall, ivk);
				body.getUnits().addAll(vcall);
				
				Local l = getFreshLocal(body, BooleanType.v());
				List<Unit> stmts = new LinkedList<Unit>();
				stmts.add(assignStmtFor(l,
						Jimple.v().newInstanceOfExpr(ivk.getBase(), callee.getDeclaringClass().getType()),
						originalCall));
				stmts.add(ifStmtFor(jimpleNeZero(l), vcall.get(0), originalCall));
				body.getUnits().insertBefore(stmts, originalCall);
			}
			body.getUnits().remove(originalCall);
		}
		body.validate();
	}

	private List<Unit> createVirtualCall(Body body, SootMethod callee, Unit originalCall, InstanceInvokeExpr ivk) {
		List<Unit> units = new LinkedList<Unit>();
		Local l = getFreshLocal(body, callee.getDeclaringClass().getType());
		// cast the base to the corresponding type.
		units.add(assignStmtFor(l, Jimple.v().newCastExpr(ivk.getBase(), callee.getDeclaringClass().getType()),
				originalCall));

		if (originalCall instanceof InvokeStmt) {
			// make the call statement
			units.add(invokeStmtFor(l, callee.makeRef(), ivk.getArgs(), originalCall));
		} else if (originalCall instanceof AssignStmt) {
			AssignStmt s = (AssignStmt) originalCall;
			// make the call statement
			units.add(assignStmtFor(s.getLeftOp(), Jimple.v().newVirtualInvokeExpr(l, callee.makeRef(), ivk.getArgs()),
					s));
		} else if (originalCall instanceof IdentityStmt) {
			throw new RuntimeException();
		}
		// jump back to the statement after the original call.		
		Unit succ = body.getUnits().getSuccOf(originalCall);
		if (succ !=null) {
			units.add(gotoStmtFor(succ, originalCall));
		}
		return units;
	}

	private Unit invokeStmtFor(Local base, SootMethodRef method, List<? extends Value> args, Host createdFrom) {
		InvokeStmt stmt = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(base, method, args));
		stmt.addAllTagsOf(createdFrom);
		return stmt;

	}

	private List<SootMethod> getPossibleCallees(Body body, Unit u, InstanceInvokeExpr call) {
		List<SootMethod> res = new LinkedList<SootMethod>();

		SootMethod callee = call.getMethod();
//		SootClass sc = callee.getDeclaringClass();
//		if (call.getBase().getType() instanceof RefType) {
//			//then we can use a tighter type.
//			RefType rt = (RefType)call.getBase().getType();
//			sc = rt.getSootClass();			
//		} else if (call.getBase().getType() instanceof ArrayType) {
//			//TODO: this should be array.clone
//			res.add(callee);
//			return res;
//		} else {
//			System.err.println(call.getBase().getType().getClass());
//		}
		
		if (call instanceof SpecialInvokeExpr) {
			//TODO: is this correct?
			SpecialInvokeExpr sivk = (SpecialInvokeExpr)call;
			if (sivk.getMethod().isConstructor()) {
				res.add(callee);
				return res;				
			}
		}

		//in jimple, the base must be a local.
		assert (call.getBase() instanceof Local);
		Collection<SootClass> possibleClasses = new HashSet<SootClass>();
		for (Type t : ltf.getLocalTypesBefore(u, (Local)call.getBase())) {
			if (t instanceof RefType) {
				possibleClasses.add(((RefType)t).getSootClass()); 
			}
		}

					
		for (SootClass sub : possibleClasses) {
			if (sub.resolvingLevel() < SootClass.SIGNATURES) {
				// Log.error("Not checking subtypes of " + sub.getName());
				// Then we probably really don't care.
			} else {				
				if (sub.declaresMethod(callee.getName(), callee.getParameterTypes(), callee.getReturnType())) {
//					if (callee.hasActiveBody()) {
						// TODO: does it make sense to only add methods
						// that have an active body?
						res.add(sub.getMethod(callee.getName(), callee.getParameterTypes(), callee.getReturnType()));
//					}
				}
			}
		}

		if (res.isEmpty()) {
			//TODO check when this happens... usually when we have no body for any version 
			//of this method.
			res.add(callee);
		}
		if (res.size()==1) {
			return res;
		}
		
		//magic constant to keep the class file small.
		if (res.size()>30) {
			System.err.println("Ignoring " + res.size() + " cases for " + u);
			res.clear();
			res.add(callee);
			return res;
		}
		
//		System.err.println(res.size());
//		if (res.size()>100) {
//			
//			StringBuilder sb = new StringBuilder();
//			sb.append(body);
//			sb.append("Calls: ");
//			for (SootMethod m : res) {
//				sb.append(", "+m.getSignature());				
//			}
//			sb.append("\nStmt "+u);
//			sb.append("\nBaseType " +call.getBase().getType());
//			sb.append("\nBase " +call.getBase());
//			sb.append("\nProc ");
//			sb.append(body.getMethod().getSignature());
//			System.err.println(sb.toString());
//		}
		// we have to sort the methods by type.
		Collections.sort(res, new Comparator<SootMethod>() {
			@Override
			public int compare(final SootMethod a, final SootMethod b) {
				if (a == b || a.getDeclaringClass() == b.getDeclaringClass())
					return 0;
				if (hierarchy.isClassSubclassOf(a.getDeclaringClass(), b.getDeclaringClass()))
					return -1;
				if (hierarchy.isClassSuperclassOf(a.getDeclaringClass(), b.getDeclaringClass()))
					return 1;
				return 0;
			}
		});
		return res;
	}

}
