/**
 * 
 */
package jayhorn.checker;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverType;
import jayhorn.solver.princess.PrincessProverFactory;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class Checker {

	private static class HornPredicate {
		public final List<Variable> variables;
		public final ProverFun predicate;

		public HornPredicate(List<Variable> vars, ProverFun pred) {
			variables = vars;
			predicate = pred;
		}
	}

	private final ProverFactory factory = new PrincessProverFactory();
	private Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();

	public boolean checkProgram(Program program) {
		Log.info("Starting verification for " + program.getEntryPoints().length + " entry points.");

		for (Method method : program.getEntryPoints()) {

			Prover p = factory.spawn();
			p.setHornLogic(true);
			try {
				checkEntryPoint(p, program, method); // TODO give this one a
														// return value.
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				p.shutdown();
			}
		}

		return true;
	}

	private void checkEntryPoint(Prover p, Program program, Method method) {
		Log.info("\tVerification from entry " + method.getMethodName());

		makeBlockPredicates(p, method);

		List<CfgBlock> todo = new LinkedList<CfgBlock>();
		todo.add(method.getSource());
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		while (!todo.isEmpty()) {
			CfgBlock current = todo.remove(0);
			done.add(current);
			blockToHorn(p, current);
			for (CfgBlock succ : current.getSuccessors()) {
				if (!todo.contains(succ) && !done.contains(succ)) {
					todo.add(succ);
				}
			}
		}
	}

	/**
	 * Creates one HornPredicate for each block. The predicate contains the list of live variables
	 * for that block sorted by names and a predicate over the types of these variables that has
	 * the same name as the block.
	 * @param p
	 * @param method
	 */
	private void makeBlockPredicates(Prover p, Method method) {
		Map<CfgBlock, Set<Variable>> liveVariables = method.computeLiveVariables();
		for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.entrySet()) {
			// First sort the list of variables by name to make access and
			// reading easier.
			List<Variable> sortedVars = setToSortedList(entry.getValue());
			List<ProverType> types = new LinkedList<ProverType>();
			for (Variable v : sortedVars) {
				types.add(getProverType(p, v.getType()));
			}
			ProverFun pred = p.mkHornPredicate(entry.getKey().getLabel(), types.toArray(new ProverType[types.size()]));
			blockPredicates.put(entry.getKey(), new HornPredicate(sortedVars, pred));
		}

	}

	/**
	 * Creates a ProverType from a Type.
	 * TODO: not fully implemented.
	 * @param p
	 * @param t
	 * @return
	 */
	private ProverType getProverType(Prover p, Type t) {
		if (t == IntType.instance()) {
			return p.getIntType();
		}
		if (t == BoolType.instance()) {
			return p.getBooleanType();
		}
		return null;
	}

	private void blockToHorn(Prover p, CfgBlock block) {
		for (Statement s : block.getStatements()) {
			statementToProverExpr(p,s);
			//TODO
		}
	}
	
	private ProverExpr statementToProverExpr(Prover p, Statement s) {
		//TODO: do the local ssa as well.
		if (s instanceof AssertStatement) {
			return p.mkLiteral(true); //TODO
		} else if (s instanceof AssumeStatement) {
			return p.mkLiteral(true); //TODO
		} else if (s instanceof AssignStatement) {
			return p.mkLiteral(true); //TODO
		} else if (s instanceof CallStatement) {
			return p.mkLiteral(true); //TODO
		} else {
			throw new RuntimeException("Statement type " + s + " not implemented!");
		}
	}
	

	private List<Variable> setToSortedList(Set<Variable> set) {
		List<Variable> res = new LinkedList<Variable>(set);
		if (!res.isEmpty()) {
			Collections.sort(res, new Comparator<Variable>() {
				@Override
				public int compare(final Variable object1, final Variable object2) {
					return object1.getName().compareTo(object2.getName());
				}
			});
		}
		return res;
	}
}
