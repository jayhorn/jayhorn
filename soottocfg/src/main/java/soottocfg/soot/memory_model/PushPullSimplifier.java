package soottocfg.soot.memory_model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.soot.SootToCfg;

public class PushPullSimplifier {
	
	private static boolean debug = false;
	
	public void simplify(Program p) {
		Method[] ms = p.getMethods();
		for (Method m : ms) {
			if (debug) {
				System.out.println("Simplifying method " + m.getMethodName());
				System.out.println(m);
			}
			Set<CfgBlock> blocks = m.vertexSet();
			int simplifications;
			do {
				// intra-block simplification
				for (CfgBlock block : blocks)
					simplify(block);
				
				// inter-block simplifications
				simplifications = 0;
				simplifications += movePullsUpInCFG(m);
				simplifications += movePushesDownInCFG(m);
			} while (simplifications > 0);
			
			if (debug)
				System.out.println("SIMPLIFIED:\n"+m);
		}
	}
	
	public void simplify(CfgBlock b) {
		int simplifications;
		do {
			simplifications = 0;
			simplifications += removeConseqPulls(b);
			simplifications += removeConseqPushs(b);
			simplifications += removePullAfterPush(b);
			simplifications += removePushAfterPull(b);
			simplifications += movePullUp(b);
			simplifications += movePushDown(b);
			simplifications += swapPushPull(b);
			simplifications += orderPulls(b);
			simplifications += orderPushes(b);
//			simplifications += assumeFalseEatPreceeding(b);
		} while (simplifications > 0);
	}
	
