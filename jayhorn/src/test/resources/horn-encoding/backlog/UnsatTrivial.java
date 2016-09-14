
class SatRuntimeException {
    public int x;
    public static SatRuntimeException a = null;
    public static void main(String[] args) {
        a = null;
        a.x = 42;
    }
}