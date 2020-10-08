
public class UnsatStringCompareTo {

    public static void main(String[] args) {
        String s = "abc";
        String x = "abx";
        assert (x.compareTo(s) == -21);
    }

}
