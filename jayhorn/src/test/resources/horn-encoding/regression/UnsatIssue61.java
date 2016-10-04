// use jayhorn.Options.setInsertRuntimeAssertions(boolean)
// to insert runtime assertion.
public class UnsatIssue61 {
    public int x;
    public static UnsatIssue61 a;
    public static void main(String[] args) {
        a = null;
        a.x = 42;
    }
}
