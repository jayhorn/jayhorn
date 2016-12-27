package jayhorn.solver.spacer;

import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Sort;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverType;


class SpacerFun implements ProverFun {

	private final FuncDecl fun;
	private final ProverType resType;
	private final Context ctx;

	public SpacerFun(FuncDecl fun, Context ctx, ProverType resType) {
		this.fun = fun;
		this.resType = resType;
		this.ctx = ctx;
	}

	public FuncDecl getFun() {
		return this.fun;
	}
	
	public ProverExpr mkExpr(ProverExpr[] args) {
		ProverExpr[] flatArgs = ProverTupleExpr.flatten(args);
		for (ProverExpr exp: flatArgs){
			System.err.println("\t"  + exp  + " : " +  exp.getType());
		}
		System.err.println("----------");
		final Expr[] z3args = new Expr[flatArgs.length];

		for (int i=0; i<flatArgs.length; i++) {
			if (flatArgs[i] instanceof SpacerTermExpr) {
				z3args[i]= ((SpacerTermExpr)flatArgs[i]).getExpr();
			} else if (args[i] instanceof SpacerBoolExpr) {
				z3args[i]= ((SpacerBoolExpr)flatArgs[i]).getExpr();	
			}			
		}
		
		try {
			if (this.resType == BoolType.INSTANCE) {
				return new SpacerBoolExpr((BoolExpr) ctx.mkApp(this.fun, z3args));
			} else {
				return new SpacerBoolExpr((BoolExpr) ctx.mkApp(this.fun, z3args));
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String toString() {
		return fun.toString();
	}

}
