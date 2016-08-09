class A extends RuntimeException {
}

class B extends A {
}

class C extends B {
}


class UnsatInstanceOf2 {
	//passes if myA is declared as type exception
	public static Exception myA = new C();

	public static void main(String[] args) {
		if (myA instanceof Throwable) {
			assert false;	
		}		
	}
}
