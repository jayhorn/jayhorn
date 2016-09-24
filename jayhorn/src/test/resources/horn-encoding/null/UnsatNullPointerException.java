public class UnsatNullPointerException {
    public int x;
    public static UnsatNullPointerException a = null;
    public static void main(String[] args) {
        a = null;
        a.x = 42;
    }
}
