
public class SatObjectFromLib3 {

  public static void test(SatObjectFromLib3 x, int y) {
      System.out.println(1);
  }
  public static void main(String[] args) {
    if (args.length == 0)
      test(null, 0);
    else
      test(null, args.length);
  }
}
