package jayhorn.solver.spacer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Verify;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Fixedpoint;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Z3Exception;

/**
 * Utility funtions for Spacer
 * @author Teme
 *
 */
public class SpacerUtils {


	public SpacerUtils(){}

	/**
	 * Get the sort of a bounded vars
	 * @param rule
	 * @param var
	 */
	//private void bound_var_sort(BoolExpr rule, Expr var){}

	/**
	 * Get the name of a bounded var
	 * @param rule
	 * @param var
	 * @throws Z3Exception 
	 */
	private Expr bound_var_name(BoolExpr rule, Expr var) throws Z3Exception{
		Expr[] args = rule.getArgs();
		Verify.verify(var.isVar(), "I am expecting a variable");
		int index = var.getIndex();
		return args[args.length - 1 - index];
	}

	private List<Expr> getVars(Expr e) {
		try {
			List<Expr> result = new LinkedList<Expr>();
			if (e.isConst() && !e.isTrue() && !e.isFalse()) {

				result.add(e);
			} else if (e.isQuantifier()) {
				Quantifier q = (Quantifier) e;

				q.getBoundVariableNames();
				getVars(((Quantifier) e).getBody());
				throw new RuntimeException("not implemented");
			} else if (e.isApp()) {

				for (Expr child : e.getArgs()) {
					result.addAll(getVars(child));
				}
			} else if (e.isNumeral()) {
				// ignore
			} else {
				throw new RuntimeException("not implemented "
						+ e.getClass().toString());
			}

			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	public void fp_get_preds(Fixedpoint fp) throws Z3Exception {
		Set<Integer> seen = new HashSet<Integer>();
		//ArrayList<BoolExpr> res = new ArrayList<BoolExpr>();

		for(BoolExpr rule: fp.getRules()){
			BoolExpr pred = rule;
			if (pred.isQuantifier()){
				pred = ((Quantifier) pred).getBody();
			}
			if (pred.isImplies()){ 
				pred = (BoolExpr) (pred.getArgs())[1];
			}

			FuncDecl decl = pred.getFuncDecl();

			//Verify.verify(decl.isUninterpreted());

			if (!seen.contains(decl.getASTKind().hashCode())){
				seen.add(decl.getASTKind().hashCode());
			}

			//if the rule contains a universal quantifier, replace
			//variables by properly named constants
			if (pred.isQuantifier()){
				List<Expr> vars = this.getVars(pred);
				System.out.println(vars);
				//pred.sub
			}
		}


	}

}
