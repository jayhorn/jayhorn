package soottocfg.soot.memory_model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.NullLiteral;
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
import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.LoopFinder;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.SootToCfg;

public class PushPullSimplifier {
	
	private static boolean debug = false;
	
	private HashMap<Method, Set<CfgBlock>> loopHeaders = new HashMap<Method, Set<CfgBlock>>();
	
	public boolean simplify(Program p) {
		boolean change = false;
		Method[] ms = p.getMethods();
		for (Method m : ms) {
			if (debug) {
				System.out.println("Simplifying method " + m.getMethodName());
				System.out.println(m);
			}
			
			// find loop headers
			if (!loopHeaders.containsKey(m)) {
				Dominators<CfgBlock> doms = new Dominators<CfgBlock>(m, m.getSource());
				LoopFinder<CfgBlock> lf = new LoopFinder<CfgBlock>(doms);
				loopHeaders.put(m, lf.getLoopHeaders());
			}
			
			Set<CfgBlock> blocks = m.vertexSet();
			int simplifications;
			do {
				// intra-block simplification
				for (CfgBlock block : blocks) {
					change = simplify(block) ? true : change;
				}
				
				// inter-block simplifications
				simplifications = 0;
				simplifications += movePullsUpInCFG(m);
				simplifications += movePushesDownInCFG(m);
				change = (simplifications>0) ? true : change;
			} while (simplifications > 0);
			
			if (debug)
				System.out.println("SIMPLIFIED:\n"+m);			
		}
		return change;
	}
	
