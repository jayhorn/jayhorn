public class UnknownStringLength2 {
  public static void main(String[] args) {
    String arg = "0abc";
    if (arg.length() < 1)
      return;
    assert false;
  }
}
