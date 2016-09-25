import MinePumpSystem.Environment; 

public  class  Actions {

	Environment env;

	Actions() {
		env = new Environment();
	}
	
	void waterRise() {
		/* TODO: This is where the precision issue comes in.
		 * If we create env as a local, JayHorn know that it
		 * is not null. If it's a field, it fails. 
		 * I assume this is a consequence of Issue 88.
		 */
//		Environment env = new Environment();
		env.waterRise();
	}
}
