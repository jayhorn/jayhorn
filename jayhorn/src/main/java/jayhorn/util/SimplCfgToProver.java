/**
 * 
 */
package jayhorn.util;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverType;
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
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class SimplCfgToProver implements ICfgToProver {

	private int dummyvarcounter = 0;
	private final Prover prover;
	private final Map<Variable, Map<Integer, ProverExpr>> ssaVariableMap = new HashMap<Variable, Map<Integer, ProverExpr>>();
	private Map<ProverExpr, Integer> usedUniqueVariables = new HashMap<ProverExpr, Integer>();

	public SimplCfgToProver(Prover p) {
		prover = p;
		createHelperFunctions();
	}
	
	/* (non-Javadoc)
	 * @see jayhorn.util.ICfgToProver#finalize()
	 */
	@Override
	public List<ProverExpr> generatedAxioms() {
		List<ProverExpr> axioms = new LinkedList<ProverExpr>();
		// now add assertions to ensure that all unique variables are different.
		for (Entry<ProverExpr, Integer> entry : usedUniqueVariables.entrySet()) {
			axioms.add(prover.mkEq(entry.getKey(), prover.mkLiteral(entry.getValue())));
		}
		return axioms;
	}
	

	/* (non-Javadoc)
	 * @see jayhorn.util.ICfgToProver#statementToTransitionRelation(soottocfg.cfg.statement.Statement)
	 */
	@Override
	public ProverExpr statementListToTransitionRelation(List<Statement> stmts) {
		if (stmts.isEmpty()) {
			return prover.mkLiteral(true);
		}
		List<ProverExpr> conj = new LinkedList<ProverExpr>(); 
		for (Statement s : stmts) {
			ProverExpr pe = statementToTransitionRelation(s);
			if (pe==null) {
				//TODO: debug code
				continue;
			}
			conj.add(pe);
		}
		return prover.mkAnd(conj.toArray(new ProverExpr[conj.size()]));
	}
	
	/* (non-Javadoc)
	 * @see jayhorn.util.ICfgToProver#statementToTransitionRelation(soottocfg.cfg.statement.Statement)
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
			CallStatement cs = (CallStatement)s;
			if (cs.getReceiver().isPresent()) {
				return prover.mkEq(expressionToProverExpr(cs.getReceiver().get()), prover.mkVariable("dummy"+(dummyvarcounter++), lookupProverType(cs.getReceiver().get().getType())));		
			}
			return null;
		} else if (s instanceof ArrayReadStatement) {
			ArrayReadStatement ar = (ArrayReadStatement)s;
			MapType mt = (MapType)ar.getBase().getType();			
			return prover.mkEq(expressionToProverExpr(ar.getLeftValue()), prover.mkVariable("dummy"+(dummyvarcounter++), lookupProverType(mt.getValueType())));
		} else if (s instanceof ArrayStoreStatement) {
			// TODO
		} else {
			// TODO ignore all other statements?
			return null;
		}
		return null; // TODO: these are hacks. Later, this must not return null.
	}

	/* (non-Javadoc)
	 * @see jayhorn.util.ICfgToProver#expressionToProverExpr(soottocfg.cfg.expression.Expression)
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
				throw new RuntimeException("Implement me :(");
			default: {
//				Div("/"), Mod("%"),  Xor("^"),Shl("<<"), Shr(">>"), Ushr("u>>"), BOr("|"), BAnd("&");
				return uninterpretedBinOpToProverExpr(be.getOp(), left, right);
//				throw new RuntimeException("Not implemented for " + be.getOp());
			}
			}
		} else if (e instanceof BooleanLiteral) {
			return prover.mkLiteral(((BooleanLiteral) e).getValue());
		} else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression) e;
			ie.getVariable();
			ie.getIncarnation();
			if (!ssaVariableMap.containsKey(ie.getVariable())) {
				ssaVariableMap.put(ie.getVariable(), new HashMap<Integer, ProverExpr>());
			}
			if (!ssaVariableMap.get(ie.getVariable()).containsKey(ie.getIncarnation())) {
				ProverExpr ssaVar = prover.mkVariable(ie.getDefVariables() + "__" + ie.getIncarnation(),
						lookupProverType(ie.getType()));
				ssaVariableMap.get(ie.getVariable()).put(ie.getIncarnation(), ssaVar);
			}
			if (ie.getVariable().isUnique()) {
				// If this is a unique variable, remember it and add axioms
				// later that ensure that
				// all unique variables are different.
				usedUniqueVariables.put(ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation()), usedUniqueVariables.size());
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
	 * Div("/"), Mod("%"),  Xor("^"),Shl("<<"), Shr(">>"), Ushr("u>>"), BOr("|"), BAnd("&")
	 * We translate these binops into applications of the uninterpreted function
	 * 'uninterpretedBinOpToProverExpr' which takes three arguments:
	 * The first argument is an integer constant to represent the operand (for
	 * simplicity, we just use the hashcode of the enum), the second and third
	 * argument are the operands of the binop.
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	private ProverExpr uninterpretedBinOpToProverExpr(BinaryOperator op, ProverExpr left, ProverExpr right) {		
		return uninterpretedBinOp
				.mkExpr(new ProverExpr[] { prover.mkLiteral(op.hashCode()), left, right });
	}
	
	private ProverType lookupProverType(Type t) {
		if (t == BoolType.instance()) {
			return prover.getBooleanType();
		}
		return prover.getIntType();
	}
	
	ProverFun arrayLength, uninterpretedBinOp;

	private void createHelperFunctions() {
		// TODO: change the type of this
		arrayLength = prover.mkUnintFunction("$arrayLength", new ProverType[] { prover.getIntType() },
				prover.getIntType());
		
		/**
		 * this is used for bit operations, etc. the first argument
		 * is a number to distinguish the operators, the other two 
		 * are the operands from the binop.
		 */
		uninterpretedBinOp = prover.mkUnintFunction("$uninterpreted", new ProverType[] { prover.getIntType(), prover.getIntType(), prover.getIntType() },
				prover.getIntType());
		
	}

}
