package jayhorn.solver.z3;

import jayhorn.solver.BoolType;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverType;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;


class Z3Fun implements ProverFun {

	private final FuncDecl fun;
	private final ProverType resType;
	private final Context ctx;

	public Z3Fun(FuncDecl fun, Context ctx, ProverType resType) {
		this.fun = fun;
		this.resType = resType;
		this.ctx = ctx;
	}

	public FuncDecl getFun() {
		return this.fun;
	}
	
	public ProverExpr mkExpr(ProverExpr[] args) {
		final Expr[] z3args = new Expr[args.length];
		for (int i=0; i<args.length; i++) {
			if (args[i] instanceof Z3TermExpr) {
				z3args[i]= ((Z3TermExpr)args[i]).getExpr();
			} else if (args[i] instanceof Z3BoolExpr) {
				z3args[i]= ((Z3BoolExpr)args[i]).getExpr();	
			}			
		}
		
		try {
			if (this.resType == BoolType.INSTANCE) {
				return new Z3BoolExpr((BoolExpr) ctx.mkApp(this.fun, z3args));
			} else {
				return new Z3TermExpr(ctx.mkApp(this.fun, z3args), this.resType);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public String toString() {
		return fun.toString();
	}

}
