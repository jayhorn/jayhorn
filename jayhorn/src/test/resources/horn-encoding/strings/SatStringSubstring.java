public class SatStringSubstring {

    public static void main(String[] args) {
        String s = "ab";
        assert ("".substring(0, 0).equals(""));
        assert ("a".substring(0, 0).equals(""));
        assert (s.substring(0, 1).equals("a"));
        assert (s.substring(0, 2).equals(s));
        assert (s.substring(1, 2).equals("b"));
        assert (s.substring(2, 2).equals(""));
        assert (s.substring(1, 1).equals(""));

        assert ("".substring(0).equals(""));
        assert ("a".substring(0).equals("a"));
        assert (s.substring(1).equals("b"));
        assert (s.substring(2).equals(""));
    }

}
