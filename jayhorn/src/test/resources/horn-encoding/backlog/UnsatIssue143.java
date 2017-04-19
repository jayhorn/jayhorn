class UnsatIssue143 {
  static void m1() {
    throw new ArrayIndexOutOfBoundsException();
  }

  static void m2() {
    int test[] = {1,2,3};
    int x = test[4];
  }

  static void m3() {
    int x = 3 / 0;
  }

  public static void main(String args[]) {
    try {
    m2();
    } catch (RuntimeException re) {}
  }
}
