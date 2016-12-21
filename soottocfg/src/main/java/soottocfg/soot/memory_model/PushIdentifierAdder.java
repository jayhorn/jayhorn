package soottocfg.soot.memory_model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.util.InterProceduralPullPushOrdering;
import soottocfg.cfg.variable.Variable;

/**
 * @author Rody Kersten
 *
 */
public class PushIdentifierAdder {

	private static boolean debug = false;

	public void addIDs(Program p) {
		InterProceduralPullPushOrdering ordering = new InterProceduralPullPushOrdering(p.getEntryPoint());
		Method[] ms = p.getMethods();
		for (Method m : ms) {
			
			if (debug) {
				System.out.println("Adding IDs to method " + m.getMethodName());
				// System.out.println(m);
			}

			Set<CfgBlock> blocks = m.vertexSet();
			for (CfgBlock b : blocks) {
				
				List<Statement> stmts = b.getStatements();
				
				for (int i = 0; i < stmts.size(); i++) {
					
					Statement s = stmts.get(i);
					
					if (s instanceof PullStatement) {
						PullStatement pull = (PullStatement) s;
						Set<PushStatement> pushes = ordering.getPushsInfluencing(pull);
						
						// Add push of havoced values on the fly if there is none (e.g. because the
						// object was returned by a library call).
						if (pushes.isEmpty()) {
							if (debug)
								System.out.println("Adding push on the fly for " + pull.getObject());
							ReferenceType rt = (ReferenceType) pull.getObject().getType();
							SourceLocation loc = pull.getSourceLocation();
							IdentifierExpression id = (IdentifierExpression) pull.getObject();

							List<Expression> rhs = new LinkedList<Expression>();
							int n = 0;
							for (Variable v : rt.getClassVariable().getAssociatedFields()) {
//								if (SootTranslationHelpers.isDynamicTypeVar(v)) {
//									//Make sure that we set the correct dynamic type.
//									rhs.add(new IdentifierExpression(loc, rt.getClassVariable()));
//								} else {
									Variable undefLocal = new Variable("undef_" + id + "_" + (n++), v.getType());
									rhs.add(new IdentifierExpression(loc, undefLocal));
//								}					
							}
							PushStatement push = new PushStatement(loc, rt.getClassVariable(), id, rhs);
							b.addStatement(i++,push);
							pushes.add(push);
							if (debug) {
								System.out.println("Added push: " + push);
								System.out.println(b);
							}
						}
						
						pull.canAffect(pushes);
						if (debug) {
							System.out.println("Pushes influencing " + pull + ": ");
							for (PushStatement push : pushes)
								System.out.println(push);
						}
					}
				}
			}

			if (debug)
				System.out.println("DONE:\n" + m);
		}
	}
}
