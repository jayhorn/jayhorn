/**
 * 
 */
package jayhorn.util;

import java.util.LinkedList;
import java.util.List;

import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
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
//				new CfgBlock(method);
				CfgBlock block = new CfgBlock(method);
				SourceLocation loc = method.getLocation();

				if (method.getMethodName().contains("<init>")) {
					//TODO
					Variable thisPointer = method.getInParams().get(0);
					ReferenceType rt = (ReferenceType)thisPointer.getType();
					
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
					PushStatement pack = new PushStatement(loc, rt.getClassVariable(), new IdentifierExpression(loc, thisPointer), rhs);
					block.addStatement(pack);
				}
				
//				AssignStatement asn = new AssignStatement(loc,
//						new IdentifierExpression(loc, program.getExceptionGlobal()), new IdentifierExpression(loc,
//								program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
//				block.addStatement(asn);

				//TODO: push an update to the globals.
//				ClassVariable c = ((ReferenceType) program.getExceptionGlobal().getType()).getClassVariable();
//				List<Expression> rhs = new LinkedList<Expression>();
//				rhs.add(new IdentifierExpression(loc,
//						program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
//				PushStatement push = new PushStatement(loc, c,
//						new IdentifierExpression(loc, program.getExceptionGlobal()), rhs);
//				block.addStatement(push);

//				Verify.verifyNotNull(method.getSource());
//				
//				System.err.println("**********  " + method.getMethodName());
				
				// throw new RuntimeException("The checker currently expects
				// that all methods have a body. Go and create a stub during
				// translation for "+method.getMethodName());
			} else if (method.isProgramEntryPoint()) {
				if (method.getInParams().size() == 1 && method.getMethodName().contains("main")) {
					Variable argsParam = method.getInParams().get(0);
					ReferenceType argsType = (ReferenceType) argsParam.getType();
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
					ClassVariable c = ((ReferenceType) argsParam.getType()).getClassVariable();
					rhs.add(new IdentifierExpression(loc, c));
					// this is an array, so initialize the remaining fields with
					// sth as well
					// TODO:
					int i = 0;
					while (rhs.size() < c.getAssociatedFields().length) {
						Variable undefLocal = new Variable("undef_field" + (i++), IntType.instance());
						rhs.add(new IdentifierExpression(loc, undefLocal));
					}
					PushStatement pack = new PushStatement(loc, c, new IdentifierExpression(loc, argsParam), rhs);
					entry.addStatement(1, pack);
				}
			}
		}
		
		
		
	}

}
