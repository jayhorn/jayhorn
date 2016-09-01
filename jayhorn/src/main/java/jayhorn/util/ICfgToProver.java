package jayhorn.util;

import java.util.List;

import jayhorn.solver.ProverExpr;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.statement.Statement;

public interface ICfgToProver {

	/**
	 * creates missing axioms about variables used during the translation.
	 */
	List<ProverExpr> generatedAxioms();

	ProverExpr statementListToTransitionRelation(List<Statement> stmts);
	
	ProverExpr statementToTransitionRelation(Statement s);

	ProverExpr expressionToProverExpr(Expression e);

}