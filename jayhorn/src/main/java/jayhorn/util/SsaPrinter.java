/**
 * 
 */
package jayhorn.util;

import java.util.Map.Entry;

import jayhorn.util.SsaTransformer.PhiStatement;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.ArrayLengthExpression;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PackStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.statement.UnPackStatement;

/**
 * @author schaef
 *
 */
public class SsaPrinter {

	public void printMethod(StringBuilder sb, Method m) {
		sb.append("Method ");
		sb.append(m.getMethodName());
		String comma = "";
		sb.append("(");
		for (Variable v : m.getInParams()) {
			sb.append(comma);
			sb.append(v.getName());
			comma = ", ";
		}
		sb.append(")\n");
		if (m.getOutParam().isPresent()) {
			sb.append("\treturns: ");
			sb.append(m.getOutParam().get());
			sb.append("\n");
		}
		comma = "";
		sb.append("\n");
		if (m.getLocals()!=null && !m.getLocals().isEmpty()) {
			sb.append("\tlocals:\n");
			for (Variable v : m.getLocals()) {
				sb.append("\t\t");
				sb.append(v.getName());
				sb.append("\n");
			}
		}

//		TopologicalOrderIterator<CfgBlock, CfgEdge> iterator = new TopologicalOrderIterator<CfgBlock, CfgEdge>(m);
//		while (iterator.hasNext()) {
//			printBlock(sb, iterator.next());
//		}
		for (CfgBlock b : m.vertexSet()) {
			printBlock(sb, b);
		}
	}

	public void printBlock(StringBuilder sb, CfgBlock b) {
		sb.append(b.getLabel());
		sb.append(":\n");
		for (Statement s : b.getStatements()) {
			sb.append("(ln ");
			sb.append(s.getJavaSourceLine());
			sb.append(")\t");
			printStatement(sb, s);
			sb.append("\n");
		}
		if (b.getMethod().outDegreeOf(b) != 0) {
			sb.append("\tgoto:\n");
			for (CfgEdge edge : b.getMethod().outgoingEdgesOf(b)) {
				sb.append("\t  ");
				if (edge.getLabel().isPresent()) {
					sb.append("if ");
					printExpression(sb, edge.getLabel().get());					
					sb.append(": ");
				}
				sb.append(b.getMethod().getEdgeTarget(edge).getLabel());
				sb.append("\n");
			}
		} else {
			sb.append("\treturn\n");
		}
	}

	public void printStatement(StringBuilder sb, Statement s) {
		if (s instanceof ArrayReadStatement) {
			sb.append("ArrayReadStatement-not implemented");
		} else if (s instanceof ArrayStoreStatement) {
			sb.append("ArrayStoreStatement-not implemented");
		} else if (s instanceof AssertStatement) {
			sb.append("assert(");
			printExpression(sb, ((AssertStatement) s).getExpression());
			sb.append(")");
		} else if (s instanceof AssignStatement) {
			AssignStatement asgn = (AssignStatement) s;
			printExpression(sb, asgn.getLeft());
			sb.append(" := ");
			printExpression(sb, asgn.getRight());
		} else if (s instanceof AssumeStatement) {
			sb.append("assume(");
			printExpression(sb, ((AssumeStatement) s).getExpression());
			sb.append(")");
		} else if (s instanceof CallStatement) {
			CallStatement cs = (CallStatement) s;
			if (cs.getReceiver().isPresent()) {
				printExpression(sb, cs.getReceiver().get());
				sb.append(" := ");
			}
			sb.append(cs.getCallTarget().getMethodName());
			sb.append("(");
			String comma = "";
			for (Expression e : cs.getArguments()) {
				sb.append(comma);
				comma = ", ";
				printExpression(sb, e);
			}
			sb.append(")");
		} else if (s instanceof PackStatement) {
			sb.append("PackStatement-not implemented");
		} else if (s instanceof UnPackStatement) {
			sb.append("UnPackStatement-not implemented");
		} else if (s instanceof PhiStatement) {
			//phi can only be introduced by ssa
			PhiStatement phi = (PhiStatement)s;
			printExpression(sb, phi.getLeft());
			sb.append(" := phi(");
			String comma = "";
			for (Entry<CfgBlock, Integer> entry : phi.getPredecessorIncarnations().entrySet()) {
				sb.append(comma);
				comma = ", ";
				sb.append(phi.getLeft().getVariable().getName());
				sb.append("__");
				sb.append(entry.getValue());
			}
			sb.append(")");
		} else {
			throw new RuntimeException("Unknown type " + s);
		}
	}

	public void printExpression(StringBuilder sb, Expression expression) {
		if (expression instanceof ArrayLengthExpression) {
			ArrayLengthExpression e = (ArrayLengthExpression) expression;
			sb.append("len(");
			printExpression(sb, e.getExpression());
			sb.append(")");
		} else if (expression instanceof BinaryExpression) {
			BinaryExpression e = (BinaryExpression) expression;
			sb.append("(");
			printExpression(sb, e.getLeft());
			sb.append(e.getOp());
			printExpression(sb, e.getRight());
			sb.append(")");
		} else if (expression instanceof BooleanLiteral) {
			BooleanLiteral e = (BooleanLiteral) expression;
			sb.append(e);
		} else if (expression instanceof IdentifierExpression) {
			IdentifierExpression e = (IdentifierExpression) expression;
			sb.append(e.getVariable().getName());
			sb.append("__");
			sb.append(e.getIncarnation());
		} else if (expression instanceof IntegerLiteral) {
			IntegerLiteral e = (IntegerLiteral) expression;
			sb.append(e);
		} else if (expression instanceof IteExpression) {
			IteExpression e = (IteExpression) expression;
			sb.append("(");
			printExpression(sb, e.getCondition());
			sb.append(") ? ");
			printExpression(sb, e.getThenExpr());
			sb.append(" : ");
			printExpression(sb, e.getElseExpr());
		} else if (expression instanceof UnaryExpression) {
			UnaryExpression e = (UnaryExpression) expression;
			sb.append("(");
			sb.append(e.getOp());
			printExpression(sb, e.getExpression());
			sb.append(")");
		} else {
			throw new RuntimeException("Unknown type " + expression);
		}
	}

}
