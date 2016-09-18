/*
 * https://github.com/jayhorn/jayhorn/issues/66
 * Fails with -mem-perc 0, and 1 but should pass.
 * Throws an exception for -mem-prec >= 2
 */

public class UnsatIssue66 {
    public int x;
    public UnsatIssue66 (int x) {
        this.x = x;
    }

    public static UnsatIssue66 a = null;

    public static void main(String[] args) {
        for (int i = 0; i < 3; ++i)
            a = new UnsatIssue66(i+1);
	if (a!=null)
        	assert(a.x == 0);
    }
}
