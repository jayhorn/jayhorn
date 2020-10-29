
public class UnknownStringEquals {

    public static void main(String[] args) {
        String s = Integer.toString(args.length);
        assert (s.equals("3"));
    }

}
