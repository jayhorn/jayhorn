package soottocfg.soot.memory_model;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SootField;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.*;
import soottocfg.soot.util.SootTranslationHelpers;

public class PushPullSimplifier {
	
	private static boolean debug = false;
	
	public PushPullSimplifier() {
	}
	
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
				simplifications += movePullsUpInCFG(blocks);
				simplifications += movePushesDownInCFG(blocks);
//				simplifications += removeEmptyBlocks(m);
				
				// does not seem sound, need to think more about it...
//				simplifications += moveAssumeFalseUpInCFG(blocks);
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
			simplifications += assumeFalseEatPreceeding(b);
		} while (simplifications > 0);
	}
	
	/* Rule I */
	private int removeConseqPulls(CfgBlock b) {
		int removed = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
			if (stmts.get(i) instanceof PullStatement && stmts.get(i+1) instanceof PullStatement) {
				PullStatement pull1 = (PullStatement) stmts.get(i);
				PullStatement pull2 = (PullStatement) stmts.get(i+1);
				// TODO not the nicest way to compare these...
				if (pull1.getObject().toString().equals(pull2.getObject().toString())) {
					if (debug)
						System.out.println("Applied rule (I); removed " + pull2);
					b.removeStatement(pull2);
					removed++;
					List<IdentifierExpression> pull1vars = pull1.getLeft();
					List<IdentifierExpression> pull2vars = pull2.getLeft();
					assert (pull1vars.size()==pull2vars.size());
					for (int j = 0; j < pull1vars.size(); j++) {
						if (!pull1vars.get(j).toString().equals(pull2vars.get(j).toString())) {
							System.out.println("Add assignment (TODO: test)");
							AssignStatement assign = new AssignStatement(SourceLocation.ANALYSIS,pull2vars.get(j),pull1vars.get(j));
							b.addStatement(i+1, assign);
						}
					}
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
				// TODO not the nicest way to compare these...
				if (push1.getObject().toString().equals(push2.getObject().toString())) {
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
					List<Expression> pushvars = push.getRight();
					List<IdentifierExpression> pullvars = pull.getLeft();
					assert (pushvars.size()==pullvars.size());
					for (int j = 0; j < pushvars.size(); j++) {
						if (!pushvars.get(j).toString().equals(pullvars.get(j).toString())) {
							System.out.println("Add assignment (TODO: test)");
							AssignStatement assign = new AssignStatement(SourceLocation.ANALYSIS,pullvars.get(j),pushvars.get(j));
							b.addStatement(i+1, assign);
						}
					}
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
			if (stmts.get(i) instanceof PullStatement && stmts.get(i+1) instanceof PushStatement) {
				PullStatement pull = (PullStatement) stmts.get(i);
				PushStatement push = (PushStatement) stmts.get(i+1);
				if (sameVars(push,pull)) {
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
			if (stmts.get(i+1) instanceof PullStatement) {
				PullStatement pull = (PullStatement) stmts.get(i+1);
				Statement s = stmts.get(i);
				if (s instanceof AssignStatement || s instanceof AssertStatement /*|| s instanceof AssumeStatement*/) {
					//only swap if none of the vars in s point to the same location as any of the fields
					Set<IdentifierExpression> pullvars = pull.getIdentifierExpressions();
					Set<IdentifierExpression> svars = s.getIdentifierExpressions();
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
				if (s instanceof AssignStatement || s instanceof AssertStatement /*|| s instanceof AssumeStatement*/) {

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
			if (stmts.get(i) instanceof PushStatement && stmts.get(i+1) instanceof PullStatement) {
				PushStatement push = (PushStatement) stmts.get(i);
				PullStatement pull = (PullStatement) stmts.get(i+1);
				//only swap if none of the vars in the pull and push point to the same location
				Set<IdentifierExpression> pullvars = pull.getIdentifierExpressions();
				Set<IdentifierExpression> pushvars = push.getIdentifierExpressions();
				if (distinct(pullvars,pushvars)) {
					b.swapStatements(i, i+1);
					if (debug)
						System.out.println("Applied rule (VII); swapped " + push + " and " + pull);
					swapped++;
				}
			}
		}
		return swapped;
	}
	
	/* Rule VIII (new) */
	private int assumeFalseEatPreceeding(CfgBlock b) {
		int eaten = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i < stmts.size(); i++) {
			if (stmts.get(i) instanceof AssumeStatement) {
				AssumeStatement as = (AssumeStatement) stmts.get(i);
				if (as.getExpression() instanceof BooleanLiteral && 
						((BooleanLiteral) as.getExpression()).equals(BooleanLiteral.falseLiteral())) {
					//Found one! Now eat everything except asserts.
					Set<Statement> toRemove = new HashSet<Statement>();
					int j = i - 1;
					while (j >= 0 && !(stmts.get(j) instanceof AssertStatement)) {
						System.out.println("Assume(false) eating " + stmts.get(j));
						toRemove.add(stmts.get(j));
						j--;
					}
					b.removeStatements(toRemove);
				}
			}
		}
		return eaten;
	}

	/* Temporary: only compare the actual identifiers. TODO: points-to analysis */
	private boolean distinct(Set<IdentifierExpression> vars1, Set<IdentifierExpression> vars2) {
		NewMemoryModel mem = (NewMemoryModel) SootTranslationHelpers.v().getMemoryModel();
		PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		for (IdentifierExpression exp1 : vars1) {
			for (IdentifierExpression exp2 : vars2) {
				if (debug)
					System.out.println("Checking distinctness of " + exp1 + " and " + exp2);
				Variable v1 = exp1.getVariable();
				Variable v2 = exp2.getVariable();
				if (v1.getName().equals(v2.getName())) {
					if (debug)
						System.out.println("Not distinct.");
					return false;
				}
				SootField sf1 = mem.lookupField(v1);
				SootField sf2 = mem.lookupField(v2);
				// oopsie, only works for static fields for now
				// TODO for instance fields we need to store Locals
				if (sf1!=null && sf1.isStatic() && sf2!=null && sf2.isStatic()) {
					PointsToSet pointsTo1 = pta.reachingObjects(sf1);
					PointsToSet pointsTo2 = pta.reachingObjects(sf2);
					if (pointsTo1.hasNonEmptyIntersection(pointsTo2)){
						if (debug)
							System.out.println("Point to same location, not distinct.");
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private int movePullsUpInCFG(Set<CfgBlock> blocks) {
		int moves = 0;
		for (CfgBlock b : blocks) {
			List<Statement> stmts = b.getStatements();
			int s = 0;
			Set<Statement> toRemove = new HashSet<Statement>();
			while (s < stmts.size() && stmts.get(s) instanceof PullStatement) {
				Set<CfgEdge> incoming = b.getMethod().incomingEdgesOf(b);
				for (CfgEdge in : incoming) {
					CfgBlock prev = b.getMethod().getEdgeSource(in);
					// only move up in CFG
					if (isUp(prev,b)) {
						prev.addStatement(stmts.get(s));
						toRemove.add(stmts.get(s));
						moves++;

						if (debug)
							System.out.println("Moved " + stmts.get(s) + " up in CFG.");
					}
				}
				s++;
			}
			b.removeStatements(toRemove);
		}
		return moves;
	}
	
	private int movePushesDownInCFG(Set<CfgBlock> blocks) {
		int moves = 0;
		for (CfgBlock b : blocks) {
			List<Statement> stmts = b.getStatements();
			int s = 0;
			Set<Statement> toRemove = new HashSet<Statement>();
			while (s < stmts.size() && stmts.get(s) instanceof PushStatement) {
				Set<CfgEdge> outgoing = b.getMethod().outgoingEdgesOf(b);
				for (CfgEdge out : outgoing) {
					CfgBlock next = b.getMethod().getEdgeTarget(out);
					// only move down in source
					if (isDown(next,b)) {
 						next.addStatement(0,stmts.get(s));
						toRemove.add(stmts.get(s));
						moves++;

						if (debug)
							System.out.println("Moved " + stmts.get(s) + " down in CFG.");
					}
				}
				s++;
			}
			b.removeStatements(toRemove);
		}
		return moves;
	}
	
	private int moveAssumeFalseUpInCFG(Set<CfgBlock> blocks) {
		int moves = 0;
		for (CfgBlock b : blocks) {
			if (!b.getStatements().isEmpty()) {
				Statement stmt = b.getStatements().get(0);
				if (stmt instanceof AssumeStatement) {
					AssumeStatement as = (AssumeStatement) stmt;
					if (as.getExpression() instanceof BooleanLiteral && 
							((BooleanLiteral) as.getExpression()).equals(BooleanLiteral.falseLiteral())) {
						System.out.println("Found assume(false)");
						// Found one! Now only move it up if all predecessors are actually up.
						boolean allUp = true;
						Set<CfgEdge> incoming = b.getMethod().incomingEdgesOf(b);
						for (CfgEdge in : incoming) {
							CfgBlock prev = b.getMethod().getEdgeSource(in);
							if (!isUp(prev,b))
								allUp = false;
						}
						if (allUp && !incoming.isEmpty()) {
							for (CfgEdge in : incoming) {
								CfgBlock prev = b.getMethod().getEdgeSource(in);
								prev.addStatement(as);							
							}
							b.removeStatement(as);
							moves++;
							if (debug)
								System.out.println("Moved assume(false) up in CFG.");
						}
					}
				}
			}
		}
		return moves;
	}
	
//	private int removeEmptyBlocks(Method m) {
//		int removed = 0;
//		Set<CfgBlock> toRemove = new HashSet<CfgBlock>();
//		for (CfgBlock b : m.vertexSet()) {
//			if (b.getStatements().isEmpty()) {
//				//make all predecessors point to unique successor
//				Set<CfgEdge> outgoing = b.getMethod().outgoingEdgesOf(b);
//				if (outgoing.size()==1) {
//					Set<CfgEdge> toRemoveEdges = new HashSet<CfgEdge>();
//					CfgBlock next = b.getMethod().getEdgeTarget((CfgEdge)outgoing.toArray()[0]);
//					Set<CfgEdge> incoming = b.getMethod().incomingEdgesOf(b);
//					for (CfgEdge in : incoming) {
//						CfgBlock prev = b.getMethod().getEdgeSource(in);
//						toRemoveEdges.add(in);
//						b.getMethod().addEdge(prev, next);
//					}
//					toRemove.add(b);
//					b.getMethod().removeAllEdges(toRemoveEdges);
//				}
//			}
//		}
//		// this one breaks everything...
////		m.removeAllVertices(toRemove);
//		return removed;
//	}
	
	private boolean sameVars(PushStatement push, PullStatement pull) {
		List<Expression> pushvars = push.getRight();
		List<IdentifierExpression> pullvars = pull.getLeft();
		if (pushvars.size() != pullvars.size())
			return false;
		
		for (int i = 0; i < pushvars.size(); i++) {
			// TODO build equals methods
			if (!pushvars.get(i).toString().equals(pullvars.get(i).toString()))
				return false;
		}
		
		return true;
	}
	
	// check if cur occurs in the CFG before prev, in that case,
	//  there is a cycle and we are not moving up 
	private boolean isUp(CfgBlock prev, CfgBlock cur) {
		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		todo.add(prev);
		while (!todo.isEmpty()) {
			CfgBlock b = todo.remove(0);
			if (!done.contains(b)) {
				if (b.equals(cur))
					return false;
				done.add(b);
				Set<CfgEdge> incoming = b.getMethod().incomingEdgesOf(b);
				for (CfgEdge in : incoming)
					todo.add(b.getMethod().getEdgeSource(in));
			}
		}
		return true;
	}

	private boolean isDown(CfgBlock next, CfgBlock cur) {
		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		todo.add(next);
		while (!todo.isEmpty()) {
			CfgBlock b = todo.remove(0);
			if (!done.contains(b)) {
				if (b.equals(cur))
					return false;
				done.add(b);
				Set<CfgEdge> outgoing = b.getMethod().outgoingEdgesOf(b);
				for (CfgEdge out : outgoing)
					todo.add(b.getMethod().getEdgeTarget(out));
			}
		}
		return true;
	}
}
