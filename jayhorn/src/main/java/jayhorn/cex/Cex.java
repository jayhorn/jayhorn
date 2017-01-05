package jayhorn.cex;

import jayhorn.solver.ProverExpr;
import soottocfg.cfg.method.Method;


/**
 * @author Teme
 */

public abstract class Cex {
	
	public Cex(){}
	
	//public CallStatement stmt;
	 
	 /**
	  * Return the method contained in this r
	  * @param solver_exp
	  * @return
	  */
	 public abstract Method containingMethod (ProverExpr solver_exp);

}
