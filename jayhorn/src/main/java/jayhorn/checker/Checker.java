package jayhorn.checker;


import jayhorn.hornify.HornHelper;
import jayhorn.hornify.encoder.S2H;
import soottocfg.cfg.Program;


/**
 * @author teme
 */


public abstract class Checker {
	
	public Checker() {
		S2H.resetInstance();
		HornHelper.resetInstance();
	}
	
	public abstract boolean checkProgram(Program program);
}



