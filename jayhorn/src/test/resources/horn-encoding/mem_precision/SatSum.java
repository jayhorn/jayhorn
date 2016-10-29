import java.util.Random;

public class SatSum {
  private int s;

  public int sum(int n) {
    if (n > 0) {
      s = sum(n-1);
      return s+n;
    } else return 0;
  }

  public static void main(String[] args){
    Random rand = new Random();
    int n = rand.nextInt();
    SatSum ex = new SatSum();
    assert (n < 2 || ex.sum(n) > n);
  }
}

