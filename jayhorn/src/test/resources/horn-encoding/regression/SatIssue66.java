/*
 * https://github.com/jayhorn/jayhorn/issues/66
 * Fails with -mem-perc 0, and 1 but should pass.
 * Throws an exception for -mem-prec >= 2
 */

public class SatIssue66 {
    public int x;
    public SatIssue66 (int x) {
        this.x = x;
    }

    public static SatIssue66 a;

    public static void main(String[] args) {
	a = null;
        for (int i = 0; i < 3; ++i)
            a = new SatIssue66(i+1);
        assert(a==null || a.x >= 0);
    }
}
