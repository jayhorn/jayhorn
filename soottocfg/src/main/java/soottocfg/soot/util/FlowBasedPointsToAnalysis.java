package soottocfg.soot.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.expression.literal.StringLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.Variable;

/**
 * @author rodykers
 */
public class FlowBasedPointsToAnalysis {
	
	private int nextAliasClass = 0;
	private Map<Variable,Set<Integer>> pointsTo = new HashMap<Variable,Set<Integer>>();
	
	public void run(Program program) {
		// first add allocation site / alias class to all constructor calls
		for (Method m : program.getMethods()) {
			for (CfgBlock b : m.vertexSet()) {
				for (Statement s : b.getStatements()) {
					if (s instanceof NewStatement) {
						NewStatement ns = (NewStatement) s;
						Set<Integer> pt = getPointsToSet(ns.getLeft());
						pt.add(nextAliasClass++);
					}
				}
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
							if (refType(as.getLeft()) && refType(as.getRight())
									&& !(as.getRight() instanceof NullLiteral)
									&& !(as.getRight() instanceof BinaryExpression)) {
								Variable left = variableFromExpression(as.getLeft());
								Variable right = variableFromExpression(as.getRight());
								changes += rightIntoLeft(right, left);
							}
						} else if (s instanceof CallStatement) {
							CallStatement cs = (CallStatement) s;
							Method target = cs.getCallTarget();
							List<Variable> params = target.getInParams();
							List<Expression> args = cs.getArguments();
							Verify.verify(params.size()==args.size());
							for (int i = 0; i < params.size(); i++) {
								Variable left = params.get(i);
								if (refType(left) && refType(args.get(i))
										&& !(args.get(i) instanceof NullLiteral)
										&& !(args.get(i) instanceof BinaryExpression)) {
									Variable right = variableFromExpression(args.get(i));
									changes += rightIntoLeft(right, left);
								}
							}
							List<Variable> rets = target.getOutParams();
							List<Expression> rec = cs.getReceiver();
							Verify.verify(rec.size()==1 || rets.size()==rec.size(),
									"In "+m.getMethodName()+ " for "+ cs+": "+rets.size()+"!="+rec.size());
							for (int i = 1; i < rec.size(); i++) {
								if (refType(rec.get(i)) && refType(rets.get(i-1))) {
									Variable left = variableFromExpression(rec.get(i));
									Variable right = rets.get(i-1);
									changes += rightIntoLeft(right, left);
								}
							}
						}
					}
				}
			}
		
		} while (changes > 0);
	}
	
	private int rightIntoLeft(Variable right, Variable left) {
		int changes = 0;
		if (refType(left) && refType(right)) {
			ReferenceType rtleft = (ReferenceType) left.getType();
			ReferenceType rtright = (ReferenceType) right.getType();
			Set<Integer> ptleft = getPointsToSet(left);
			Set<Integer> ptright = getPointsToSet(right);
			if (!ptleft.containsAll(ptright)){
				for (Integer allocSite : ptright) {
					// only consider well typed assignments
					if (!ptleft.contains(allocSite) 
							&& rtleft.getClassVariable().superclassOf(rtright.getClassVariable())) {
						ptleft.add(allocSite);
						changes++;
					}
				}
			}
		}
		return changes;
	}
	
	public boolean mustAlias(Expression ref1, Expression ref2) {
		ReferenceType rt1 = getReferenceType(ref1);
		ReferenceType rt2 = getReferenceType(ref2);
		
		if (rt1.getClassVariable()==null || rt2.getClassVariable()==null) {
//			System.err.println("Class var not set: " + ref1 + " and " + ref2);
			return false; 
		}
		
		if (!rt1.getClassVariable().subclassOf(rt2.getClassVariable()) 
				&& !rt1.getClassVariable().superclassOf(rt2.getClassVariable()))
			return false;
		
		if (!(ref1 instanceof NullLiteral || ref2 instanceof NullLiteral)) {
			Variable v1 = variableFromExpression(ref1);
			Variable v2 = variableFromExpression(ref2);
			if (v1.equals(v2))
				return true;
		}
		
		Set<Integer> pt1 = getPointsToSet(ref1);
		Set<Integer> pt2 = getPointsToSet(ref2);
		return pt1.size()==1 && pt2.size()==1 && pt1.containsAll(pt2);
	}
	
	public boolean mayAlias(Expression ref1, Expression ref2) {
		if (ref1 instanceof NullLiteral || ref2 instanceof NullLiteral)
			return false;
		
		ReferenceType rt1 = getReferenceType(ref1);
		ReferenceType rt2 = getReferenceType(ref2);
		
		if (rt1.getClassVariable()==null || rt2.getClassVariable()==null) {
//			System.err.println("Class var not set: " + ref1 + " and " + ref2);
			return false;
		}
		
		if (!rt1.getClassVariable().subclassOf(rt2.getClassVariable()) 
				&& !rt1.getClassVariable().superclassOf(rt2.getClassVariable()))
			return false;
		
		Set<Integer> pt1 = getPointsToSet(ref1);
		Set<Integer> pt2 = getPointsToSet(ref2);
		
		// If we did not collect points to info, err on the safe side
		if (pt1.isEmpty() || pt2.isEmpty()) return true;
		
		return !(Collections.disjoint(pt1,pt2));
	}
	
	private Set<Integer> getPointsToSet(Variable v) {
		if (!this.pointsTo.containsKey(v))
			this.pointsTo.put(v, new HashSet<Integer>());	
		return this.pointsTo.get(v);
	}
	
	private Set<Integer> getPointsToSet(Expression e) {
		if (e instanceof NullLiteral) {
			return new HashSet<Integer>();
		}
		return getPointsToSet(variableFromExpression(e));
	}
	
	private Variable variableFromExpression(Expression e) {
		Verify.verify(e.getUseVariables().size()==1,
				"Called variableFromExpression on expression that does not contain exactly 1 variable: " + e);
		return e.getUseVariables().iterator().next();
	}
	
	private boolean refType(Expression e1){
		return (e1.getType() instanceof ReferenceType) && !(e1 instanceof StringLiteral);	// TODO: better definition
	}
	
	private boolean refType(Variable e1){
		return (e1.getType() instanceof ReferenceType);
	}
	
	private ReferenceType getReferenceType(Expression e) {
		Verify.verify(refType(e), "Called aliasing method on non-reference type");
		return (ReferenceType) e.getType();
	}
}
