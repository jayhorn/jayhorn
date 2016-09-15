
public class UnsatIssue60 {
    public int x;
    public static UnsatIssue60 a = null;
    public static void main(String[] args) {
        a = null;
        a.x = 42;
    }
}