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

class SatBranches {

  public static void main(String[] args) {
     Random r = new Random();
     A a = new A();
     if (r.nextBoolean()) {
     	a.setI(41);
     } else {
        a.setI(42);
     }
     assert(a.i==41 || a.i==42);
  }
}
