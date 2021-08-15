public class SatStringLastIndexOf {

    public static void main(String[] args) {
        String a = "";
        char b = 'a';
        String c = "ba";
        String d = "ddsd";
        String e = "ds";
        String s = "baas";
        assert (s.lastIndexOf(a) == 4);
        assert (s.lastIndexOf(b) == 2);
        assert (s.lastIndexOf(d) == -1);
        assert (s.lastIndexOf(e) == -1);
        assert (s.lastIndexOf(s) == 0);
        assert (s.lastIndexOf(b, 2) == 2);
        assert (d.lastIndexOf(e, 2) == 1);
        assert (d.lastIndexOf(e, 4) == 1);
    }

}
