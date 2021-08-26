public class UnsatStringSubstring {

    public static void main(String[] args) {
        String s = "abcab";
        assert (s.substring(4, 4).equals("b"));
        assert (s.substring(0, 4).equals("abc"));
        assert (s.substring(0, 2).equals("abc"));
        assert (s.substring(1, 2).equals("abc"));
        assert (s.substring(1, 4).equals("ca"));
        assert (s.substring(3, 4).equals("ca"));
        assert (s.substring(0, 5).equals("ca"));
        assert (s.substring(0, s.length() - 1).equals(s));
        assert (s.substring(1).equals(s));
        assert (s.substring(1).equals("ab"));
    }

}