	/* Rule I */
	private int removeConseqPulls(CfgBlock b) {
		int removed = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (	(stmts.get(i) instanceof PullStatement || isConstructorCall(stmts.get(i)))
					&& stmts.get(i+1) instanceof PullStatement) {
				Statement pull1 = stmts.get(i);
				Statement pull2 = stmts.get(i+1);
				if (getObject(pull1).sameVariable(getObject(pull2))) {
					if (debug)
						System.out.println("Applied rule (I); removed " + pull2);
					b.removeStatement(pull2);
					removed++;
				}
			}
		}
		return removed;
	}
	
	/* Rule II */
	private int removeConseqPushs(CfgBlock b) {
		int removed = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (stmts.get(i) instanceof PushStatement && stmts.get(i+1) instanceof PushStatement) {
				PushStatement push1 = (PushStatement) stmts.get(i);
				PushStatement push2 = (PushStatement) stmts.get(i+1);
				if (getObject(push1).sameVariable(getObject(push2))) {
					if (debug)
						System.out.println("Applied rule (II); removed " + push1);
					b.removeStatement(push1);
					removed++;
				}
			}
		}
		return removed;
	}
	
	/* Rule III */
	private int removePullAfterPush(CfgBlock b) {
		int removed = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (stmts.get(i) instanceof PushStatement && stmts.get(i+1) instanceof PullStatement) {
				PushStatement push = (PushStatement) stmts.get(i);
				PullStatement pull = (PullStatement) stmts.get(i+1);
				if (sameVars(push,pull)) {
					if (debug)
						System.out.println("Applied rule (III); removed " + pull);
					b.removeStatement(pull);
					removed++;
				}
			}
		}
		return removed;
	}
	
	/* Rule IV */
	private int removePushAfterPull(CfgBlock b) {
		int removed = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (	(stmts.get(i) instanceof PullStatement || isConstructorCall(stmts.get(i)))
					&& stmts.get(i+1) instanceof PushStatement) {
				Statement pull = stmts.get(i);
				Statement push = stmts.get(i+1);
				if (sameVarsStatement(push,pull)) {
					if (debug)
						System.out.println("Applied rule (IV); removed " + push);
					b.removeStatement(push);
					removed++;
				}
			}
		}
		return removed;
	}
	
	/* Rule V */
	private int movePullUp(CfgBlock b) {
		int moved = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (stmts.get(i+1) instanceof PullStatement || isConstructorCall(stmts.get(i+1))) {
				Statement pull = stmts.get(i+1);
				Statement s = stmts.get(i);
				if (s instanceof AssignStatement || s instanceof AssertStatement || s instanceof NewStatement /*|| s instanceof AssumeStatement*/) {
					//only swap if none of the vars in s point to the same location as any of the fields
					Set<IdentifierExpression> pullvars = pull.getIdentifierExpressions();
					Set<IdentifierExpression> svars = s.getIdentifierExpressions();
					
					if (s instanceof AssignStatement) {
						AssignStatement as = (AssignStatement) s;
						svars = as.getLeft().getUseIdentifierExpressions();
					}
					
					if (distinct(svars,pullvars)) {
						b.swapStatements(i, i+1);
						if (debug)
							System.out.println("Applied rule (V); swapped " + s + " and " + pull);
						moved++;
					}
				}
			}
		}
		return moved;
	}
	
	/* Rule VI */
	private int movePushDown(CfgBlock b) {
		int moved = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (stmts.get(i) instanceof PushStatement) {
				PushStatement push = (PushStatement) stmts.get(i);
				Statement s = stmts.get(i+1);
				if (s instanceof AssignStatement || s instanceof AssertStatement || s instanceof NewStatement /*|| s instanceof AssumeStatement*/) {

					// I don't think this check is needed. In SatStatic2 example,
					// it prevents a push to move past an assignment, while there is an identical
					// push later (there always is, if the assigned vars are not distinct).
					
					//only swap if none of the vars in s point to the same location as any of the fields
//					Set<IdentifierExpression> pushvars = push.getIdentifierExpressions();
//					Set<IdentifierExpression> svars = s.getDefIdentifierExpressions();
//					if (distinct(svars,pushvars)) { 
						b.swapStatements(i, i+1);
						if (debug)
							System.out.println("Applied rule (VI); swapped " + push + " and " + s);
						moved++;
//					}
				}
			}
		}
		return moved;
	}
	
	/* Rule VII */
	private int swapPushPull(CfgBlock b) {
		int swapped = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (	stmts.get(i) instanceof PushStatement && 
					(stmts.get(i+1) instanceof PullStatement || isConstructorCall(stmts.get(i+1)))) {
				Statement push = stmts.get(i);
				Statement pull = stmts.get(i+1);
				//only swap if the objects in the pull and push do not point to the same location
				if (!SootToCfg.getPointsToAnalysis().mayAlias(getObject(pull), getObject(push))) {
					b.swapStatements(i, i+1);
					if (debug)
						System.out.println("Applied rule (VII); swapped " + push + " and " + pull);
					swapped++;
				}
			}
		}
		return swapped;
	}
	
	/* Rule VIII */
	private int orderPulls(CfgBlock b) {
		// order pushes alphabetically w.r.t. the object name
		// allows to remove doubles
		int swapped = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (	(stmts.get(i) instanceof PullStatement || isConstructorCall(stmts.get(i)))
					&& (stmts.get(i+1) instanceof PullStatement || isConstructorCall(stmts.get(i+1)))) {
				Statement pull1 = stmts.get(i);
				Statement pull2 = stmts.get(i+1);
				if (getObject(pull1).toString().compareTo(getObject(pull2).toString()) < 0) {
					//only swap if none of the vars in the pull and push point to the same location
					Set<IdentifierExpression> pull1vars = pull1.getIdentifierExpressions();
					Set<IdentifierExpression> pull2vars = pull2.getIdentifierExpressions();
					if (distinct(pull1vars,pull2vars)) {
						b.swapStatements(i, i+1);
						if (debug)
							System.out.println("Applied rule (VIII); swapped " + pull1 + " and " + pull2);
						swapped++;
					}
				}
			}
		}
		return swapped;
	}
	
	/* Rule IX */
	private int orderPushes(CfgBlock b) {
		// order pushes alphabetically w.r.t. the object name
		// allows to remove doubles
		int swapped = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (stmts.get(i) instanceof PushStatement && stmts.get(i+1) instanceof PushStatement) {
				PushStatement push1 = (PushStatement) stmts.get(i);
				PushStatement push2 = (PushStatement) stmts.get(i+1);
				if (push1.getObject().toString().compareTo(push2.getObject().toString()) > 0) {
					//only swap if none of the vars in the pull and push point to the same location
					Set<IdentifierExpression> push1vars = push1.getIdentifierExpressions();
					Set<IdentifierExpression> push2vars = push2.getIdentifierExpressions();
					if (distinct(push1vars,push2vars)) {
						b.swapStatements(i, i+1);
						if (debug)
							System.out.println("Applied rule (IX); swapped " + push1 + " and " + push2);
						swapped++;
					}
				}
			}
		}
		return swapped;
	}
	
	/* Rule X (new) */
