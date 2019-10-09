
/**
 * Case in which previously the push/pull simplifier made a mistake
 */

public class SatControlFlow {

  private static class C {
    private static class D {
      int x;
      D p;

      D(int v, D r) {
        x = v;
        p = r;
      }
    }

    D y = null;

    public void f(int v) {
      if (y == null) {
        y = new D(v, null);
        return;
      }
      y.p = null; // previously this assignment was always executed
    }
  }


  public static void main(String args[]) {
    C b = new C();
    b.f(42);
    assert b.y.x == 42;
  }
}
