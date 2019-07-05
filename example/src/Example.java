import java.util.Random;
import org.sosy_lab.sv_benchmarks.Verifier;

class A {
  public int i;

  void setI(int i) {
    this.i = i;
  }

  int getI(int i) {
    return i;
  }
}

class Example {

  public static void main(String[] args) {
     A a = new A();
     if (Verifier.nondetBoolean()) {
     	a.setI(41);
     } else {
        a.setI(42);
     }
     assert(a.i==41);
  }
}
