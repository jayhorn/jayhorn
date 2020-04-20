import java.util.Random;

public class UnsatRandom2 {
  public static void main(String[] args) {
    Random rand = new Random(0);
    Random rand2 = rand.nextBoolean() ? null : (new Random (0));
    rand2.nextBoolean();
  }
}
