package soot_tests;


public class Dups {

	public Object dup(){
		Object o = new Object();
		return o;
	}
	
	public long dubl(){
		long l = 1234;
		l = l + l;
		return l;
	}
	
}
