
public class SatLongToString {

    public static void main(String[] args) {
        String s = String.valueOf(816L);
        assert (s.equals("816"));
        String t = String.valueOf(-24L);
        assert (t.equals("-24"));
        String z = String.valueOf(0L);
        assert (z.equals("0"));
    }

}
