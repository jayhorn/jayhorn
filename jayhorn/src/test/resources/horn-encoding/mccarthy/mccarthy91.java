public class mccarthy91 {
    public static int f(int n) {
        if (n > 100)
            return n - 10;
        else
            return f(f(n + 11));
    }

    public static void check1(int x) {
        int y = f(x);
        assert(x > 101 || y == 91); // holds
    }

    public static void check2(int x) {
        int y = f(x);
        assert(x > 101 || y < 90); // does not hold
    }
}
