package soottocfg.soot.memory_model;

import java.util.List;
import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.*;

public class PushPullSimplifier {
	
	private static boolean debug = true;
	
	public PushPullSimplifier() {
	}
	
	public void simplify(Program p) {
		Method[] ms = p.getMethods();
		for (Method m : ms) {
			if (debug)
				System.out.println("Simplifying method " + m.getMethodName());
			Set<CfgBlock> blocks = m.vertexSet();
			for (CfgBlock block : blocks) {
				if (debug)
					System.out.println("Simplifying block " + block);
				simplify(block);
			}
			
			if (debug)
				System.err.println(m);
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
				// TODO not the nicest way to compare these...
				if (push.getObject().toString().equals(pull.getObject().toString())) {
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
				// TODO not the nicest way to compare these...
				if (push.getObject().toString().equals(pull.getObject().toString())) {
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
				if (s instanceof AssignStatement || s instanceof AssertStatement || s instanceof AssumeStatement) {
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
				if (s instanceof AssignStatement || s instanceof AssertStatement || s instanceof AssumeStatement) {
					//only swap if none of the vars in s point to the same location as any of the fields
					Set<IdentifierExpression> pushvars = push.getIdentifierExpressions();
					
					// only check if assigned?
					Set<IdentifierExpression> svars = s.getDefIdentifierExpressions();
					if (distinct(svars,pushvars)) {
						b.swapStatements(i, i+1);
						if (debug)
							System.out.println("Applied rule (VI); swapped " + push + " and " + s);
						moved++;
					}
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
	
	/* Temporary: only compare the actual identifiers. TODO: points-to analysis */
	private boolean distinct(Set<IdentifierExpression> vars1, Set<IdentifierExpression> vars2) {
		for (IdentifierExpression exp1 : vars1)
			for (IdentifierExpression exp2 : vars2)
				if (exp1.getVariable().getName().equals(exp2.getVariable().getName()))
					return false;
		return true;
	}
}
