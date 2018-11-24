

public class SatStrings1 {
    public static void main(String args[]) {
        String a = "abc";
        String b = "cba";
        String c = "abc";

        assert a != null;
        assert a != b;
        assert a == c;
    }
}
