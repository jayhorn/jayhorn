class A extends Exception {
}

class UnsatThrows {
	public static A myA;

	public static void main(String[] args) {
		if (myA instanceof Exception) {
			//unreachable because myA is of NullType at this point
		}
		assert false;
	}
}
