public class UnknownDouble {

  static double f(long l) {
    assert false;
    return 0.0;
  }

  public static double g() {
    double x = 0.0;
    if (x < 0.0) {
      x *= 10.0;
    } else {
      x /= 10.0;
    }
    return f((long) x);
  }

  public static void main(String[] args) {
    g();
  }
}
