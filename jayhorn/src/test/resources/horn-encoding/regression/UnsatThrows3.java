/*
 * Example that previously led to an exception in conjunction with
 * method inlining
 */

public class UnsatThrows3 {
  public static int f() {
      return 0;
  }
  public static void main(String args[]) {
    try {
      int x = 1 / f();
    } catch (ArithmeticException exc) {
      assert false;
    }
  }
}