//	private int assumeFalseEatPreceeding(CfgBlock b) {
//		int eaten = 0;
//		List<Statement> stmts = b.getStatements();
//		for (int i = 0; i < stmts.size(); i++) {
//			if (stmts.get(i) instanceof AssumeStatement) {
//				AssumeStatement as = (AssumeStatement) stmts.get(i);
//				if (as.getExpression() instanceof BooleanLiteral && 
//						((BooleanLiteral) as.getExpression()).equals(BooleanLiteral.falseLiteral())) {
//					//Found one! Now eat everything except asserts.
//					Set<Statement> toRemove = new HashSet<Statement>();
//					int j = i - 1;
//					while (j >= 0 && !(stmts.get(j) instanceof AssertStatement)) {
//						System.out.println("Assume(false) eating " + stmts.get(j));
//						toRemove.add(stmts.get(j));
//						j--;
//					}
//					b.removeStatements(toRemove);
//				}
//			}
//		}
//		return eaten;
//	}
	
	private int movePullsUpInCFG(Method m) {
		int moves = 0;
		for (CfgBlock b : m.vertexSet()) {
			List<Statement> stmts = b.getStatements();
			int s = 0;
			Set<Statement> toRemove = new HashSet<Statement>();
			while (s < stmts.size() && 
					(stmts.get(s) instanceof PullStatement || isConstructorCall(stmts.get(s)))) {
				
				PullStatement pull = (PullStatement) stmts.get(s);
				Set<CfgEdge> incoming = b.getMethod().incomingEdgesOf(b);				
				Set<CfgBlock> moveTo = new HashSet<CfgBlock>();
				boolean nothingMoves = false;
				
				for (CfgEdge in : incoming) {
					CfgBlock prev = b.getMethod().getEdgeSource(in);
					
					// only move up in CFG
					if (m.distanceToSource(prev) < m.distanceToSource(b)) {
						if (in.getLabel().isPresent() && 
								!distinct(in.getLabel().get().getUseIdentifierExpressions(), pull.getIdentifierExpressions())) {
								// edge label contains a ref to push object, do not move this push
							 nothingMoves = true;
							 break;
						}
						if (!willBePushedIn(pull, prev)) {
							// object has not been pushed in successor block, do not move this push
							nothingMoves = true;
							break;
						}
						moveTo.add(prev);
					} else if (!existsPath(b, prev)) {
						/**
						 * With the check that there does not exist a path, we accommodate the following case.
						 * There is a loop and b is the header. We know this because the is a path from b
						 * to prev, yet b is closer to the source.
						 * In this case, we want to move the pull out of the loop into the other
						 * predecessor nodes (note that these surely exist), yet not move it into
						 * prev, as we would then be moving it around in circles.
						 */
						nothingMoves = true;
						break;
					} else {
						System.out.println("Skipping the copy of " + pull + " from " + b + " to " + prev);
					}
				}
				
				if (!nothingMoves) {
					for (CfgBlock prev : moveTo) {
						//don't create references to the same statement in multiple blocks
						if (toRemove.contains(pull))
							pull = pull.deepCopy();
						else
							toRemove.add(pull);
						prev.addStatement(pull);
						moves++;

						if (debug)
							System.out.println("Moved " + pull + " up in CFG.");
					}
				}
				
				s++;
			}
			b.removeStatements(toRemove);
		}
		return moves;
	}
	
	private int movePushesDownInCFG(Method m) {
		int moves = 0;
		for (CfgBlock b : m.vertexSet()) {
			if (debug)
				System.out.println("Checking block " + b.getLabel() + " for pushes to move down");
			List<Statement> stmts = b.getStatements();
			int s = stmts.size()-1;
			Set<Statement> toRemove = new HashSet<Statement>();
			while (s >= 0 && stmts.get(s) instanceof PushStatement) {
				
				PushStatement push = (PushStatement) stmts.get(s);
				Set<CfgEdge> outgoing = b.getMethod().outgoingEdgesOf(b);				
				Set<CfgBlock> moveTo = new HashSet<CfgBlock>();
				boolean nothingMoves = false;

				if (debug)
					System.out.println("Let's see if we can move " + push + " down in the CFG...");
				
				for (CfgEdge out : outgoing) {
					CfgBlock next = b.getMethod().getEdgeTarget(out);
					
					// only move down in source
					if (m.distanceToSink(next) < m.distanceToSink(b)) {
						if (out.getLabel().isPresent() && 
							!distinct(out.getLabel().get().getUseIdentifierExpressions(), push.getIdentifierExpressions())) {
							// edge label contains a ref to push object, do not move this push
							if (debug)
								System.out.println("Label not distinct: " + push);
							nothingMoves = true;
							break;
						}
						if (!hasBeenPulledIn(push, next)) {
							// object has not been pulled in successor block, do not move this push
							if (debug)
								System.out.println("Not pulled in successor block " + next.getLabel() + ": " + push);
							nothingMoves = true;
							break;
						}
						moveTo.add(next);
					} else if (!existsPath(next, b)) {
						/**
						 * Analogous to moving pulls up, we want to break pushes
						 * from loops.
						 */
						nothingMoves = true;
					} else {
						System.out.println("Skipping the copy of " + push + " from " + b + " to " + next);
					}
				}
				
				if (!nothingMoves) {
					for (CfgBlock next : moveTo) {
						// don't create references to the same statement in multiple blocks
						if (toRemove.contains(push))
							push = (PushStatement) push.deepCopy();
						else
							toRemove.add(push);
						next.addStatement(0, push);
						moves++;

						if (debug)
							System.out.println("Moved " + push + " down in CFG.");
					}
				}
				
				s--;
			}
			b.removeStatements(toRemove);
		}
		return moves;
	}
	
	private boolean distinct(Set<IdentifierExpression> vars1, Set<IdentifierExpression> vars2) {
		for (IdentifierExpression exp1 : vars1) {
			for (IdentifierExpression exp2 : vars2) {
				if (debug)
					System.out.println("Checking distinctness of " + exp1 + exp1.getType() + " and " + exp2 + exp2.getType());
				
				if (exp1.sameVariable(exp2)) {
					if (debug)
						System.out.println("Same var: " + exp1 + " and " + exp2);
					return false;
				} else if (exp1.getType() instanceof ReferenceType
						&& exp2.getType() instanceof ReferenceType) {
					if (soottocfg.Options.v().memPrecision() >= 3) {
						if (SootToCfg.getPointsToAnalysis().mayAlias(exp1, exp2))
							return false;
					} else {
						ReferenceType rt1 = (ReferenceType) exp1.getType();
						ReferenceType rt2 = (ReferenceType) exp2.getType();
						ClassVariable cv1 = rt1.getClassVariable();
						ClassVariable cv2 = rt2.getClassVariable();
						if (cv1!=null && cv2!=null 
								&& (cv1.subclassOf(cv2) || !cv1.superclassOf(cv2)))
							return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean sameVars(PushStatement push, PullStatement pull) {
		List<Expression> pushvars = push.getRight();
		List<IdentifierExpression> pullvars = pull.getLeft();
		if (pushvars.size() != pullvars.size())
			return false;
		
		for (int i = 0; i < pushvars.size(); i++) {
			if (! (pushvars.get(i) instanceof IdentifierExpression))
				return false;
			IdentifierExpression ie1 = (IdentifierExpression) pullvars.get(i);
			IdentifierExpression ie2 = (IdentifierExpression) pushvars.get(i);
			if (!ie1.sameVariable(ie2))
				return false;
		}
		
		return true;
	}
	
	private boolean sameVars(PushStatement push, CallStatement pull) {
		List<Expression> pushvars = push.getRight();
		List<Expression> pullvars = pull.getReceiver();
		if (pushvars.size() != pullvars.size())
			return false;
		
		for (int i = 0; i < pushvars.size(); i++) {
			if (! (pullvars.get(i) instanceof IdentifierExpression))
				return false;
			if (! (pushvars.get(i) instanceof IdentifierExpression))
				return false;
			IdentifierExpression ie1 = (IdentifierExpression) pullvars.get(i);
			IdentifierExpression ie2 = (IdentifierExpression) pushvars.get(i);
			if (!ie1.sameVariable(ie2))
				return false;
		}
		
		return true;
	}
	
	private boolean sameVarsStatement(Statement push, Statement pull) {
		Verify.verify(push instanceof PushStatement);
		Verify.verify(pull instanceof PullStatement || isConstructorCall(pull));
		if (pull instanceof PullStatement)
			return sameVars((PushStatement) push, (PullStatement) pull);
		else
			return sameVars((PushStatement) push, (CallStatement) pull);
	}
	
	private boolean isConstructorCall(Statement s) {
		if (s instanceof CallStatement) {
			CallStatement cs = (CallStatement) s;
			return cs.getCallTarget().isConstructor();
		}
		return false;
	}
	
	private IdentifierExpression getObject(Statement s) {
		if (s instanceof PullStatement)
			return (IdentifierExpression) ((PullStatement) s).getObject();
		if (s instanceof PushStatement)
			return (IdentifierExpression) ((PushStatement) s).getObject();
		if (isConstructorCall(s))
			return (IdentifierExpression) ((CallStatement) s).getArguments().get(0);
		return null;
	}
	
	// check if the object of a push has been pulled in or on the path to CfgBlock b
	private boolean hasBeenPulledIn(PushStatement push, CfgBlock b) {
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		Queue<CfgBlock> q = new LinkedList<CfgBlock>();
		q.add(b);
		while (!q.isEmpty()) {
			CfgBlock cur = q.poll();
			done.add(cur);
			for (Statement s : cur.getStatements()) {
				if (s instanceof PullStatement || isConstructorCall(s)) {
					if (getObject(s).sameVariable(getObject(push))) {
						return true;
					}					
				}
			}
			
			Set<CfgEdge> incoming = cur.getMethod().incomingEdgesOf(cur);
			for (CfgEdge in : incoming) {
				CfgBlock prev = cur.getMethod().getEdgeSource(in);
				if (!done.contains(prev) && !q.contains(prev))
					q.add(prev);
			}
		}
		return false;
	}
	
	// check if the object of a push has been pulled in or on the path to CfgBlock b
	private boolean willBePushedIn(PullStatement pull, CfgBlock b) {
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		
		Queue<CfgBlock> q = new LinkedList<CfgBlock>();
		q.add(b);
		while (!q.isEmpty()) {
			CfgBlock cur = q.poll();
			done.add(cur);
			for (Statement s : cur.getStatements()) {
				if (s instanceof PushStatement) {
					if (getObject(s).sameVariable(getObject(pull))) {
						return true;
					}
				}
			}
			
			Set<CfgEdge> outgoing = cur.getMethod().outgoingEdgesOf(cur);
			for (CfgEdge out : outgoing) {
				CfgBlock next = cur.getMethod().getEdgeTarget(out);
				if (!done.contains(next) && !q.contains(next))
					q.add(next);
			}
		}
		return false;
	}
	
	private boolean existsPath(CfgBlock from, CfgBlock to) {
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		Queue<CfgBlock> q = new LinkedList<CfgBlock>();
		q.add(from);
		while (!q.isEmpty()) {
			CfgBlock cur = q.poll();
			done.add(cur);
			if (cur.equals(to))
				return true;
			
			Set<CfgEdge> outgoing = cur.getMethod().outgoingEdgesOf(cur);
			for (CfgEdge out : outgoing) {
				CfgBlock next = cur.getMethod().getEdgeTarget(out);
				if (!done.contains(next) && !q.contains(next))
					q.add(next);
			}
		}
		return false;
	}
}
