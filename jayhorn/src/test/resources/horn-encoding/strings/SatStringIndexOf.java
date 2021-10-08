public class SatStringIndexOf {

    public static void main(String[] args) {
        String s = "aaa";
        assert ("ab".indexOf("c") == -1);
        assert ("ab".indexOf('c') == -1);
        assert ("ab".indexOf("cd") == -1);
        assert ("ab".indexOf("cde") == -1);
        assert (s.indexOf("") == 0);
        assert (s.indexOf("a") == 0);
        assert (s.indexOf('a') == 0);
        assert (s.indexOf("aa") == 0);

        assert (s.indexOf("", 0) == 0);
        assert (s.indexOf("a", 0) == 0);
        assert (s.indexOf('a', 0) == 0);
        assert (s.indexOf("aa", 0) == 0);
        assert (s.indexOf("", 1) == 1);
        assert (s.indexOf("a", 1) == 1);
        assert (s.indexOf('a', 1) == 1);
        assert (s.indexOf("aa", 1) == 1);
        assert (s.indexOf("a", 3) == -1);
        assert (s.indexOf('a', 3) == -1);
        assert (s.indexOf("aa", 2) == -1);

        assert (s.indexOf("", 9) == 3);     // JAVADOC DEFINITION MISMATCH: https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#indexOf-java.lang.String-int-
        assert (s.indexOf("", -9) == 0);
    }

}
