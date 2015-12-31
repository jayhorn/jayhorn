package jayhorn.util;

import java.util.List;

import jayhorn.solver.ProverExpr;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.statement.Statement;

public interface ICfgToProver {

	/**
	 * creates missing axioms about variables used during the translation.
	 */
	public List<ProverExpr> generatedAxioms();

	public ProverExpr statementListToTransitionRelation(List<Statement> stmts);
	
	public ProverExpr statementToTransitionRelation(Statement s);

	public ProverExpr expressionToProverExpr(Expression e);

}