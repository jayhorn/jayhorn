
public class UnsatStringEquals {

    public static void main(String[] args) {
        String s = "abc";
        String t = "cba";
        // s and t contents are removed by optimizer CopyPropagator.copyPropagate(method)
        assert (s.equals(t));
    }

}
