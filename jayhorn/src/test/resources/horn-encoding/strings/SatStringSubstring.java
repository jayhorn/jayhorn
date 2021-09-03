public class SatStringSubstring {

    public static void main(String[] args) {
        String s = "abcd";
        assert (s.substring(0, 0).equals(""));
        assert (s.substring(0, 2).equals("ab"));
        assert (s.substring(1, 3).equals("bc"));
        assert (s.substring(0).equals("abcd"));
        assert (s.substring(3).equals("d"));
        assert (s.substring(s.length()).equals(""));
    }

}
