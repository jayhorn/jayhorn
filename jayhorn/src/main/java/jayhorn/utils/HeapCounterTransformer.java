/**
 * 
 */
package jayhorn.utils;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class HeapCounterTransformer {

	public static final String outHeapCounterName = "outHeapCounter";
	
	public HeapCounterTransformer() {
	}

	/**
	 * Add IDs to track the calling context. There are different
	 * strategies to do that. Currently, this only implements the
	 * approach of assigning a unqiue number to every call statement and
	 * passing that as extra parameter to all callees. The callee
	 * then adds that to all pushes and pulls.
	 * 
	 * @param p
	 */
	public void transform(Program p) {
		insertGlobalHeapCounter(p);
	}

	/**
	 * Each call statement gets a unique ID. Every time we call a method,
	 * we pass the ID of the caller as an argument. This ID is also
	 * passed into every pull and push.
	 * 
	 * @param p
	 */
	private void insertGlobalHeapCounter(Program p) {

		// start from 1 because zero is reserved for main.
		SourceLocation loc;

		for (Method m : p.getMethods()) {
			Variable inCounter = new Variable("inHeapCounter", IntType.instance());
			
			m.getInParams().add(inCounter);
			m.getReturnType().add(IntType.instance());
			if (m.getOutParams().isEmpty()) {
				throw new RuntimeException("unexpected");
			}
			Variable outCounter = new Variable(outHeapCounterName, IntType.instance());
			m.getOutParams().add(outCounter);

			loc = m.getLocation();
			if (m.isProgramEntryPoint()) {
				m.getSource().getStatements().add(0, new AssignStatement(m.getLocation(),
						new IdentifierExpression(loc, outCounter), new IntegerLiteral(loc, 1)));
			} else {
				m.getSource().getStatements().add(0, new AssignStatement(m.getLocation(),
						new IdentifierExpression(loc, outCounter), new IdentifierExpression(loc, inCounter)));
			}

			for (CfgBlock b : m.vertexSet()) {
				Set<NewStatement> newStmts = new HashSet<NewStatement>(); 
				for (Statement s : b.getStatements()) {
					loc = s.getSourceLocation();

					if (s instanceof CallStatement) {
						CallStatement cs = (CallStatement) s;
						cs.getArguments().add(new IdentifierExpression(loc, outCounter));
						cs.getReceiver().add(new IdentifierExpression(loc, outCounter));
					} else if (s instanceof NewStatement) {
						NewStatement ns = (NewStatement) s;
						ns.setCounterVar(outCounter);
						newStmts.add(ns);
					}
				}
				
				for (NewStatement ns : newStmts) {
					loc = ns.getSourceLocation();
					int idx = b.getStatements().indexOf(ns);
					BinaryExpression plusOne = new BinaryExpression(loc, BinaryOperator.Plus, new IdentifierExpression(loc, outCounter), IntegerLiteral.one());
					AssignStatement idxPlusPlus = new AssignStatement(loc,
							new IdentifierExpression(loc, outCounter), plusOne);					
					b.addStatement(idx, idxPlusPlus);
				}
				
			}
		}
//		System.err.println(p);
	}
}
