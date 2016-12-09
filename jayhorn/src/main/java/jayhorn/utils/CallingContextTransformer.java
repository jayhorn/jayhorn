/**
 * 
 */
package jayhorn.utils;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class CallingContextTransformer {

	public CallingContextTransformer() {
	}

	/**
	 * Add IDs to track the calling context. There are different
	 * strategies to do that. Currently, this only implements the
	 * approach of assigning a unqiue number to every call statement and
	 * passing that as extra parameter to all callees. The callee
	 * then adds that to all pushes and pulls. 
	 * @param p
	 */
	public void transform(Program p) {
		insertOneLevelCallingContext(p);
	}
	
	private static final String CALLCONTEXT = "__callCtx";
	
	/**
	 * Each call statement gets a unique ID. Every time we call a method,
	 * we pass the ID of the caller as an argument. This ID is also
	 * passed into every pull and push.
	 * @param p
	 */
	private void insertOneLevelCallingContext(Program p) {
		//Register new ghost expression for the caller id.
		GhostRegister.v().ghostVariableMap.put(CALLCONTEXT, IntType.instance());
		
		//start from 1 because zero is reserved for main.
		int uniqueNumber = 1;
		SourceLocation loc;
		
		for (Method m : p.getMethods()) {			
			Variable callID = new Variable("callID", IntType.instance());

			m.getInParams().add(callID);
			Variable callIdLocal = new Variable("cIdLocal", IntType.instance());
			m.addLocalVariable(callIdLocal);
			loc = m.getLocation();
			if (m.isProgramEntryPoint()) {				
				m.getSource().getStatements().add(0, new AssignStatement(m.getLocation(), new IdentifierExpression(loc, callIdLocal), new IntegerLiteral(loc, 0)));
			} else {
				m.getSource().getStatements().add(0, new AssignStatement(m.getLocation(), new IdentifierExpression(loc, callIdLocal), new IdentifierExpression(loc, callID)));
			}
			
			for (CfgBlock b : m.vertexSet()) {				
				for (Statement s : b.getStatements()) {
					loc = s.getSourceLocation();

					if (s instanceof CallStatement) {
						CallStatement cs = (CallStatement)s;
						cs.getArguments().add(new IntegerLiteral(loc, uniqueNumber++));
					} else if (s instanceof PullStatement) {
						PullStatement ps = (PullStatement)s;
						ps.getGhostExpressions().add(new IdentifierExpression(loc, callIdLocal));
					} else if (s instanceof PushStatement) {
						PushStatement ps = (PushStatement)s;
						ps.getGhostExpressions().add(new IdentifierExpression(loc, callIdLocal));						}
				}
			}
		}
//		System.err.println(p);
	}
}
