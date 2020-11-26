
public class SatIntToString {

    public static void main(String[] args) {
        String s = String.valueOf(816);
        assert (s.equals("816"));
        String t = String.valueOf(-24);
        assert (t.equals("-24"));
        String z = String.valueOf(0);
        assert (z.equals("0"));
        String tmp = String.valueOf(10000000000L);
        assert (tmp.equals("10000000000"));
        // next two assertions are probabely machine-dependent:
//        String max = String.valueOf(Long.MAX_VALUE);
//        assert (max.equals("9223372036854775807"));
//        String min = String.valueOf(Long.MIN_VALUE);
//        assert (min.equals("-9223372036854775808"));

    }

}
