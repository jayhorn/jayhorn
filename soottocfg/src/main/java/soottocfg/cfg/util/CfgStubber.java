/**
 * 
 */
package soottocfg.cfg.util;

import java.util.LinkedList;
import java.util.List;

import soot.SootMethod;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.TupleType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class CfgStubber {
	
	public void stubUnboundFieldsAndMethods(Program program) {
		for (Method method : program.getMethods()) {
			if (method.getSource() == null) {
				/*
				 * If the method does not have a body, we just add a non-det assignment to the 
				 * exception global to indicate that this method might have thrown an exception.
				 * 
				 * TODO: We should add some support to look for user provided specs here!
				 */
				CfgBlock block = new CfgBlock(method);
				SourceLocation loc = method.getLocation();
				
				System.out.println("STUBBING METHOD " + method.getMethodName());
				System.out.println("Out param: " + method.getOutParam());
				System.out.println("Return types: " + method.getReturnType());

				if (method.getMethodName().contains(SootMethod.constructorName)) {
					//TODO
					Variable thisPointer = method.getInParams().get(0);
					ReferenceType rt = getRefTypeFrom(thisPointer);
					
					List<Expression> rhs = new LinkedList<Expression>();
					int i=0;
					for (Variable v : rt.getClassVariable().getAssociatedFields()) {
						if (v.getName().contains(SootTranslationHelpers.typeFieldName)) {
							//Make sure that we set the correct dynamic type.
							rhs.add(new IdentifierExpression(loc, rt.getClassVariable()));
						} else {
							Variable undefLocal = new Variable("undef_field" + (i++), IntType.instance());
							rhs.add(new IdentifierExpression(loc, undefLocal));
						}
//						System.err.println("********* "+v);						
					}
					PushStatement push = new PushStatement(loc, rt.getClassVariable(), new IdentifierExpression(loc, thisPointer), rhs);
					block.addStatement(push);
				} else if (method.getMethodName().contains(SootTranslationHelpers.HavocClassName)) {

					// add push with undef values to havoc methods
					Type t = method.getReturnType().get(0);
					if (t instanceof ReferenceType) {
						ReferenceType rt = (ReferenceType) t;
						System.out.println("Adding push for return type " + rt);
						List<Expression> rhs = new LinkedList<Expression>();
						int i=0;
						for (Variable v : rt.getClassVariable().getAssociatedFields()) {
							if (v.getName().contains(SootTranslationHelpers.typeFieldName)) {
								//Make sure that we set the correct dynamic type.
								rhs.add(new IdentifierExpression(loc, rt.getClassVariable()));
							} else {
								Variable undefLocal = new Variable("undef_field" + (i++), IntType.instance());
								rhs.add(new IdentifierExpression(loc, undefLocal));
							}
							//							System.err.println("********* "+v);						
						}
						Variable outVar = new Variable(MethodInfo.returnVariableName, rt);
						LinkedList<Variable> rets = new LinkedList<Variable>();
						rets.add(outVar);
						method.setOutParam(rets);
						IdentifierExpression ret = new IdentifierExpression(loc, outVar);
						PushStatement push = new PushStatement(loc, rt.getClassVariable(), ret, rhs);
						block.addStatement(push);
					}
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
					rhs.add(new IdentifierExpression(loc, argsType.getClassVariable()));
					ClassVariable c = argsType.getClassVariable();
					rhs.add(new IdentifierExpression(loc, c));
					// this is an array, so initialize the remaining fields with
					// sth as well
					// TODO:
					int i = 0;
					while (rhs.size() < c.getAssociatedFields().length) {
						Variable undefLocal = new Variable("undef_field" + (i++), IntType.instance());
						rhs.add(new IdentifierExpression(loc, undefLocal));
					}
					PushStatement push = new PushStatement(loc, c, new IdentifierExpression(loc, argsParam), rhs);
					entry.addStatement(1, push);
				}
			}
		}
	}

	private ReferenceType getRefTypeFrom(Variable var) {
		ReferenceType rt;
		if (soottocfg.Options.v().useTupleEncoding()) {
			rt = (ReferenceType)((TupleType)var.getType()).getElementTypes().get(0);
		} else {
			rt = (ReferenceType)var.getType();
		}
		return rt;
	}
}
