class A {
}

class B extends A {
}

class UnsatInstanceOf {
	public static void main(String[] args) {
		A a = new A();
		if (a instanceof A)  {
			assert false;
		}
	}
}
