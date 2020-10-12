
public class SatStringCompareTo {

    public static void main(String[] args) {
        String e = "";
        String a = "a";
        String s = "abc";
        String x = "abx";
        assert (e.compareTo(s) == -3);
        assert (s.compareTo(e) == 3);
        assert (a.compareTo(s) == -2);
        assert (s.compareTo(a) == 2);
        assert (s.compareTo(x) == -21);
        assert (x.compareTo(s) == 21);
        assert (e.compareTo(e) == 0);
        assert (s.compareTo(s) == 0);
    }

}
