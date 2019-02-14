
public class SatStringEquals {

    public static void main(String[] args) {
        String s = "abc";
        String t = "abc";
        // s and t contents are removed by optimizer CopyPropagator.copyPropagate(method)
        assert (s.equals(t));
    }

}
