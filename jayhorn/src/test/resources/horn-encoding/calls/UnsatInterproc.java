class A {
	public int i;
}

class UnsatInterproc {

  static void setField(A a) {
    a.i = 42;
  }

  public static void main(String[] args) {
     A a = new A();
     setField(a);
     assert(a.i!=42);
  }
}
