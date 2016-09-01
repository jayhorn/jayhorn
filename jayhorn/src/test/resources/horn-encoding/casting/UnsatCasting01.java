
class A {
	public int x;
}

class B extends A {
	public int y;
}

class C extends A {
	public int z;
}


public class UnsatCasting01 {

	public static void main(String args[]) {
		A a = new B();
		foo(a);
	}
	
	private static void foo(A a) {
		C myC = (C)a; //bad cast
		assert true;
	}
	
}
