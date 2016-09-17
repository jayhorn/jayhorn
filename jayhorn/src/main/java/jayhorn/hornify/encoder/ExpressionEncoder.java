/**
 * 
 */
package jayhorn.hornify.encoder;

import java.math.BigInteger;
import java.util.Map;

import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornHelper;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class ExpressionEncoder {

	private final Prover p;	
	private final HornEncoderContext hornContext;
	/**
	 * 
	 */
	public ExpressionEncoder(Prover p, HornEncoderContext hornContext) {
		this.p = p;
		this.hornContext = hornContext;
	}

	public HornEncoderContext getContext() {
		return this.hornContext;
	}
	
	public ProverExpr exprToProverExpr(Expression e, Map<Variable, ProverExpr> varMap) {
		if (e instanceof IdentifierExpression) {
			Variable var = ((IdentifierExpression) e).getVariable();
			if (var instanceof ClassVariable) {
				return p.mkLiteral(hornContext.getTypeID((ClassVariable)var));
			} else {
				return HornHelper.hh().findOrCreateProverVar(p, var, varMap);
			}
		} else if (e instanceof IntegerLiteral) {
			return p.mkLiteral(BigInteger.valueOf(((IntegerLiteral) e).getValue()));
		} else if (e instanceof NullLiteral) {
			return p.mkLiteral(BigInteger.valueOf(1234));			
		} else if (e instanceof BinaryExpression) {
			final BinaryExpression be = (BinaryExpression) e;
			final ProverExpr left = exprToProverExpr(be.getLeft(), varMap);
			final ProverExpr right = exprToProverExpr(be.getRight(), varMap);

			// TODO: the following choices encode Java semantics
			// of various operators; need a good schema to choose
			// how precise the encoding should be (probably
			// configurable)
			switch (be.getOp()) {
			case Plus:
				return p.mkPlus(left, right);
			case Minus:
				return p.mkMinus(left, right);
			case Mul:
				return p.mkMult(left, right);
			case Div:
				return p.mkTDiv(left, right);
			case Mod:
				return p.mkTMod(left, right);
			case Eq:
				return p.mkEq(left, right);
			case Ne:
				return p.mkNot(p.mkEq(left, right));
			case Gt:
				return p.mkGt(left, right);
			case Ge:
				return p.mkGeq(left, right);
			case Lt:
				return p.mkLt(left, right);
			case Le:
				return p.mkLeq(left, right);
			case PoLeq:
				if ((be.getRight() instanceof IdentifierExpression)
						&& (((IdentifierExpression) be.getRight()).getVariable() instanceof ClassVariable)) {

					final ClassVariable var = (ClassVariable) ((IdentifierExpression) be.getRight()).getVariable();

					ProverExpr disj = p.mkLiteral(false);
					for (Integer i : this.hornContext.getSubtypeIDs(var)) {
						disj = p.mkOr(disj, p.mkEq(left, p.mkLiteral(i)));
					}

					return disj;
				} else {
					throw new RuntimeException("instanceof is only supported for concrete types");
				}
			case And:
				return p.mkAnd(left, right);
			case Or:
				return p.mkOr(left, right);
			case Implies:
				return p.mkImplies(left, right);
			case Shl:
			case Shr:
			case BAnd:
			case BOr:
			case Xor:
				return p.mkVariable("HACK_FreeVar" + HornHelper.hh().newVarNum(), p.getIntType());
			// Verify.verify(left.getType()==p.getIntType() &&
			// right.getType()==p.getIntType());
			// return binopFun.mkExpr(new ProverExpr[]{left, right});
			default: {
				throw new RuntimeException("Not implemented for " + be.getOp());
			}
			}
		} else if (e instanceof UnaryExpression) {
			final UnaryExpression ue = (UnaryExpression) e;
			final ProverExpr subExpr = exprToProverExpr(ue.getExpression(), varMap);

			// TODO: the following choices encode Java semantics
			// of various operators; need a good schema to choose
			// how precise the encoding should be (probably
			// configurable)
			switch (ue.getOp()) {
			case Neg:
				return p.mkNeg(subExpr);
			case LNot:
				return p.mkNot(subExpr);
			}
		} else if (e instanceof IteExpression) {
			final IteExpression ie = (IteExpression) e;
			final ProverExpr condExpr = exprToProverExpr(ie.getCondition(), varMap);
			final ProverExpr thenExpr = exprToProverExpr(ie.getThenExpr(), varMap);
			final ProverExpr elseExpr = exprToProverExpr(ie.getElseExpr(), varMap);
			return p.mkIte(condExpr, thenExpr, elseExpr);
		} else if (e instanceof BooleanLiteral) {
			return p.mkLiteral(((BooleanLiteral) e).getValue());
		}
		throw new RuntimeException("Expression type " + e + " not implemented!");
	}	
	
}
