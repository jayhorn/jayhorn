import java.util.Random;

class UnsatMultiplication05 {
  public static void main(String args[]) {
    Random r = new Random();
    int i = r.nextInt();
    int j = r.nextInt();
    if (i > 0 && j > 0) {
      int count = 0;
      for (int n = 0; n < i; n++)
        for (int m = 0; m < j; m++)
          count++;
      assert (count <= 0);
    }
  }
}
