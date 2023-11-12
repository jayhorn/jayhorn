
public class SatStringStartsWithOffset {

    public static void main(String[] args) {
        String e = "";
        String s = "abcabc";
        assert (e.startsWith("", 0));
        assert (s.startsWith("", 0));
        assert (s.startsWith("a", 0));
        assert (s.startsWith("abcabc", 0));
        assert (!s.startsWith("abcabca", 0));
        assert (s.startsWith(e, 2));
        assert (!s.startsWith(e, -1));
        assert (!s.startsWith(e, 6));
        assert (s.startsWith("bcabc", 1));
        assert (!s.startsWith("bcabca", 1));
        assert (s.startsWith("abc", 0));
        assert (s.startsWith("abc", 3));
    }

}
