/**
 * 
 */
package jayhorn.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import org.jgrapht.Graphs;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.DominanceFrontier;
import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.Tree;

/**
 * @author schaef
 *
 */
public class SsaTransformer {

	private final Method method;
	private final Program program;
		
	public SsaTransformer(Program prog, Method m) {
		program = prog;
		method = m;
		if (!method.vertexSet().isEmpty()) {
			placePhiFunctions();
			renameVariables();
		}
	}
	
	public void eliminatePhiStatements() {
		//TODO assert loop-freeness
		for (CfgBlock b : method.vertexSet()) {
			Set<PhiStatement> toRemove = new HashSet<PhiStatement>();
			for (Statement s : b.getStatements()) {
				if (s instanceof PhiStatement) {
					PhiStatement phi = (PhiStatement)s;
					toRemove.add(phi);
					int inc = phi.getLeft().getIncarnation();
					Variable v = phi.getLeft().getVariable();
					SourceLocation loc = null;
					for (Entry<CfgBlock, Integer> entry : phi.getPredecessorIncarnations().entrySet()) {
						CfgBlock pred = entry.getKey();
						Verify.verify(pred!=b);
						AssignStatement asgn = new AssignStatement(loc, 
								new IdentifierExpression(loc, v, inc), 
								new IdentifierExpression(loc, v, entry.getValue()));
						pred.getStatements().add(asgn);
					}
				}
			}
			b.getStatements().removeAll(toRemove);
		}
	}
	
