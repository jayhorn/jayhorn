class A extends Exception {
}

class SatInstanceOf1 {
	public static Exception myA = new A();

	public static void main(String[] args) {
		if (myA instanceof RuntimeException) {
			assert false;
		} 
	}
}
