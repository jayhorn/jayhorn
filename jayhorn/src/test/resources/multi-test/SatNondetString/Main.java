import org.sosy_lab.sv_benchmarks.Verifier;

public class Main {
  public static void main(String[] args) {
    String str = Verifier.nondetString();
    if (str.length() > 0)
      assert str.charAt(0) == str.charAt(0);
    else
      assert str.equals("");
  }
}
