
public class UnsatStringStartsWithDefaultOffset {

    public static void main(String[] args) {
        String s = "abcde";
        assert (s.startsWith("abxde", 0));
    }

}
