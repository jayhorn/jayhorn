/**
 * 
 */
package jayhorn.hornify.encoder;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import com.google.common.base.Verify;

import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornHelper;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverType;
import jayhorn.solver.ProverTupleType;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.Program;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.TupleAccessExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.expression.literal.StringLiteral;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class ExpressionEncoder {

	private final Prover p;
	private final HornEncoderContext hornContext;

	private final StringEncoder stringEncoder;

	/**
	 * 
	 */
	public ExpressionEncoder(Prover p, HornEncoderContext hornContext) {
		this.p = p;
		this.hornContext = hornContext;
		this.stringEncoder = new StringEncoder(p, HornHelper.hh().getStringADT());
	}

	public StringEncoder getStringEncoder() {
		return stringEncoder;
	}

	public HornEncoderContext getContext() {
		return this.hornContext;
	}

        public static class OverApproxException extends RuntimeException {}

	/**
	 * TODO: this is a hack!
	 * 
	 * @param id
	 * @return
	 */
	private ProverExpr typeIdToProverExpr(int id) {
		return p.mkLiteral(id);
	}
	
        private ProverExpr varToProverExpr(Variable var, Map<Variable, ProverExpr> varMap) {
            if (var instanceof ClassVariable) {
                return typeIdToProverExpr(hornContext.getTypeID((ClassVariable) var));
            } else {
                if (hornContext.elimOverApprox() && Program.isAbstractedVariable(var))
                    throw new OverApproxException();
                final ProverExpr proverVar = HornHelper.hh().findOrCreateProverVar(p, var, varMap);
                if (hornContext.getProgram().getGlobalVariables().contains(var)) {
                    /*
                     * For globals, we just use an integer number.
                     * NOTE: The translation guarantees that this number is unique and different
                     * from Null.
                     */
                    int idx = hornContext.getProgram().getGlobalVariables().indexOf(var);
                    return makeUniqueReference(p, var, proverVar, -idx - 1);
                }
                
                return proverVar;
            }
        }

	public List<ProverHornClause> getExtraEncodedClauses() {
		return stringEncoder.getEncodedClauses();
	}

	public ProverExpr exprToProverExpr(Expression e, Map<Variable, ProverExpr> varMap) {
		if (e instanceof StringLiteral) {	// check before (e instanceof IdentifierExpression)
//			return stringEncoder.mkString(((StringLiteral) e).getValue());
			StringLiteral stl = (StringLiteral) e;
			ProverExpr str = stringEncoder.mkStringPE(stl.getValue());
			ProverExpr ref = varToProverExpr(stl.getVariable(), varMap);
//			stringEncoder.assertStringLiteral(ref, ste, (ReferenceType)e.getType());
//			return ref;
			return p.mkTupleUpdate(ref, 3, str);
		} else if (e instanceof IdentifierExpression) {
			Variable var = ((IdentifierExpression) e).getVariable();
                        return varToProverExpr(var, varMap);
		} else if (e instanceof TupleAccessExpression) {
			TupleAccessExpression tae = (TupleAccessExpression) e;
			ProverExpr tuple = varToProverExpr(tae.getVariable(), varMap);
			return p.mkTupleSelect(tuple, tae.getAccessPosition());
			// return p.mkVariable("HACK_FreeVar" + HornHelper.hh().newVarNum(),
			// HornHelper.hh().getProverType(p, tae.getType()));
		} else if (e instanceof IntegerLiteral) {
			return p.mkLiteral(BigInteger.valueOf(((IntegerLiteral) e).getValue()));
		} else if (e instanceof NullLiteral) {
			return p.mkLiteral(HornHelper.NullValue);
		} else if (e instanceof BinaryExpression) {
			final BinaryExpression be = (BinaryExpression) e;
			ProverExpr left = exprToProverExpr(be.getLeft(), varMap);
			// if (left instanceof ProverTupleType) {
			// //in that case only use the projection to the first element
			// //of the tuple.
			// left = p.mkTupleSelect(left, 0);
			// }
			ProverExpr right = exprToProverExpr(be.getRight(), varMap);
			// if (right instanceof ProverTupleType) {
			// //in that case only use the projection to the first element
			// //of the tuple.
			// Verify.verify(right instanceof ProverTupleExpr, be.getRight()+"
			// becomes " +right +" of type " + right.getType().getClass());
			// right = p.mkTupleSelect(right, 0);
			// }

			if (be.getLeft() instanceof NullLiteral && be.getRight() instanceof IdentifierExpression) {
				right = p.mkTupleSelect(right, 0);
			}
			if (be.getRight() instanceof NullLiteral && be.getLeft() instanceof IdentifierExpression) {
				left = p.mkTupleSelect(left, 0);
			}

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
		        if (left instanceof ProverTupleExpr) {
		            ProverTupleExpr tLeft = (ProverTupleExpr)left;
		            ProverTupleExpr tRight = (ProverTupleExpr)right;
		            /*
					TODO: this is sound if we assume that the first element of a tuple is the sound identifier.
						  does this make sense? it should be advantageous to use the other tuple components to differentiate objects
						  (also applies to case StringEq)
					*/
		            return p.mkEq(tLeft.getSubExpr(0), tRight.getSubExpr(0));
		        } else {
					return p.mkEq(left, right);
		        }
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
						// ProverExpr tmp =
						// p.mkTupleSelect(typeIdToProverExpr(i), 0);
						ProverExpr tmp = typeIdToProverExpr(i);
						disj = p.mkOr(disj, p.mkEq(left, tmp));
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
			case IndexInString:
				return stringEncoder.mkIndexInString(e, varMap);
			case Shl:
			case Shr:
			case Ushr:
			case BAnd:
			case BOr:
			case Xor:
                                if (hornContext.elimOverApprox())
                                    throw new OverApproxException();
				return p.mkHornVariable("HACK_FreeVar" + HornHelper.hh().newVarNum(), p.getIntType());
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

			/*
			TODO: the following choices encode Java semantics
				  of various operators; need a good schema to choose
				  how precise the encoding should be (probably
				  configurable)
			*/
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

	
        /**
         * For variables representing global constants, generate a unique reference
         * based on the index of the variable.
         */
        private ProverExpr makeUniqueReference(Prover p, Variable v,
                                               ProverExpr fullRef, int number) {
		ProverType pt = HornHelper.hh().getProverType(p, v.getType());
		if (pt instanceof ProverTupleType) {
                        Verify.verify(v.getType() instanceof ReferenceType);
                        final ClassVariable classVar =
                            ((ReferenceType)v.getType()).getClassVariable();
                        final ProverExpr classId =
                            typeIdToProverExpr(hornContext.getTypeID(classVar));
                        return
                            p.mkTupleUpdate(
                            p.mkTupleUpdate(fullRef,
                                            0, p.mkLiteral(number)),
                                            1, classId);
		} else if (pt instanceof jayhorn.solver.IntType) {
			return p.mkLiteral(number);
		} else {
			Verify.verify(false);
                        return fullRef;
		}
        }

}
