class A {
	public int i;
}

class SatInit {
  public static void main(String[] args) {
     A a = new A();
     a.i++;
     assert a.i==1;
  }
}

