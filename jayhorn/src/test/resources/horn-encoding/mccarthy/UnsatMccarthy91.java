public class UnsatMccarthy91 {
    private static int f(int n) {
        if (n > 100)
            return n - 10;
        else
            return f(f(n + 11));
    }

    public static void main(String[] args) {
    	int x = args.length;
        int y = f(x);
        assert(x > 101 || y < 90); // does not hold
    }
}
