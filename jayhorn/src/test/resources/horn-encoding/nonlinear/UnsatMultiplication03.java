import java.util.Random;

class UnsatMultiplication03 {
  public static void main(String args[]) {
    Random r = new Random();
    int i = r.nextInt();
    int j = r.nextInt();
    if (i > 1)
      assert (i*i > 4);
  }
}
