
class A {
	public int x;
}

class B extends A {
	public int y;
}

class C extends A {
	public int z;
}


public class SatCasting01 {

	public static void main(String args[]) {
		A a = new C(); 
		foo(a);
	}
	
	private static void foo(A a) {
		C myC = (C)a; //good cast
		assert true;
	}
	
}
