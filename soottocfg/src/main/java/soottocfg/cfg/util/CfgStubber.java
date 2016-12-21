package soottocfg.cfg.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.common.base.Verify;

import soot.SootMethod;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class CfgStubber {

	public void stubUnboundFieldsAndMethods(Program program) {
		Queue<Method> todo = new LinkedList<Method>();
		todo.addAll(Arrays.asList(program.getMethods()));
		while (!todo.isEmpty()) {
			Method method = todo.remove();

			if (method.getSource() == null) {
				/*
				 * If the method does not have a body, we just add a non-det
				 * assignment to the
				 * exception global to indicate that this method might have
				 * thrown an exception.
				 * 
				 * TODO: We should add some support to look for user provided
				 * specs here!
				 */
				CfgBlock block = new CfgBlock(method);
				SourceLocation loc = method.getLocation();

				if (method.getMethodName().contains(SootMethod.constructorName)) {
					//ensure that no exception is thrown
					LinkedList<Variable> outParams = new LinkedList<Variable>();
					//when stubbing, set the exceptional return to null.
					Variable exceptionalRetVar = new Variable("exc", method.getReturnType().get(0));
					outParams.add(exceptionalRetVar);

					AssignStatement noException = new AssignStatement(loc,
							new IdentifierExpression(loc, exceptionalRetVar),
							SootTranslationHelpers.v().getMemoryModel().mkNullConstant());
					block.addStatement(noException);
							
					Variable thisPointer = method.getInParams().get(0);
					ReferenceType rt = getRefTypeFrom(thisPointer);

					List<Expression> rhs = new LinkedList<Expression>();
					int i = 0;
					for (Variable v : rt.getClassVariable().getAssociatedFields()) {
						++i;
						Variable outVar = new Variable("$out"+i, v.getType());
						outParams.add(outVar);
						Variable other;
//						if (SootTranslationHelpers.isDynamicTypeVar(v)) {	
//							// Make sure that we set the correct dynamic type.
//							other = rt.getClassVariable();							
//						} else {
							other = new Variable("undef_field" + i, v.getType());							
//						}
						//assign the outParams to fresh values.
						block.addStatement(new AssignStatement(loc, 
								new IdentifierExpression(loc, outVar), 
								new IdentifierExpression(loc, other)));
						//add an element to the push.
						rhs.add(new IdentifierExpression(loc, other));
					}

					PushStatement push = new PushStatement(loc, rt.getClassVariable(),
							new IdentifierExpression(loc, thisPointer), rhs);
					block.addStatement(push);

					//verify that the size is correct.
					Verify.verify(outParams.size()==method.getReturnType().size(),
							outParams.size()+"!="+method.getReturnType().size());

					method.setOutParam(outParams);
				} else if (method.getReturnType().size() > 0) {
					LinkedList<Variable> rets = new LinkedList<Variable>();
					//when stubbing, set the exceptional return to null.
					//System.out.println("Return types of " + method.getMethodName() + ": " + method.getReturnType());
					Variable exceptionalRetVar = new Variable("exc", method.getReturnType().get(0));
					rets.add(exceptionalRetVar);
					AssignStatement noException = new AssignStatement(loc,
							new IdentifierExpression(loc, exceptionalRetVar),
							SootTranslationHelpers.v().getMemoryModel().mkNullConstant());
					block.addStatement(noException);

					//start from 1 because 0 is already the exceptional return.
					int f = 0;
					for (int i=1; i<method.getReturnType().size(); i++) {
						Type t = method.getReturnType().get(i);
						// add push with undef values to havoc methods
						if (t instanceof ReferenceType) {
							ReferenceType rt = (ReferenceType) t;
							List<Expression> rhs = new LinkedList<Expression>();

							for (Variable v : rt.getClassVariable().getAssociatedFields()) {
//								if (SootTranslationHelpers.isDynamicTypeVar(v)) {
//									// Make sure that we set the correct dynamic
//									// type.
//									rhs.add(new IdentifierExpression(loc, rt.getClassVariable()));
//								} else {
									Variable undefLocal = new Variable("undef_field" + (f++), v.getType());
									rhs.add(new IdentifierExpression(loc, undefLocal));
//								}
							}
							Variable outVar = new Variable(MethodInfo.returnVariableName, rt);
							
							rets.add(outVar);
							//TODO: needs testing!
							IdentifierExpression ret = new IdentifierExpression(loc, outVar);
							block.addStatement(new NewStatement(loc, ret, rt.getClassVariable()));
							
							PushStatement push = new PushStatement(loc, rt.getClassVariable(), ret, rhs);
							block.addStatement(push);
						} else {
							Variable outVar = new Variable(MethodInfo.returnVariableName, t);
							rets.add(outVar);
						}
					}
					method.setOutParam(rets);
				}

			} else if (method.isProgramEntryPoint()) {
				if (method.getInParams().size() == 1 && method.getMethodName().contains("main")) {
					Variable argsParam = method.getInParams().get(0);
					ReferenceType argsType = getRefTypeFrom(argsParam);
					CfgBlock entry = method.getSource();
					SourceLocation loc = method.getLocation();
					Variable sizeLocal = new Variable("undef_size", IntType.instance());
					AssumeStatement asm = new AssumeStatement(loc, new BinaryExpression(loc, BinaryOperator.Ge,
							new IdentifierExpression(loc, sizeLocal), IntegerLiteral.zero()));
					entry.addStatement(0, asm);

					// pack(JayHornArr12, r0, [JayHornArr12.$length,
					// JayHornArr12.$elType, JayHornArr12.$dynamicType])
					List<Expression> rhs = new LinkedList<Expression>();
					rhs.add(new IdentifierExpression(loc, sizeLocal));
					ClassVariable c = argsType.getClassVariable();
					rhs.add(new IdentifierExpression(loc, c));
					// this is an array, so initialize the remaining fields with
					// sth as well
					// TODO:
					int i = 0;
					while (rhs.size() < c.getAssociatedFields().length) {
						Variable undefLocal = new Variable("undef_field" + (i++), c.getAssociatedFields()[i].getType() /*IntType.instance()*/);
						rhs.add(new IdentifierExpression(loc, undefLocal));
					}
					PushStatement push = new PushStatement(loc, c, new IdentifierExpression(loc, argsParam), rhs);
					entry.addStatement(1, push);
				}
			}
		}
	}

	private ReferenceType getRefTypeFrom(Variable var) {
		return (ReferenceType) var.getType();
	}
}
