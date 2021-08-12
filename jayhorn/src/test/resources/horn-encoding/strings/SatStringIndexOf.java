public class SatStringIndexOf {

    public static void main(String[] args) {
        String a = "";
        String b = "a";
        String c = "ba";
        String d = "fddsd";
        String e = "ds";
        String s = "baax";
        assert (s.indexOf(a) == 0);
        assert (s.indexOf(b) == 1);
        assert (s.indexOf(c) == 0);
        assert (s.indexOf(d) == -1);
        assert (s.indexOf(e) == -1);
        assert (s.indexOf(s) == 0);
    }

}
