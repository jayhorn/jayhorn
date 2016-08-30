/**
 * 
 */
package jayhorn.util;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverType;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.ArrayLengthExpression;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.soot.util.SootTranslationHelpers;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author schaef
 *
 */
public class SimplCfgToProver implements ICfgToProver {

	private int dummyvarcounter = 0;
	private final Prover prover;
	private final Map<Variable, Map<Integer, ProverExpr>> ssaVariableMap = new HashMap<Variable, Map<Integer, ProverExpr>>();
	private Map<ProverExpr, Integer> usedUniqueVariables = new HashMap<ProverExpr, Integer>();

	private Map<ClassVariable, ProverExpr> usedClassVariables = new HashMap<ClassVariable, ProverExpr>();
	private Set<ProverExpr> instantiatePoAxiomsFor = new HashSet<ProverExpr>();

	public SimplCfgToProver(Prover p) {
		prover = p;
		createHelperFunctions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.util.ICfgToProver#finalize()
	 */
	@Override
	public List<ProverExpr> generatedAxioms() {
		List<ProverExpr> axioms = new LinkedList<ProverExpr>();
		// now add assertions to ensure that all unique variables are different.
		for (Entry<ProverExpr, Integer> entry : usedUniqueVariables.entrySet()) {
			axioms.add(prover.mkEq(entry.getKey(), prover.mkLiteral(entry.getValue())));
		}

		// TODO: brutal brute force hack
		// Make the partial order axioms.
		List<Entry<ClassVariable, ProverExpr>> classVarList = new LinkedList<Entry<ClassVariable, ProverExpr>>(
				usedClassVariables.entrySet());

		for (ProverExpr pe : instantiatePoAxiomsFor) {
			for (int i = 0; i < classVarList.size(); i++) {
				Entry<ClassVariable, ProverExpr> e1 = classVarList.get(i);
				for (int j = i + 1; j < classVarList.size(); j++) {
					Entry<ClassVariable, ProverExpr> e2 = classVarList.get(j);
					if (isSubtypeOf(e1.getKey(), e2.getKey())) {
						axioms.add(prover.mkImplies(poCompare.mkExpr(new ProverExpr[] { pe, e1.getValue() }),
								poCompare.mkExpr(new ProverExpr[] { pe, e2.getValue() })));
						// because we know they are not equal.
						axioms.add(prover.mkImplies(poCompare.mkExpr(new ProverExpr[] { pe, e1.getValue() }),
								prover.mkNot(poCompare.mkExpr(new ProverExpr[] { pe, e2.getValue() }))));
					} else if (isSubtypeOf(e1.getKey(), e2.getKey())) {
						axioms.add(prover.mkImplies(poCompare.mkExpr(new ProverExpr[] { pe, e2.getValue() }),
								poCompare.mkExpr(new ProverExpr[] { pe, e1.getValue() })));

						// because we know they are not equal.
						axioms.add(prover.mkImplies(poCompare.mkExpr(new ProverExpr[] { pe, e2.getValue() }),
								prover.mkNot(poCompare.mkExpr(new ProverExpr[] { pe, e1.getValue() }))));
					} else {
						axioms.add(prover.mkImplies(poCompare.mkExpr(new ProverExpr[] { pe, e1.getValue() }),
								prover.mkNot(poCompare.mkExpr(new ProverExpr[] { pe, e2.getValue() }))));
						axioms.add(prover.mkImplies(poCompare.mkExpr(new ProverExpr[] { pe, e2.getValue() }),
								prover.mkNot(poCompare.mkExpr(new ProverExpr[] { pe, e1.getValue() }))));

					}
				}
			}
		}
		return axioms;
	}

	public List<ProverExpr> generateParamTypeAxioms(Method m) {
		List<ProverExpr> axioms = new LinkedList<ProverExpr>();
		for (Variable v : m.getInParams()) {
			if (v.getType() instanceof ReferenceType) {
				ClassVariable cv = ((ReferenceType) v.getType()).getClassVariable();
				Expression typeGuard = new BinaryExpression(null, BinaryOperator.PoLeq,
						new IdentifierExpression(null, v, 0), new IdentifierExpression(null, cv));
				axioms.add(expressionToProverExpr(typeGuard));
			}
		}
		return axioms;
	}

	private boolean isSubtypeOf(ClassVariable sub, ClassVariable sup) {
		if (sub.equals(sup)) {
			return true;
		}
		for (ClassVariable parent : sub.getParents()) {
			if (isSubtypeOf(parent, sup))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.util.ICfgToProver#statementToTransitionRelation(soottocfg.cfg.
	 * statement.Statement)
	 */
	@Override
	public ProverExpr statementListToTransitionRelation(List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return prover.mkLiteral(true);
		}
		List<ProverExpr> conj = new LinkedList<ProverExpr>();
		for (Statement s : stmts) {
			ProverExpr pe = statementToTransitionRelation(s);
			if (pe == null) {
				// TODO: debug code
				continue;
			}
			conj.add(pe);
		}
		return prover.mkAnd(conj.toArray(new ProverExpr[conj.size()]));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.util.ICfgToProver#statementToTransitionRelation(soottocfg.cfg.
	 * statement.Statement)
	 */
	@Override
	public ProverExpr statementToTransitionRelation(Statement s) {
		if (s instanceof AssertStatement) {
			return expressionToProverExpr(((AssertStatement) s).getExpression());
		} else if (s instanceof AssignStatement) {
			ProverExpr l = expressionToProverExpr(((AssignStatement) s).getLeft());
			ProverExpr r = expressionToProverExpr(((AssignStatement) s).getRight());

			return prover.mkEq(l, r);
		} else if (s instanceof AssumeStatement) {
			return expressionToProverExpr(((AssumeStatement) s).getExpression());
		} else if (s instanceof CallStatement) {
			CallStatement cs = (CallStatement) s;
			if (!cs.getReceiver().isEmpty()) {
				if (cs.getReceiver().size() == 1) {
					return prover.mkEq(expressionToProverExpr(cs.getReceiver().get(0)), prover.mkVariable(
							"dummy" + (dummyvarcounter++), lookupProverType(cs.getReceiver().get(0).getType())));
				}
				throw new RuntimeException("Not implemented");
			}
			return null;
		} else if (s instanceof ArrayReadStatement) {
			ArrayReadStatement ar = (ArrayReadStatement) s;
			// TODO HACK
			if (ar.getIndices()[ar.getIndices().length - 1].toString().contains(SootTranslationHelpers.typeFieldName)) {
				// special handling for dynamic types
				ProverExpr arrAccess = prover.mkSelect(dynamicTypeArray,
						new ProverExpr[] { expressionToProverExpr(ar.getIndices()[0]) });
				return prover.mkEq(expressionToProverExpr(ar.getLeftValue()), arrAccess);
			}
			if (ar.getIndices()[ar.getIndices().length - 1].toString()
					.contains(SootTranslationHelpers.lengthFieldName)) {
				return prover.mkEq(expressionToProverExpr(ar.getLeftValue()), arrayLength.mkExpr(
						new ProverExpr[] { expressionToProverExpr(ar.getIndices()[ar.getIndices().length - 2]) }));
			}

			MapType mt = (MapType) ar.getBase().getType();
			return prover.mkEq(expressionToProverExpr(ar.getLeftValue()),
					prover.mkVariable("dummy" + (dummyvarcounter++), lookupProverType(mt.getValueType())));
		} else if (s instanceof ArrayStoreStatement) {
			// TODO
		} else {
			// TODO ignore all other statements?
		}
		return null; // TODO: these are hacks. Later, this must not return null.
	}

	int __hack_counter = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.util.ICfgToProver#expressionToProverExpr(soottocfg.cfg.expression
	 * .Expression)
	 */
	@Override
	public ProverExpr expressionToProverExpr(Expression e) {
		if (e instanceof ArrayLengthExpression) {
			return arrayLength
					.mkExpr(new ProverExpr[] { expressionToProverExpr(((ArrayLengthExpression) e).getExpression()) });
		} else if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) e;
			ProverExpr left = expressionToProverExpr(be.getLeft());
			ProverExpr right = expressionToProverExpr(be.getRight());
			switch (be.getOp()) {
			case Plus:
				return prover.mkPlus(left, right);
			case Minus:
				return prover.mkMinus(left, right);
			case Mul:
				return prover.mkMult(left, right);

			case Eq:
				return prover.mkEq(left, right);
			case Ne:
				return prover.mkNot(prover.mkEq(left, right));
			case Gt:
				return prover.mkGt(left, right);
			case Ge:
				return prover.mkGeq(left, right);
			case Lt:
				return prover.mkLt(left, right);
			case Le:
				return prover.mkLeq(left, right);

			case And:
				return prover.mkAnd(left, right);
			case Or:
				return prover.mkOr(left, right);
			case Implies:
				return prover.mkImplies(left, right);
			case PoLeq:
				// check we need to instantiate the po definition for the left
				// or right.
				if (be.getLeft() instanceof IdentifierExpression
						&& !(((IdentifierExpression) be.getLeft()).getVariable() instanceof ClassVariable)) {
					instantiatePoAxiomsFor.add(left);
				}
				if (be.getRight() instanceof IdentifierExpression
						&& !(((IdentifierExpression) be.getRight()).getVariable() instanceof ClassVariable)) {
					instantiatePoAxiomsFor.add(right);
				}

				return poCompare.mkExpr(new ProverExpr[] { left, right });

			default: {
				// Div("/"), Mod("%"), Xor("^"),Shl("<<"), Shr(">>"),
				// Ushr("u>>"), BOr("|"), BAnd("&");
				return uninterpretedBinOpToProverExpr(be.getOp(), left, right);
				// throw new RuntimeException("Not implemented for " +
				// be.getOp());
			}
			}
		} else if (e instanceof BooleanLiteral) {
			return prover.mkLiteral(((BooleanLiteral) e).getValue());
		} else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression) e;
			if (!ssaVariableMap.containsKey(ie.getVariable())) {
				ssaVariableMap.put(ie.getVariable(), new HashMap<Integer, ProverExpr>());
			}
			if (!ssaVariableMap.get(ie.getVariable()).containsKey(ie.getIncarnation())) {
				ProverExpr ssaVar = prover.mkVariable(ie.getVariable().getName() + "__" + ie.getIncarnation(),
						lookupProverType(ie.getType()));
				ssaVariableMap.get(ie.getVariable()).put(ie.getIncarnation(), ssaVar);
			}
			if (ie.getVariable().isUnique()) {
				// If this is a unique variable, remember it and add axioms
				// later that ensure that
				// all unique variables are different.
				if (ie.getVariable() instanceof ClassVariable) {
					ClassVariable cv = (ClassVariable) ie.getVariable();
					usedUniqueVariables.put(ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation()),
							usedUniqueVariables.size());
					usedClassVariables.put(cv, ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation()));
				} else {
					usedUniqueVariables.put(ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation()),
							usedUniqueVariables.size());
				}
			}
			return ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation());
		} else if (e instanceof IntegerLiteral) {
			return prover.mkLiteral(BigInteger.valueOf(((IntegerLiteral) e).getValue()));
		} else if (e instanceof IteExpression) {
			IteExpression ie = (IteExpression) e;
			return prover.mkIte(expressionToProverExpr(ie.getCondition()), expressionToProverExpr(ie.getThenExpr()),
					expressionToProverExpr(ie.getElseExpr()));
		} else if (e instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression) e;
			ProverExpr expr = expressionToProverExpr(ue.getExpression());

			if (ue.getOp() == UnaryOperator.LNot) {
				return prover.mkNot(expr);
			} else {
				assert (ue.getOp() == UnaryOperator.Neg);
				return prover.mkMult(prover.mkLiteral(-1), expr);
			}
		} else {
			throw new RuntimeException("unexpected expression type: " + e);
		}
	}

	/**
	 * This is a simple hack to abstract binops that we do not care about,
	 * such as bit operations. Currently, we ignore
	 * Div("/"), Mod("%"), Xor("^"),Shl("<<"), Shr(">>"), Ushr("u>>"), BOr("|"),
	 * BAnd("&")
	 * We translate these binops into applications of the uninterpreted function
	 * 'uninterpretedBinOpToProverExpr' which takes three arguments:
	 * The first argument is an integer constant to represent the operand (for
	 * simplicity, we just use the hashcode of the enum), the second and third
	 * argument are the operands of the binop.
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	private ProverExpr uninterpretedBinOpToProverExpr(BinaryOperator op, ProverExpr left, ProverExpr right) {
		return uninterpretedBinOp.mkExpr(new ProverExpr[] { prover.mkLiteral(op.hashCode()), left, right });
	}

	private ProverType lookupProverType(Type t) {
		if (t == BoolType.instance()) {
			return prover.getBooleanType();
		}
		return prover.getIntType();
	}

	ProverFun arrayLength, uninterpretedBinOp, poCompare;
	ProverExpr dynamicTypeArray;

	private void createHelperFunctions() {
		// TODO: change the type of this
		arrayLength = prover.mkUnintFunction("$arrayLength", new ProverType[] { prover.getIntType() },
				prover.getIntType());

		/**
		 * this is used for bit operations, etc. the first argument
		 * is a number to distinguish the operators, the other two
		 * are the operands from the binop.
		 */
		uninterpretedBinOp = prover.mkUnintFunction("$uninterpreted",
				new ProverType[] { prover.getIntType(), prover.getIntType(), prover.getIntType() },
				prover.getIntType());

		// TODO: add axioms
		poCompare = prover.mkUnintFunction("$poCompare", new ProverType[] { prover.getIntType(), prover.getIntType() },
				prover.getBooleanType());

		dynamicTypeArray = prover.mkVariable("$dyntype",
				prover.getArrayType(new ProverType[] { prover.getIntType() }, prover.getIntType()));

	}

}
