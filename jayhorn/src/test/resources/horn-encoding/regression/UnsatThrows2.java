class A extends RuntimeException {
}

class UnsatThrows2 {
	public static void main(String[] args) {
		RuntimeException a = new A();
		try {
			throw a;
		} catch (Throwable e) {
			assert false;
		}
	}
}
