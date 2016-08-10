class A extends RuntimeException {
}

class UnsatThrows {
	public static void main(String[] args) {
		RuntimeException a = new A();
		try {
			throw a;
		} catch (Exception e) {
			assert false;
		}
	}
}
