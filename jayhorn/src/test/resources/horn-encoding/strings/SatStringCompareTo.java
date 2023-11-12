
public class SatStringCompareTo {

    public static void main(String[] args) {
        String a = "a";
        String s = "abc";
        String t = "abcde";
        String x = "abx";
        String y = "ayc";
        assert (a.compareTo(s) == -2);
        assert (s.compareTo(a) == 2);
        assert (s.compareTo(x) == -21);
        assert (x.compareTo(s) == 21);
        assert (y.compareTo(t) == 23);
        assert (t.compareTo(y) == -23);
    }

}
