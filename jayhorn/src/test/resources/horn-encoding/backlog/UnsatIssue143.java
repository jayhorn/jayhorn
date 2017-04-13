class UnsatIssue143 {
  static void m() {
    throw new NullPointerException();
  }

  public static void main(String args[]) {
    m();
  }
}
