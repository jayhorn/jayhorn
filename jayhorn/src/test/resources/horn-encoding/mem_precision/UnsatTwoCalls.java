import java.util.Random;

class A {
  public int i;

  void setI(int i) {
    this.i = i;
  }

  int getI(int i) {
    return i;
  }
}

class UnsatTwoCalls {

  public static void main(String[] args) {
    A a = new A();
    a.setI(41);
    a.setI(42);
    assert(a.i==41);
  }
}
