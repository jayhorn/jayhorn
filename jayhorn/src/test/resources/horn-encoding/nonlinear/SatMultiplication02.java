import java.util.Random;

class SatMultiplication02 {
  public static void main(String args[]) {
    Random r = new Random();
    int i = r.nextInt();
    int j = r.nextInt();
    if (i > 1 && j > 1)
      assert (i*j >= 4);
  }
}
