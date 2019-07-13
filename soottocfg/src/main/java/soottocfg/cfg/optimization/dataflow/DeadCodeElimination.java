package soottocfg.cfg.optimization.dataflow;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.optimization.ExpressionEvaluator;
import soottocfg.cfg.optimization.UnreachableNodeRemover;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.util.SetOperations;

public class DeadCodeElimination  {
	// Given a method, eliminate the dead code in it
	public DeadCodeElimination() {
	}

	public static boolean eliminateDeadCode(Method m) {		
		LiveVars<CfgBlock> blockLiveVars = m.computeBlockLiveVariables();
		boolean changed = false;
		for (CfgBlock block : m.vertexSet()) {
			changed = eliminateDeadStatements(m, block, blockLiveVars) ? true : changed; 
			//TODO: this currently doesn't affect the value of changed 
			//because its not finished yet.
			eliminateDeadConditions(m, block);
		}
		UnreachableNodeRemover.pruneUnreachableNodes(m, m.getSource());
		return changed;
	}

	protected static boolean isDead(Statement stmt, LiveVars<Statement> liveVars) {
		// If a statement writes to only variables that are not live, we can
		// remove it!
		// I.e. if intersection s.lvals, s.live is empty
		if ((stmt instanceof AssignStatement ||
                     stmt instanceof PullStatement) &&
                    SetOperations.intersect(
                      stmt.getDefVariables(),
                      liveVars.liveOut.get(stmt)).isEmpty())
                    return true;

                // Assignments with identical lhs and rhs can be removed
                if (stmt instanceof AssignStatement) {
                    AssignStatement astmt = (AssignStatement)stmt;
                    Expression left = astmt.getLeft();
                    Expression right = astmt.getRight();
                    if (left instanceof IdentifierExpression &&
                        right instanceof IdentifierExpression &&
                        ((IdentifierExpression)left).getVariable().equals(
                          ((IdentifierExpression)right).getVariable()))
                        return true;
                }
                
                if (stmt instanceof AssertStatement &&
                    BooleanLiteral.trueLiteral().equals(
                      ExpressionEvaluator.simplify(
                        ((AssertStatement)stmt).getExpression())))
                    return true;

                if (stmt instanceof AssumeStatement &&
                    BooleanLiteral.trueLiteral().equals(
                      ExpressionEvaluator.simplify(
                        ((AssumeStatement)stmt).getExpression())))
                    return true;

                return false;
	}


    /*
    	protected static Statement simplifyStmt(Statement stmt) {
            if (stmt instanceof AssertStatement) {
                AssertStatement astmt = (AssertStatement)stmt;
                return new AssertStatement(
                         stmt.getSourceLocation(),
                         ExpressionEvaluator.simplify(astmt.getExpression()));
            }
            if (stmt instanceof AssumeStatement) {
                AssumeStatement astmt = (AssumeStatement)stmt;
                return new AssumeStatement(
                         stmt.getSourceLocation(),
                         ExpressionEvaluator.simplify(astmt.getExpression()));
            }

            return stmt;   
        }
    */
    
	protected static boolean eliminateDeadStatements(
                                        Method method,
                                        CfgBlock block,
                                        LiveVars<CfgBlock> blockLiveVars) {
		boolean changed = false;
		List<Statement> rval = new LinkedList<Statement>();
		LiveVars<Statement> stmtLiveVars = block.computeLiveVariables(blockLiveVars);
		for (Statement s : block.getStatements()) {
			if (isDead(s, stmtLiveVars)) {
                            // If the statements is dead, just remove it from the list
                            changed = true;
			} else {
                            // otherwise, it stays in the list
                            rval.add(s.deepCopy());
			}
		}
		if (changed) {
			//only replace statements if something changed.
			block.setStatements(rval);
		}
		return changed;
	}

	/**
	 * Simplify the expressions on the edge labels. If an expression simplifies
	 * to True, we can remove the lable. If it simplifies to false, we can remove
	 * the edge.
	 * @param method
	 * @param block
	 * @return
	 */
	protected static boolean eliminateDeadConditions(Method method, CfgBlock block) {
		boolean changed = false;
		Set<CfgEdge> toRemove = new HashSet<CfgEdge>();

		for (CfgEdge edge : method.outgoingEdgesOf(block)) {
			if (edge.getLabel().isPresent()) {
				Expression simpleLabel = ExpressionEvaluator.simplify(edge.getLabel().get());
				if (simpleLabel instanceof BooleanLiteral) {
					if (((BooleanLiteral)simpleLabel).getValue()) {
						//then we can remove the label.
						edge.removeLabel();
					} else {
						//then we can remove the edge.
						toRemove.add(edge);
					}
				} else {
					edge.setLabel(simpleLabel);
				}
//				
//				Optional<Object> res = ExpressionEvaluator.eval(edge.getLabel().get());
//				if (res.isPresent()) {
//					if (!(Boolean)res.get()) {
//						// condition false, remove edge
//						toRemove.add(edge);
//					} else {
//						// condition true, remove all other edges?
//					}
//				} else {
//					// TODO?
//				}
			}
		}
				
		if (!toRemove.isEmpty()) {
			method.removeAllEdges(toRemove);
			changed = true;
		}
		return changed;
	}
}
