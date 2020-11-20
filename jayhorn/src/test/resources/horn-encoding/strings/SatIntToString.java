
public class SatIntToString {

    public static void main(String[] args) {
        String s = String.valueOf(816);
        assert (s.equals("816"));
        String t = String.valueOf(-24);
        assert (t.equals("-24"));
        String z = String.valueOf(0);
        assert (z.equals("0"));
    }

}
