public class UnsatStringSubstring {

    public static void main(String[] args) {
        String s = "abcabcd";
        assert (s.substring(0, 5).equals("abca"));
        assert (s.substring(4, 4).equals("b"));
        assert (s.substring(1, 4).equals("ca"));
        assert (s.substring(0, s.length() - 1).equals(s));
        assert (s.substring(1, s.length()).equals(s));
    }

}