	public boolean simplify(CfgBlock b) {
		boolean change = false;
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
			if (simplifications>0) {
				change = true;
			}
		} while (simplifications > 0);
		return change;
	}
	
	/* Rule I */
	private int removeConseqPulls(CfgBlock b) {
		int removed = 0;
		List<Statement> stmts = b.getStatements();
		for (int i = 0; i+1 < stmts.size(); i++) {
                    if ((stmts.get(i) instanceof PullStatement ||
                         isConstructorCall(stmts.get(i))) &&
                        stmts.get(i+1) instanceof PullStatement) {
                        Statement pull1 = stmts.get(i);
                        Statement pull2 = stmts.get(i+1);
                        if (getObject(pull1).sameVariable(getObject(pull2))) {
                            List<IdentifierExpression> left1 = getPullLHS(pull1);
                            List<IdentifierExpression> left2 = getPullLHS(pull2);
                            if (left1.size() == left2.size()) {
                                if (debug)
                                    System.out.println("Applied rule (I); removed " + pull2);
                                b.removeStatement(i+1);
                                SourceLocation loc = pull2.getSourceLocation();
                                for (int j = 0; j < left1.size(); ++j) {
                                    IdentifierExpression var1 = left1.get(j);
                                    IdentifierExpression var2 = left2.get(j);
                                    if (!var1.sameVariable(var2))
                                        b.addStatement(i+1,
                                                       new AssignStatement(loc, var2, var1));
                                }
                                removed++;
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
                    if (stmts.get(i) instanceof PushStatement &&
                        stmts.get(i+1) instanceof PullStatement) {
                        PushStatement push = (PushStatement) stmts.get(i);
                        PullStatement pull = (PullStatement) stmts.get(i+1);

                        List<Expression> pushvars = push.getRight();
                        List<IdentifierExpression> pullvars = pull.getLeft();
                        
                        if (getObject(push).sameVariable(getObject(pull)) &&
                            pushvars.size() >= pullvars.size()) {

                            if (debug)
                                System.out.println("Applied rule (III); removed " + pull);
                            removed++;

                            b.removeStatement(i+1);
                            SourceLocation loc = pull.getSourceLocation();

                            for (int j = pullvars.size() - 1; j >= 0; --j) {
                                Expression pushvar = pushvars.get(j);
                                IdentifierExpression pullvar = pullvars.get(j);
                                
                                if (!(pushvar instanceof IdentifierExpression &&
                                      ((IdentifierExpression)pushvar).sameVariable(pullvar)))
                                    b.addStatement(i+1,
                                                   new AssignStatement(loc, pullvar, pushvar));
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
                    Statement s = stmts.get(i);

                    if (stmts.get(i+1) instanceof PullStatement &&
                        (s instanceof AssignStatement ||
                         s instanceof NewStatement ||
                         s instanceof AssumeStatement ||
                         s instanceof AssertStatement)) {
                        PullStatement pull = (PullStatement)stmts.get(i+1);

                        Set<Variable> sDefs = s.getDefVariables();
                        Set<Variable> pullVars = pull.getAllVariables();

                        if (Collections.disjoint(sDefs, pullVars)) {
                            Set<Variable> pullDefs = pull.getDefVariables();
                            List<Variable> conflictingVars = new LinkedList<>();

                            for (Variable v : s.getAllVariables())
                                if (pullDefs.contains(v))
                                    conflictingVars.add(v);

                            if (conflictingVars.isEmpty()) {
                                if (debug)
                                    System.out.println("Applied rule (V); swapped " + s + " and " + pull);
                                b.swapStatements(i, i+1);
                            } else {
                                // we need to introduce temporary copies of the variables
                                // defined by the pull

                                SourceLocation loc = pull.getSourceLocation();
                                Map<Variable, IdentifierExpression> varMap = new HashMap<>();

                                for (Variable v : conflictingVars) {
                                    Variable vCopy = new Variable(v.getName() + "_cp", v.getType());
                                    IdentifierExpression expr = new IdentifierExpression(loc, vCopy);
                                    varMap.put(v, expr);
                                    b.addStatement(i+2,
                                                   new AssignStatement(loc,
                                                                       new IdentifierExpression (loc, v),
                                                                       expr));
                                }

                                List<IdentifierExpression> newLHS = new LinkedList<> ();
                                for (IdentifierExpression expr : pull.getLeft()) {
                                    IdentifierExpression vCopy = varMap.get(expr.getVariable());
                                    if (vCopy == null)
                                        newLHS.add(expr);
                                    else
                                        newLHS.add(vCopy);
                                }

                                Statement newPull =
                                    new PullStatement(loc, pull.getClassSignature(),
                                                      getObject(pull),
                                                      newLHS, pull.getGhostExpressions());

                                b.removeStatement(i+1);
                                b.addStatement(i, newPull);

                                if (debug)
                                    System.out.println("Applied rule (V); swapped " + s + " and " + pull
                                                       + ", renaming assigned variables");
                            }

                            moved++;
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
				if (s instanceof AssignStatement || s instanceof AssertStatement || 
						/*s instanceof NewStatement ||*/ s instanceof AssumeStatement) {
						b.swapStatements(i, i+1);
						if (debug)
							System.out.println("Applied rule (VI); swapped " + push + " and " + s);
						moved++;
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
                if (debug)
                    System.out.println("Checking block " + b.getLabel() + " for pulls to move up");
			
                List<Statement> stmts = b.getStatements();
                int s = 0;
                Set<Statement> toRemove = new HashSet<Statement>();
                Set<CfgEdge> incoming = m.incomingEdgesOf(b);

                Set<CfgBlock> incomingBlocks = new HashSet<CfgBlock>();
                for (CfgEdge in : incoming)
                    incomingBlocks.add(m.getEdgeSource(in));
				
                while (s < stmts.size() && 
                       (stmts.get(s) instanceof PullStatement || isConstructorCall(stmts.get(s)))) {
				
                    Statement pull = stmts.get(s);
                    boolean nothingMoves = false;
				
                    if (debug)
                        System.out.println("Let's see if we can move " + pull + " up in the CFG...");

                    // if there is even just one predecessor which is further from 
                    // the source, don't move anything

                    int bDist = m.distanceToSource(b);
                    for (CfgEdge in : incoming) {
                        CfgBlock prev = m.getEdgeSource(in);
                        if (!(m.distanceToSource(prev) < bDist ||
                              (pull instanceof PullStatement &&
                               blockIsBlockingPull(prev, (PullStatement)pull))) ||
                            !(m.outgoingEdgesOf(prev).size() <= 1 ||
                              blockIsPullingObject(prev, getObjectVar(pull)))) {
                            nothingMoves = true;
                            break;
                        }
                    }
		
                    if (nothingMoves)
                        break;
		
                    for (CfgBlock prev : incomingBlocks) {
                        toRemove.add(pull);
                        prev.addStatement(pull.deepCopy());
                        moves++;

                        if (debug)
                            System.out.println("Moved " + pull + " up in CFG.");
                    }
		
                    s++;
                }
                b.removeStatements(toRemove);
            }
            return moves;
	}

    /**
     * Determine whether a block contains a pull statement for the
     * given object.
     *
     * TODO: this should be done properly using AI
     */
    private boolean blockIsPullingObject(CfgBlock b, Variable obj) {
        List<Statement> stmts = b.getStatements();
        for (int i = stmts.size() - 1; i >= 0; --i) {
            Statement stmt = stmts.get(i);
            if (stmt instanceof PullStatement) {
                if (obj.equals(getObjectVar(stmt)))
                    return true;
            }
            if (stmt.getDefVariables().contains(obj))
                return false;
        }
        return false;
    }

    /**
     * Determine whether the possibility exists that method <code>movePullUp</code>
     * will move the given pull statement across the whole block
     */
    private boolean blockIsBlockingPull(CfgBlock b, PullStatement pull) {
        Variable object = getObjectVar(pull);
        List<Statement> stmts = b.getStatements();
        for (int i = stmts.size() - 1; i >= 0; --i) {
            Statement stmt = stmts.get(i);
            if (stmt instanceof PushStatement) {
                PushStatement s = (PushStatement)stmt;
                if (object.equals(getObjectVar(s)))
                    return true;
            }
            if (stmt instanceof NewStatement) {
                NewStatement s = (NewStatement)stmt;
                if (object.equals(getObjectVar(s)))
                    return true;
            }
            if (stmt.getDefVariables().contains(object))
                return false;
        }
        return false;
    }
	
	/*
	 * We don't currently allow pushes to break out of loops. We might in the future
	 * to improve precision, but we have to carefully establish the conditions for doing so.
	 */
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

                                // TODO: why is this not limited to subsequent blocks with only one incoming edge?
				for (CfgEdge out : outgoing) {
					CfgBlock next = b.getMethod().getEdgeTarget(out);
					
					// only move down in source 
					// and to the end of a loop, not back into the header
					if (m.distanceToSink(next) < m.distanceToSink(b) &&
                                            !loopHeaders.get(m).contains(next) &&
                                            m.incomingEdgesOf(next).size() == 1) {
						
						// Not sure why I added this before, but as labels are pure expressions, it's not needed
//						if (out.getLabel().isPresent() && 
//							!distinct(out.getLabel().get().getUseIdentifierExpressions(), push.getIdentifierExpressions())) {
//							// edge label contains a ref to push object, do not move this push
//							if (debug)
//								System.out.println("Label not distinct: " + push);
//							nothingMoves = true;
//							break;
//						}
						
						if (!hasBeenPulledIn(push, next)) {
							// object has not been pulled in successor block, do not move this push
							if (debug)
								System.out.println("Not pulled in predecessor of block " + next.getLabel() + ": " + push);
							nothingMoves = true;
							break;
						}
						moveTo.add(next);
					} else {
						nothingMoves = true;
						break;
					}
				}
				
				if (!nothingMoves) {
					for (CfgBlock next : moveTo) {
                                            toRemove.add(push);
                                            // don't create references to the same statement in multiple blocks
                                            next.addStatement(0, push.deepCopy());
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
		if (debug)
			System.out.println("Checking distinctness of " + vars1 + " and " + vars2);
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
		if (s instanceof NewStatement)
			return (IdentifierExpression) ((NewStatement) s).getLeft();
		if (isConstructorCall(s))
			return (IdentifierExpression) ((CallStatement) s).getArguments().get(0);
		return null;
	}

        private List<IdentifierExpression> getPullLHS(Statement s) {
            if (s instanceof PullStatement)
                return ((PullStatement)s).getLeft();
            if (isConstructorCall(s)) {
                List<IdentifierExpression> result = new LinkedList<>();
                List<Expression> receivers = ((CallStatement) s).getReceiver();
                for (int i = 1; i < receivers.size(); ++i)
                    result.add((IdentifierExpression)receivers.get(i));
                return result;
            }
            return null;
        }

	private Variable getObjectVar(Statement s) {
            return getObject(s).getVariable();
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
	
	private boolean isNullCheckBeforePull(Statement previous, AssertStatement as, PullStatement pull) {
		Variable pullVar = getObjectVar(pull);
		if (previous instanceof AssignStatement) {
			AssignStatement assign = (AssignStatement) previous;
			Expression rhs = assign.getRight();
			if (rhs instanceof BinaryExpression) {
				BinaryExpression be = (BinaryExpression) rhs;
				if (be.getOp() == BinaryExpression.BinaryOperator.Ne) {
					if (be.getRight() instanceof NullLiteral && be.getLeft() instanceof IdentifierExpression) {
						IdentifierExpression ie = (IdentifierExpression) be.getLeft();
						for (Variable v : pull.getAllVariables()) {
							if (v.equals(pullVar)) {
								if (debug)
									System.out.println("Found null check for " + ie);
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}
