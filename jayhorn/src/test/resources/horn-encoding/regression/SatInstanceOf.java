class A {
}

class B extends A {
}

class SatInstanceOf {
	public static void main(String[] args) {
		A a = new A();
		if (a instanceof B)  {
			assert false;
		}
	}
}
