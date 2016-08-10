/**
 * 
 */
package jayhorn.util;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Verify;

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
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;

/**
 * @author schaef
 *
 */
public class CfgStubber {

	public void stubUnboundFieldsAndMethods(Program program) {
		for (Method method : program.getMethods()) {
			if (method.getSource() == null) {
				CfgBlock block = new CfgBlock(method);
				SourceLocation loc = method.getLocation();

				AssignStatement asn = new AssignStatement(loc,
						new IdentifierExpression(loc, program.getExceptionGlobal()), new IdentifierExpression(loc,
								program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
				block.addStatement(asn);

				ClassVariable c = ((ReferenceType) program.getExceptionGlobal().getType()).getClassVariable();
				List<Expression> rhs = new LinkedList<Expression>();
				rhs.add(new IdentifierExpression(loc,
						program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
				PushStatement pack = new PushStatement(loc, c,
						new IdentifierExpression(loc, program.getExceptionGlobal()), rhs);
				block.addStatement(pack);

				Verify.verifyNotNull(method.getSource());
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
