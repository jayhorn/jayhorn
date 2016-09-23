import MinePumpSystem.Environment; 
import MinePumpSystem.MinePump; 

public  class  Actions {
	

	Environment env;
	MinePump p;

    boolean methAndRunningLastTime = false;
    boolean switchedOnBeforeTS = false;
	
	
	Actions() {
		env = new Environment();
		p = new MinePump(env);
	}

	
	
	void waterRise() {
		env.waterRise();
	}

	
	void methaneChange() {
		env.changeMethaneLevel();
	}

	void stopSystem  () {
	    if(p.isSystemActive())
		p.stopSystem();
	}
    
	
	void startSystem  () {
	    if(!p.isSystemActive())
		p.startSystem();
	}
		
	void timeShift() {
	
		p.timeShift();
		
		if (p.isSystemActive()) {
		  Specification1();
		}
	}
	
	String getSystemState() {
		return p.toString();
	}
	
	// Specification 1 methan is Critical and pumping leads to Error
	void Specification1() {			
	
	        Environment e = p.getEnv();
	
		    boolean b1 = e.isMethaneLevelCritical();
	        boolean b2 = p.isPumpRunning();
	     
			 if ( b1 && b2) {
			     assert false;   				
			 }		 			    
    }
}
