class A extends Exception {
}

class UnsatInstanceOf3 {
	public static Exception myA = new RuntimeException();

	public static void main(String[] args) {
		if (myA instanceof Exception) {
			assert false;
		}		
	}
}
