class A {
  public int i;

  void setI(int i) {
    this.i = i;
  }

  int getI(int i) {
    return i;
  }
}

class UnsatInstances {
  public static void main(String[] args) {
     A a = new A();
     A b = new A();
     a.setI(41);
     b.setI(42);
     assert a.i!=41;
  }
}
