import java.util.Random;

public class SatRandom {
  public static void main(String[] args) {
    Random rand = new Random(0);
    int x = rand.nextInt();
    int y = rand.nextInt();
    int c = x >= y ? x : y;
    assert c >= x;
  }
}
