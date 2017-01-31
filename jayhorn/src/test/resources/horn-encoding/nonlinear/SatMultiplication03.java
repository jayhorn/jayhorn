import java.util.Random;

class SatMultiplication03 {
  public static void main(String args[]) {
    Random r = new Random();
    int i = r.nextInt();
    if (i > 1)
      assert (i*i >= 4);
  }
}
