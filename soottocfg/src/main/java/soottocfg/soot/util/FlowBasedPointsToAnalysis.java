package soottocfg.soot.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Queue;

import org.jgrapht.Graphs;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.memory_model.NewMemoryModel;
import soottocfg.soot.transformers.ArrayTransformer;

/**
 * @author rodykers
 */
public class FlowBasedPointsToAnalysis {
	
	public void run(Program program) {
		
		// first add allocation site / alias class to all constructor calls
		for (Method m : program.getMethods()) {
			if (m.isConstructor()) {
				this.addToConstructor(m);
			}
		}
		
		// then propagate point to sets until we reach a fixpoint
		int changes;
		do {
			changes = 0;

			// go over all assignments and method calls and update points to sets
			for (Method m : program.getMethods()) {
				for (CfgBlock b : m.vertexSet()) {
					for (Statement s : b.getStatements()) {
						if (s instanceof AssignStatement) {
							AssignStatement as = (AssignStatement) s;
							Type left = as.getLeft().getType();
							Type right = as.getRight().getType();
							changes += rightIntoLeft(right, left);
						} else if (s instanceof CallStatement) {
							CallStatement cs = (CallStatement) s;
							Method target = cs.getCallTarget();
							List<Variable> params = target.getInParams();
							List<Expression> args = cs.getArguments();
							Verify.verify(params.size()==args.size());
							for (int i = 0; i < params.size(); i++) {
								Type left = params.get(i).getType();
								Type right = args.get(i).getType();
								changes += rightIntoLeft(right, left);
							}
							List<Type> rets = target.getReturnType();
							List<Expression> rec = cs.getReceiver();
							Verify.verify(rec.size()==0 || rets.size()==rec.size());
							for (int i = 0; i < rec.size(); i++) {
								Type left = rec.get(i).getType();
								Type right = rets.get(i);
								changes += rightIntoLeft(right, left);
							}
						} 
					}
				}
			}
		
		} while (changes > 0);
	}
	
	private void addToConstructor(Method m) {
		Queue<CfgBlock> todo = new LinkedList<CfgBlock>();
		todo.add(m.getSink());
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		while (!todo.isEmpty()) {
			CfgBlock cur = todo.remove();
			done.add(cur);
			List<Statement> stats = cur.getStatements();
			for (int i = stats.size()-1; i >= 0; i--) {
				if (stats.get(i) instanceof PushStatement) {
					PushStatement push = (PushStatement) stats.get(i);
					int allocSite = push.getID();
					ReferenceType rt = (ReferenceType) push.getObject().getType();
					Set<Integer> pt = rt.getPointsToSet();
					pt.add(allocSite);
					return;
				}
			}
			for (CfgBlock b : Graphs.predecessorListOf(m, cur)) {
				if (!done.contains(b))
					todo.add(b);
			}
		}
		Verify.verify(false, "Constructor does not have a push: " + m.getMethodName());
	}

	private int rightIntoLeft(Type right, Type left) {
		int changes = 0;
		if (left instanceof ReferenceType && right instanceof ReferenceType) {
			ReferenceType lhs = (ReferenceType) left;
			ReferenceType rhs = (ReferenceType) right;
			Set<Integer> ptleft = lhs.getPointsToSet();
			Set<Integer> ptright = rhs.getPointsToSet();
			if (!ptleft.containsAll(ptright)){
				for (Integer allocSite : ptright) {
					// only consider well typed assignments
					if (!ptleft.contains(allocSite) 
							&& lhs.getClassVariable().superclassOf(rhs.getClassVariable())) {
						ptleft.add(allocSite);
						changes++;
					}
				}
//				ptleft.addAll(ptright);
			}
		}
		return changes;
	}
	
	public static boolean mustAlias(Expression ref1, Expression ref2) {
		Set<Integer> pt1 = getPointsToSet(ref1);
		Set<Integer> pt2 = getPointsToSet(ref2);
		return pt1.size()==1 && pt2.size()==1 && pt1.containsAll(pt2);
	}
	
	public static boolean mayAlias(Expression ref1, Expression ref2) {
		Set<Integer> pt1 = getPointsToSet(ref1);
		Set<Integer> pt2 = getPointsToSet(ref2);
		return !(Collections.disjoint(pt1,pt2));
	}
	
	static private Set<Integer> getPointsToSet(Expression e) {
		Type t = e.getType();
		if (! (t instanceof ReferenceType)) {
			Verify.verify(false, "Called mustAlias on non-reference type");
		}
		
		// bit of a hack to get this to work with the Jayhorn classes
		if (e.toString().equals(NewMemoryModel.globalsClassName)
				|| t.toString().startsWith(ArrayTransformer.arrayTypeName)) {
			Set<Integer> pointsto = new HashSet<Integer>();
			pointsto.add(-1);
			return pointsto;
		}
		
		ReferenceType rt = (ReferenceType) t;
		Set<Integer> pt = rt.getPointsToSet();
		
//		System.out.println("Ref: " + e + " of type " + rt + " points to " + pt);
		Verify.verify(!pt.isEmpty(), 
				"Points to information missing, did you run the points-to analysis?");
		return pt;
	}
}
