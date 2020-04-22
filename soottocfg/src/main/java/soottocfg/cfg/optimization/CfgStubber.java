package soottocfg.cfg.optimization;

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
import soottocfg.cfg.expression.TupleAccessExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
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
import soottocfg.soot.transformers.ArrayTransformer;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class CfgStubber {

        /**
         * White-list some cases where the stub is not an over-approximation
         */
        private boolean isActualStub(Method method) {
            String name = method.getMethodName();
            if (name.contains("Havoc_Class:") ||
                // this might be too broad
                name.contains("java.util.Random: void <init>") ||
                name.contains("void <init>()") ||
                name.contains("Exception: void <init>(java.lang.String)") ||
                name.contains("java.io.PrintStream: void println"))
                return false;
            return true;
        }

	public void stubUnboundFieldsAndMethods(Program program) {
		Queue<Method> todo = new LinkedList<Method>();
		todo.addAll(Arrays.asList(program.getMethods()));
		while (!todo.isEmpty()) {
			Method method = todo.remove();

			if (method.getSource() == null) {
				method.setStub(isActualStub(method));

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
					// ensure that no exception is thrown
					LinkedList<Variable> outParams = new LinkedList<Variable>();
					// when stubbing, set the exceptional return to null.
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
						Variable outVar = new Variable("$out" + i, v.getType());
						outParams.add(outVar);
						Variable other;
						other = new Variable("undef_field" + i, v.getType());

						// assign the outParams to fresh values.
						block.addStatement(new AssignStatement(loc, new IdentifierExpression(loc, outVar),
								new IdentifierExpression(loc, other)));
						// add an element to the push.
						rhs.add(new IdentifierExpression(loc, other));
					}

					PushStatement push = new PushStatement(loc, rt.getClassVariable(),
							new IdentifierExpression(loc, thisPointer), rhs);
					block.addStatement(push);

					// verify that the size is correct.
					Verify.verify(outParams.size() == method.getReturnType().size(),
							outParams.size() + "!=" + method.getReturnType().size());

					method.setOutParam(outParams);
				} else if (method.getReturnType().size() > 0) {
					LinkedList<Variable> rets = new LinkedList<Variable>();
					// when stubbing, set the exceptional return to null.
					// System.out.println("Return types of " +
					// method.getMethodName() + ": " + method.getReturnType());
					Variable exceptionalRetVar = new Variable("exc", method.getReturnType().get(0));
					rets.add(exceptionalRetVar);
					AssignStatement noException = new AssignStatement(loc,
							new IdentifierExpression(loc, exceptionalRetVar),
							SootTranslationHelpers.v().getMemoryModel().mkNullConstant());
					block.addStatement(noException);

					// start from 1 because 0 is already the exceptional return.
					int f = 0;
					for (int i = 1; i < method.getReturnType().size(); i++) {
						Type t = method.getReturnType().get(i);
						// add push with undef values to havoc methods
						if (t instanceof ReferenceType) {

							// Rody: because of issue 116, create
							// non-deterministic choice between
							// null and instance with undef fields
							CfgBlock caseNull = new CfgBlock(method);
							method.addEdge(block, caseNull);
							CfgBlock caseInstance = new CfgBlock(method);
							method.addEdge(block, caseInstance);
							
							ReferenceType rt = (ReferenceType) t;
							List<Expression> rhs = new LinkedList<Expression>();

							if (t.toString().contains(ArrayTransformer.arrayTypeName)) {
								// for an array, make sure we add the constraint
								// that length >= 0
								Variable sizeLocal = new Variable("undef_size", IntType.instance());
								AssumeStatement asm = new AssumeStatement(loc,
										new BinaryExpression(loc, BinaryOperator.Ge,
												new IdentifierExpression(loc, sizeLocal), IntegerLiteral.zero()));
								caseInstance.addStatement(0, asm);
								rhs.add(new IdentifierExpression(loc, sizeLocal));
								ClassVariable c = rt.getClassVariable();
								// TODO: @Rody: the dynamic type is not pushed
								// anymore.
								// rhs.add(new IdentifierExpression(loc, c));

								// this is an array, so initialize the remaining
								// fields with sth as well
								int n = rhs.size();
								while (n < c.getAssociatedFields().length) {
									Variable undefLocal = new Variable("undef_field" + (f++),
											c.getAssociatedFields()[n++].getType());
									rhs.add(new IdentifierExpression(loc, undefLocal));
								}
							} else {
								for (Variable v : rt.getClassVariable().getAssociatedFields()) {
									Variable undefLocal = new Variable("undef_field" + (f++), v.getType());
									rhs.add(new IdentifierExpression(loc, undefLocal));
								}
							}
							Variable outVar = new Variable(MethodInfo.returnVariableName, rt);

							rets.add(outVar);
							IdentifierExpression ret = new IdentifierExpression(loc, outVar);
							caseInstance.addStatement(new NewStatement(loc, ret, rt.getClassVariable()));

							PushStatement push = new PushStatement(loc, rt.getClassVariable(), ret, rhs);
							caseInstance.addStatement(push);

							caseNull.addStatement(new AssignStatement(loc, ret, new NullLiteral(loc)));

						} else {
							Variable outVar = new Variable(MethodInfo.returnVariableName, t);
							rets.add(outVar);
						}
					}
					method.setOutParam(rets);
					method.findOrCreateUniqueSink();
				}

			} else if (method.isProgramEntryPoint()) {
				if (method.getInParams().size() == 1 && method.getMethodName().contains("main")) {
					CfgBlock entry = method.getSource();
					SourceLocation loc = method.getLocation();

					Variable argsParam = method.getInParams().get(0);
					ReferenceType argsType = getRefTypeFrom(argsParam);
					ClassVariable c = argsType.getClassVariable();
					Verify.verify("JayArray_java_lang_String".equals(c.getName()),
							"Main needs a string array as argument");
					/*
					 * The length field is now part of the tuple.
					 */
					TupleAccessExpression refExpr = new TupleAccessExpression(loc, argsParam,
							ReferenceType.RefFieldName);
					TupleAccessExpression typeExpr = new TupleAccessExpression(loc, argsParam,
							ReferenceType.TypeFieldName);
					TupleAccessExpression lenExpr = new TupleAccessExpression(loc, argsParam,
							SootTranslationHelpers.lengthFieldName);

                                        /**
                                         * We add an assumption that the input array has address
                                         * 1 (which is guaranteed to be non-null), has
                                         * non-negative length, and has string array type
                                         */
					AssumeStatement asm = new AssumeStatement(loc,
                                          new BinaryExpression(loc, BinaryOperator.And,
                                          new BinaryExpression(loc, BinaryOperator.And,
                                            SootTranslationHelpers.createInstanceOfExpression(typeExpr, c),
                                            new BinaryExpression(loc, BinaryOperator.Ge, lenExpr, IntegerLiteral.zero())),
                                            new BinaryExpression(loc, BinaryOperator.Eq, refExpr, IntegerLiteral.one())));
					entry.addStatement(0, asm);

					// push(JayHornArr12, r0, [JayHornArr12.$length,
					// JayHornArr12.$elType, JayHornArr12.$dynamicType])

					List<Expression> rhs = new LinkedList<Expression>();
					/*
					 * TODO: @Rody, the push doesn't take the the dynamic
					 * type and size as first two parameters because stuff
					 * like this should be part of the tuples.
					 */
					// rhs.add(new IdentifierExpression(loc, sizeLocal));

					// rhs.add(new IdentifierExpression(loc, c));
					// this is an array, so initialize the remaining fields with
					// sth as well
					int i = 0;
					for (Variable fieldVar : c.getAssociatedFields()) {
						if (fieldVar.getName().equals(SootTranslationHelpers.lengthFieldName)) {
							// TODO: this should be unreachable later since the
							// associated
							// fields should not contain any final fields.
							rhs.add(new TupleAccessExpression(loc, argsParam,
									SootTranslationHelpers.lengthFieldName));
						} else {
							Variable undefLocal = new Variable("undef_field" + i++, fieldVar.getType());
							rhs.add(new IdentifierExpression(loc, undefLocal));
						}
					}

					IdentifierExpression argsLocal = new IdentifierExpression(loc, argsParam);

					// AssumeStatement asmNotNull = new AssumeStatement(loc,
					// new BinaryExpression(loc, BinaryOperator.Ne,
					// argsLocal, new NullLiteral(loc)));
					// entry.addStatement(1, asmNotNull);

					PushStatement push = new PushStatement(loc, c, argsLocal, rhs);
					entry.addStatement(1, push);
				}
			}
		}
	}

	private ReferenceType getRefTypeFrom(Variable var) {
		return (ReferenceType) var.getType();
	}
}
