

public class UnsatCtor {

    public static class Inner {
        Inner2 x;
        public Inner() {
            x = new Inner2();
            assert false;
        }
    }

    public static class Inner2 {
    }

    public static void main(String[] args) { new Inner(); }

}
