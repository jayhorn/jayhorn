package soottocfg.soot.memory_model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soottocfg.Options;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.util.InterProceduralPullPushOrdering;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author Rody Kersten
 *
 */
public class PushIdentifierAdder {

	private static boolean debug = false;

	public final static String LP = "lastpush";

	public void addIDs(Program p) {
		addGhostFieldToClasses(p);
		addToPushesAndPulls(p);
	}

	private void addGhostFieldToClasses(Program p) {
		Set<ClassVariable> cvs = p.getClassVariables();
		for (ClassVariable cv : cvs) {
			Variable lastpush = new Variable(LP, IntType.instance());
			cv.addGhostField(lastpush);
		}
	}

	private void addToPushesAndPulls(Program p) {
		InterProceduralPullPushOrdering ordering = new InterProceduralPullPushOrdering(p.getEntryPoints()[0]);
		Variable lp = new Variable(LP, IntType.instance());
		Method[] ms = p.getMethods();
		for (Method m : ms) {

			if (!hasPushOrPull(m))
				continue;

			if (debug) {
				System.out.println("Adding IDs to method " + m.getMethodName());
				// System.out.println(m);
			}

			// Add "lastpush" local
			Collection<Variable> locals = m.getLocals();
			for (Variable v : locals) {
				if (v.getName().equals(LP))
					throw new RuntimeException("Method " + m.getMethodName() + " contains a local named " + LP);
			}
			m.addLocalVariable(lp);

			Set<CfgBlock> blocks = m.vertexSet();
			for (CfgBlock b : blocks) {

				List<Statement> stmts = b.getStatements();

				for (int i = 0; i < stmts.size(); i++) {
					Statement s = stmts.get(i);
					if (s instanceof PullStatement) {
						PullStatement pull = (PullStatement) s;
						Set<PushStatement> pushes = ordering.getPushsInfluencing(pull);
						if (debug) {
							System.out.println("Pushes influencing " + pull + ": ");
							for (PushStatement push : pushes)
								System.out.println(push);
						}

						List<Expression> disj = new ArrayList<Expression>();
						for (PushStatement push : pushes) {
							Expression exp = new BinaryExpression(pull.getSourceLocation(), BinaryOperator.Eq,
									new IdentifierExpression(pull.getSourceLocation(), lp),
									new IntegerLiteral(pull.getSourceLocation(), push.getID()));
							disj.add(exp);
						}

						if (!disj.isEmpty()) {

							IdentifierExpression lpid = new IdentifierExpression(pull.getSourceLocation(), lp);
							SourceLocation loc = pull.getSourceLocation();

							// findBugs noted this is not actually used
//							List<Expression> args = new ArrayList<Expression>();
//							if (Options.v().passCallerIdIntoMethods()) {
//								args.add(new IntegerLiteral(loc, SootTranslationHelpers.v().getUniqueNumberForUnit(s)));
//							}

							Iterator<Expression> it = disj.iterator();
							Expression toAssume = it.next();
							while (it.hasNext()) {
								Expression toAdd = it.next();
								toAssume = new BinaryExpression(loc, BinaryOperator.Or, toAssume, toAdd);
							}
							Statement assume = new AssumeStatement(loc, toAssume);
							b.addStatement(++i, assume);
							pull.addGhostField(lpid);
							i++;
						}
					} else if (s instanceof PushStatement) {
						PushStatement push = (PushStatement) s;
						IntegerLiteral pushID = new IntegerLiteral(SourceLocation.ANALYSIS, push.getID());
						Expression lastpush = new IdentifierExpression(push.getSourceLocation(), lp);
						push.addGhostField(lastpush);
						Statement assign = new AssignStatement(push.getSourceLocation(), lastpush, pushID);
						b.addStatement(i, assign);
						i++;
					}
				}
			}

			if (debug)
				System.out.println("DONE:\n" + m);
		}
	}

	private boolean hasPushOrPull(Method m) {
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				if (s instanceof PullStatement || s instanceof PushStatement) {
					return true;
				}
			}
		}
		return false;
	}
}
