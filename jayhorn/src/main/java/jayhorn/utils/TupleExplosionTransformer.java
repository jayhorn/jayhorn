/**
 * 
 */
package jayhorn.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.TupleAccessExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class TupleExplosionTransformer {

	public static final String outHeapCounterName = "outHeapCounter";

	public TupleExplosionTransformer() {
	}

	/**
	 * Add IDs to track the calling context. There are different
	 * strategies to do that. Currently, this only implements the
	 * approach of assigning a unqiue number to every call statement and
	 * passing that as extra parameter to all callees. The callee
	 * then adds that to all pushes and pulls.
	 * 
	 * @param p
	 */
	public void transform(Program p) {
		insertGlobalHeapCounter(p);
	}

	long counter = 0;

	List<Type> explodeType(Type t) {
		List<Type> res = new LinkedList<Type>();
		if (t instanceof ReferenceType) {
			for (Type el : ((ReferenceType) t).getElementTypeList()) {
				res.addAll(explodeType(el));
			}
		} else {
			res.add(t);
		}
		return res;
	}

	List<Type> explodeType(List<Type> types) {
		List<Type> res = new LinkedList<Type>();
		for (Type t : types) {
			res.addAll(explodeType(t));
		}
		return res;
	}

	List<Variable> createVarsForTuple(String basename, Type t) {
		List<Variable> res = new LinkedList<Variable>();
		for (Type ext : explodeType(t)) {
			res.add(new Variable(basename + (counter++), ext));
		}
		return res;
	}

	List<Variable> explode(Variable v, Map<Variable, List<Variable>> map) {
		if (!map.containsKey(v)) {
			counter = 0;
			map.put(v, createVarsForTuple(v.getName(), v.getType()));
		}
		return map.get(v);
	}

	List<Variable> explode(Collection<Variable> in, Map<Variable, List<Variable>> map) {
		LinkedList<Variable> tmp = new LinkedList<Variable>();
		for (Variable v : in) {
			tmp.addAll(explode(v, map));
		}
		return tmp;
	}

	/**
	 * Each call statement gets a unique ID. Every time we call a method,
	 * we pass the ID of the caller as an argument. This ID is also
	 * passed into every pull and push.
	 * 
	 * @param p
	 */
	private void insertGlobalHeapCounter(Program p) {

		//TODO: create a map from classconst to int
		
		// start from 1 because zero is reserved for main.
		Map<Method, Map<Variable, List<Variable>>> explosionMaps = new HashMap<Method, Map<Variable, List<Variable>>>();

		Map<Method, List<Variable>> origInParams = new HashMap<Method, List<Variable>>();
		Map<Method, List<Variable>> origOutParams = new HashMap<Method, List<Variable>>();

		for (Method m : p.getMethods()) {
			origInParams.put(m, new LinkedList<Variable>(m.getInParams()));
			origOutParams.put(m, new LinkedList<Variable>(m.getOutParams()));

			Map<Variable, List<Variable>> exploded = new HashMap<Variable, List<Variable>>();
			explosionMaps.put(m, exploded);
			List<Variable> newIn = explode(m.getInParams(), exploded);
			m.getInParams().clear();
			m.getInParams().addAll(newIn);

			List<Variable> newOut = explode(m.getOutParams(), exploded);
			m.getOutParams().clear();
			m.getOutParams().addAll(newOut);
			
			List<Variable> newLocals = explode(m.getLocals(), exploded);
			m.getLocals().clear();
			m.getLocals().addAll(newLocals);

			List<Type> expl = new LinkedList<Type>(explodeType(m.getReturnType()));
			m.getReturnType().clear();
			m.getReturnType().addAll(expl);
		}

		for (ClassVariable cv : p.getClassVariables()) {
			// TODO: explode the fields.
		}

		for (Method m : p.getMethods()) {
			Map<Variable, List<Variable>> map = explosionMaps.get(m);
			for (CfgBlock b : m.vertexSet()) {
				List<Statement> newBody = new LinkedList<Statement>();

				for (Statement s : b.getStatements()) {
					SourceLocation loc = s.getSourceLocation();

					if (s instanceof CallStatement) {
						CallStatement cs = (CallStatement) s;
						cs.getArguments();

						//TODO: this will not work if the receiver
						//is a reference with more fields than what
						//is declared in the returnType.
						//Same problem for the args.
						List<Expression> newReceiver = substituteExpressionList(cs.getReceiver(), map);
						List<Expression> newArgs = new LinkedList<Expression>();
						int ctr = 0;
						for (Expression e : cs.getArguments()) {
							if (e instanceof IdentifierExpression) {
								if (map.containsKey(((IdentifierExpression) e).getVariable())) {
									List<Variable> sub = map.get(((IdentifierExpression) e).getVariable());
									for (Variable subVar : sub) {
										newArgs.add(new IdentifierExpression(e.getSourceLocation(), subVar));
									}
								} else {
									newArgs.add(e);
								}
							} else if (e instanceof NullLiteral) {
								ReferenceType realType = (ReferenceType) origInParams.get(cs.getCallTarget()).get(ctr)
										.getType();
								newArgs.addAll(createNullTupleForType(realType));
							} else {
								newArgs.add(e);
							}
							ctr++;
						}
						newBody.add(new CallStatement(loc, cs.getCallTarget(), newArgs, newReceiver));
					} else if (s instanceof AssignStatement) {
						AssignStatement as = (AssignStatement) s;
						if (as.getLeft().getType() instanceof ReferenceType) {
							Variable leftVar = ((IdentifierExpression) as.getLeft()).getVariable();
							if (as.getRight() instanceof NullLiteral) {
								// create a null equivalent for each tuple
								// field.
								int i = 0;
								for (Expression e : createNullTupleForType((ReferenceType) as.getLeft().getType())) {
									IdentifierExpression l = new IdentifierExpression(loc, map.get(leftVar).get(i++));
									newBody.add(new AssignStatement(s.getSourceLocation(), l, e));
								}
							} else if (as.getRight() instanceof IdentifierExpression) {
								IdentifierExpression rid = (IdentifierExpression) as.getRight();
								for (int i = 0; i < map.get(leftVar).size(); i++) {
									if (i < map.get(rid.getVariable()).size()) {
										newBody.add(new AssignStatement(s.getSourceLocation(),
												new IdentifierExpression(loc, map.get(leftVar).get(i)),
												new IdentifierExpression(loc, map.get(rid.getVariable()).get(i))));
									} else {
										Variable local = new Variable("_"+map.get(leftVar).get(i).getName()+"$"+i, map.get(leftVar).get(i).getType()); 
										m.getLocals().add(local);										
										newBody.add(new AssignStatement(s.getSourceLocation(),
												new IdentifierExpression(loc, map.get(leftVar).get(i)),
												new IdentifierExpression(loc, local)));
									}
								}
							}
						} else {
							newBody.add(new AssignStatement(s.getSourceLocation(),
									redoExpression(as.getLeft(), map),
									redoExpression(as.getRight(), map) ));
						}
					} else if (s instanceof AssertStatement) {
						AssertStatement as = (AssertStatement) s;
						newBody.add(new AssertStatement(loc, redoExpression(as.getExpression(), map)));
					} else if (s instanceof AssumeStatement) {
						AssumeStatement as = (AssumeStatement) s;
						newBody.add(new AssumeStatement(loc, redoExpression(as.getExpression(), map)));
					} else if (s instanceof NewStatement) {
						NewStatement ns = (NewStatement)s;
						ns.getCounterVar();
//						ns.getClassVariable()
//						ns.getLeft().getVariable();
throw new RuntimeException("Not finished");						
					} else if (s instanceof PullStatement) {
						PullStatement ps = (PullStatement)s;
						throw new RuntimeException("Not finished");
					} else if (s instanceof PushStatement) {
						PushStatement ps = (PushStatement)s;
						throw new RuntimeException("Not finished");
					}
				}

			}
		}
		// System.err.println(p);
	}
	
	private Expression redoExpression(Expression e, Map<Variable, List<Variable>> map) {
		if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) e;
			return new BinaryExpression(e.getSourceLocation(), be.getOp(), redoExpression(be.getLeft(), map),
					redoExpression(be.getRight(), map));
		} else if (e instanceof BooleanLiteral || e instanceof IntegerLiteral) {
			return e.deepCopy();
		} else if (e instanceof IdentifierExpression) {
			if (map.containsKey(((IdentifierExpression) e).getVariable())) {
				// pick the first ... that's always good.
				return new IdentifierExpression(e.getSourceLocation(),
						map.get(((IdentifierExpression) e).getVariable()).get(0));
			} else {
				return e.deepCopy();
			}
		} else if (e instanceof IteExpression) {
			IteExpression ite = (IteExpression) e;
			return new IteExpression(e.getSourceLocation(), redoExpression(ite.getCondition(), map),
					redoExpression(ite.getThenExpr(), map), redoExpression(ite.getElseExpr(), map));
		} else if (e instanceof NullLiteral) {
			return e.deepCopy();
		} else if (e instanceof TupleAccessExpression) {
			TupleAccessExpression te = (TupleAccessExpression) e;
			return new IdentifierExpression(e.getSourceLocation(),
					map.get(te.getVariable()).get(te.getAccessPosition()));
		} else if (e instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression) e;
			return new UnaryExpression(ue.getSourceLocation(), ue.getOp(), redoExpression(ue.getExpression(), map));
		}
		throw new RuntimeException("not implemented");
	}

	private List<Expression> createNullTupleForType(ReferenceType t) {
		List<Expression> nullExpr = new LinkedList<Expression>();
		for (Type el : explodeType(t)) {
			if (el instanceof BoolType) {
				nullExpr.add(BooleanLiteral.falseLiteral());
			} else {
				nullExpr.add(IntegerLiteral.zero());
			}
		}
		return nullExpr;
	}

	private List<Expression> substituteExpressionList(List<Expression> in, Map<Variable, List<Variable>> map) {
		List<Expression> newReceiver = new LinkedList<Expression>();
		for (Expression e : in) {
			if (e instanceof IdentifierExpression) {
				if (map.containsKey(((IdentifierExpression) e).getVariable())) {
					List<Variable> sub = map.get(((IdentifierExpression) e).getVariable());
					for (Variable subVar : sub) {
						newReceiver.add(new IdentifierExpression(e.getSourceLocation(), subVar));
					}
				} else {
					newReceiver.add(e);
				}
			} else if (e instanceof NullLiteral) {
				throw new RuntimeException("Not implemented");
			} else {
				newReceiver.add(e);
			}
		}
		return newReceiver;
	}

}
