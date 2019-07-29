
public class UnsatRecursive {
    public static class Ptr {
        public int a;
        }

    public static void neg(Ptr p) {
        p.a = -p.a;
        }

    public static void rec(Ptr p, boolean b) {
        if(b) {
            neg(p);
            p.a++;
            } else {
            p.a++;
            neg(p);
            rec(p, true);
            }
        }
    
    public static void main(String[] args) {
        Ptr p = new Ptr();
        p.a = args.length - 1000;
        int d = p.a;
        rec(p, false);
        assert d == p.a;
        }
}
