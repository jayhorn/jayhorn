public class UnsatStringLastIndexOf {

    public static void main(String[] args) {
        String a = "a";
        String b = "";
        String s = "baba";
        assert (s.lastIndexOf(a) == 1);
        assert (s.lastIndexOf(a, 2) == 3);
        assert (s.lastIndexOf(b) == 0);
    }

}