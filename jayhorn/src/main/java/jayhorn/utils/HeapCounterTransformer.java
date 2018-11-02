/**
 * 
 */
package jayhorn.utils;

import java.util.HashSet;
import java.util.Set;

import jayhorn.Options;
import jayhorn.hornify.encoder.S2H;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
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
                // PR: actually, start at 0, because each time we allocate a new object
                // the counter is incremented first
		SourceLocation loc;

		for (Method m : p.getMethods()) {
			loc = m.getLocation();	
			Variable inCounter = new Variable("inHeapCounter", IntType.instance());
			//IdentifierExpression inExp = new IdentifierExpression(loc, inCounter);
			
			m.getInParams().add(inCounter);
			m.getReturnType().add(IntType.instance());
			if (m.getOutParams().isEmpty()) {
				throw new RuntimeException("unexpected");
			}
			Variable outCounter = new Variable(outHeapCounterName, IntType.instance());
			m.getOutParams().add(outCounter);
			
			if (m.isProgramEntryPoint()) {
				IdentifierExpression outExp = new IdentifierExpression(loc, outCounter);
				m.getSource().getStatements().add(0, new AssignStatement(m.getLocation(),
						outExp, new IntegerLiteral(loc, 1)));
				// Adding Heap Count bound checks
				int bound = Options.v().getHeapLimit();
				if (bound > -1) {
					//Expression diff = new BinaryExpression(loc, BinaryExpression.BinaryOperator.Minus, outExp, inExp);
					Expression assrt = new BinaryExpression(loc, BinaryExpression.BinaryOperator.Le, outExp, 
						new IntegerLiteral(loc, bound));
					m.getSink().getStatements().add(0, new AssertStatement(loc, assrt));
				}
			}else {
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
						//
						if (cs.getCallTarget().getSource()!=null) {
							cs.getReceiver().add(new IdentifierExpression(loc, outCounter));
						} else {
							cs.getReceiver().add(new IdentifierExpression(loc, new Variable("noCounter", IntType.instance())));
						}
						//cs.getReceiver().add(new IdentifierExpression(loc, outCounter));
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
			
			// Track of in and out bound variable counters
			S2H.sh().setHeapCounter(m, inCounter, outCounter);
		}
	//System.err.println(p);

	}
}
