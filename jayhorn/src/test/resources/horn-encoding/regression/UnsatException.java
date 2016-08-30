//class A extends Exception {
//	public A() {}
//}

class UnsatException {
	public static void main(String[] args) {
		RuntimeException a = new RuntimeException();	
		if (a instanceof RuntimeException) {} else {}
		assert false;
	}
}
