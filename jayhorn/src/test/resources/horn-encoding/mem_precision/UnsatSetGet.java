class A {
  public int i;

  void setI(int i) {
    this.i = i;
  }

  int getI() {
    return this.i;
  }
}

class UnsatSetGet {

  public static void main(String[] args) {
    A a = new A();
    a.setI(42); 
    assert(a.getI()!=42);
  }
}
