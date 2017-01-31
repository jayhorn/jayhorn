import java.util.Random;

class SatMultiplication01 {
  public static void main(String args[]) {
    Random r = new Random();
    int i = r.nextInt();
    int j = r.nextInt();
    if (i > 0 && j > 0)
      assert (i*j > 0);
  }
}
