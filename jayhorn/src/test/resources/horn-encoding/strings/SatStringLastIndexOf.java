public class SatStringLastIndexOf {

    public static void main(String[] args) {
        String s = "aaa";
        assert ("ab".lastIndexOf("c") == -1);
        assert ("ab".lastIndexOf('c') == -1);
        assert ("ab".lastIndexOf("cd") == -1);
        assert ("ab".lastIndexOf("cde") == -1);
        assert (s.lastIndexOf("") == 3);
        assert (s.lastIndexOf("a") == 2);
        assert (s.lastIndexOf('a') == 2);
        assert (s.lastIndexOf("aa") == 1);
        assert (s.lastIndexOf("", 3) == 3);
        assert (s.lastIndexOf("a", 3) == 2);
        assert (s.lastIndexOf('a', 3) == 2);
        assert (s.lastIndexOf("aa", 3) == 1);
        assert (s.lastIndexOf("", 2) == 2);
        assert (s.lastIndexOf("a", 2) == 2);
        assert (s.lastIndexOf('a', 2) == 2);
        assert (s.lastIndexOf("aa", 2) == 1);
        assert (s.lastIndexOf("", -1) == -1);
        assert (s.lastIndexOf("a", -1) == -1);
        assert (s.lastIndexOf('a', -1) == -1);
        assert (s.lastIndexOf("aa", -1) == -1);
    }

}
