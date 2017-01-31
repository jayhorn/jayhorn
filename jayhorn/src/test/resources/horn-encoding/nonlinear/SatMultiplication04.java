import java.util.Random;

class SatMultiplication04 {
  public static void main(String args[]) {
    Random r = new Random();
    int i = r.nextInt();
    if (i > 0)
      assert (i*-1 < 0);
  }
}
