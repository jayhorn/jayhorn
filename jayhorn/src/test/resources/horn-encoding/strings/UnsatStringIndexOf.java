
public class UnsatStringIndexOf {

    public static void main(String[] args) {
        String a = "a";
        String b = "";
        String s = "babax";
        assert (s.indexOf(a) == 3);
        assert (s.indexOf(a, 2) == 1);
        assert (s.indexOf(b) == -1);
    }

}