	/** Modern Compiler Implementation in Java, Second Edition page 407
	 * For each node n, Aorig[n] is the set of variables defined in node n
	 * Place-phi-Functions =
		for each node n
			for each variable a in Aorig[n]
				defsites[a] <- defsites[a] U {n}
		for each variable a
			W <- defsites[a]
			while W not empty
				remove some node n from W
				for each y in DF[n]
					if a not in Aphi[y]
						insert the statement a <- phi(a, a,..., a) at the top
						  of block y, where the phi-function has as many
						  arguments as y has predecessors.
						Aphi[y] ← Aphi[y]U{a}
						if a not in Aorig[y]
							W <- W U {y}
	 */
	private void placePhiFunctions() {
		Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, method.getSource()); 
		DominanceFrontier<CfgBlock> DF = new DominanceFrontier<CfgBlock>(dom);
		Map<Variable, Set<CfgBlock>> defsites = new HashMap<Variable, Set<CfgBlock>>();		
		for (CfgBlock n : method.vertexSet()) {
			Set<Variable> variables = n.getDefVariables(); //TODO check with Daniel
			for (Variable a : variables) {
				if (!defsites.containsKey(a)) {
					defsites.put(a, new HashSet<CfgBlock>());
				}
				defsites.get(a).add(n);
			}
		}
		Map<CfgBlock, Set<Variable>> Aphi = new HashMap<CfgBlock, Set<Variable>>();
		for (Variable a : method.getUseVariables()) {
			Set<CfgBlock> W = defsites.get(a);
			while(W!=null && !W.isEmpty()) {
				CfgBlock n = W.iterator().next();
				W.remove(n);
				for (CfgBlock y : DF.getDominanceFrontier().get(n)) {
					if (!Aphi.containsKey(y)) {
						Aphi.put(y, new HashSet<Variable>());
					}
					if (!Aphi.get(y).contains(a)) {
						createPhiFunction(y,a);
						Aphi.get(y).add(a);
						if (!y.getUseVariables().contains(a)) {
							W.add(y);
						}
					}
				}
			}
		}
	}
	
	private void createPhiFunction(CfgBlock b, Variable v) {
		IdentifierExpression left = new IdentifierExpression(null, v);
		PhiStatement phi = new PhiStatement(left, b);
		b.getStatements().add(0, phi);
	}
	
	
	/** Modern Compiler Implementation in Java, Second Edition page 409
	 * 	for each variable a
			Count[a] <- 0
			Stack[a] <- empty
			push 0 onto Stack[a]
	 */
	private void renameVariables() {
		System.err.println(method);
		Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, method.getSource());		
		Map<Variable, Integer> Count = new HashMap<Variable, Integer>();
		Map<Variable, Stack<Integer>> stack = new HashMap<Variable, Stack<Integer>>();
		Set<Variable> allVariables = new HashSet<Variable>();
		allVariables.addAll(method.getUseVariables());
		allVariables.addAll(method.getDefVariables());
		allVariables.add(program.getExceptionGlobal());
		for (Variable a : allVariables) { //TODO should be ALL variables.
			Count.put(a, 0);
			stack.put(a, new Stack<Integer>());
			stack.get(a).push(0);
		}
		Tree<CfgBlock> domTree = dom.getDominatorTree();
		rename(domTree.getRoot(), stack, Count, domTree);		
	}
	
	/**
	 * Modern Compiler Implementation in Java, Second Edition page 409
	 * 	for each statement S in block n
	 *		if S is not a phi-function
	 *			for each use of some variable x in S
	 *				i <- top(Stack[x])
	 *				replace the use of x with xi in S
	 *		for each definition of some variable a in S
	 *			Count[a] <- Count[a] + 1
	 *			i <- Count[a]
	 *			push i onto Stack[a]
	 *			replace definition of a with definition of ai in S	
	 *	for each successor Y of block n,
	 *		Suppose n is the jth predecessor of Y
	 *		for each phi-function in Y
	 *		suppose the jth operand of the phi-function is a
	 *		i <- top(Stack[a])
	 *		replace the jth operand with ai
	 *	for each child X of n
	 *		Rename(X)
	 *	for each statement S in block n
	 *		for each definition of some variable a in S
	 *			pop Stack[a]			
	 * @param n
	 */
	private void rename(CfgBlock n, Map<Variable, Stack<Integer>> stack, Map<Variable, Integer> Count, Tree<CfgBlock> domTree) {
		for (Statement S : n.getStatements()) {
			if (!(S instanceof PhiStatement)) {
				for (IdentifierExpression idexp : S.getUseIdentifierExpressions()) {
					int i = stack.get(idexp.getVariable()).peek();
					idexp.setIncarnation(i);
				}
			}
			for (IdentifierExpression ie : S.getDefIdentifierExpressions()) {
				Variable a = ie.getVariable();
				Count.put(a, Count.get(a)+1);
				int i = Count.get(a);
				stack.get(a).push(i);
				ie.setIncarnation(i);
			}
			//TODO: procedure hack:
			if (S instanceof CallStatement) {
				Variable a = program.getExceptionGlobal();
				Count.put(a, Count.get(a)+1);
				stack.get(a).push(Count.get(a));
			} //end of hack
		}
		for (CfgBlock Y : Graphs.successorListOf(method, n)) {
			for (Statement s : Y.getStatements()) {
				if (s instanceof PhiStatement) {
					PhiStatement phi = (PhiStatement)s;
					if (stack.get(phi.getLeft().getVariable()).isEmpty()) {
						System.err.println("WTF " + phi.getLeft().getVariable()); //TODO
					}
					phi.setPredecessorIncarnation(n, stack.get(phi.getLeft().getVariable()).peek());
				}
			}
		}
		for (CfgBlock X : domTree.getChildrenOf(n)) {
			rename(X, stack, Count, domTree);
		}
		for (Statement S : n.getStatements()) {
			for (Variable a : S.getDefVariables()) {
				stack.get(a).pop();
				if(stack.get(a).isEmpty()) {
					System.err.println("wtf "+a + " in "+n.getLabel());
				}
			}
		}
	}
	

	public static class PhiStatement extends Statement {

		private static final long serialVersionUID = -4653846673112633894L;
		private final IdentifierExpression left;
		private final Map<CfgBlock, Integer> predecessorIncarnations = new HashMap<CfgBlock, Integer>();

		public PhiStatement(IdentifierExpression left, CfgBlock createdFrom) {
			super(null);
			this.left = left;
		}

		public IdentifierExpression getLeft() {
			return left;
		}
		
		public void setPredecessorIncarnation(CfgBlock pred, Integer incarnation) {
			predecessorIncarnations.put(pred, incarnation);
		}
		
		public Map<CfgBlock, Integer> getPredecessorIncarnations() {
			return predecessorIncarnations;
		}		
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(left);
			sb.append(" := phi(");
			sb.append("...");
			sb.append(")");
			return sb.toString();
		}

		@Override
		public Set<IdentifierExpression> getUseIdentifierExpressions() {			
			return new HashSet<IdentifierExpression>();
		}

		@Override
		public Set<IdentifierExpression> getDefIdentifierExpressions() {
			Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
			res.add(left);
			return res;
		}
	}
	
}
