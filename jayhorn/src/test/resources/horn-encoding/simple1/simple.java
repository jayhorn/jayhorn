
public class simple {
    public static int plusOne(int x) {
        return x + 1;
    }

    public void f1() {
        int i = 0;
        while (i < 9)
            i = i + 1;
        assert(i == 10); // does not hold
    }

    public void f2() {
        int i = 0;
        while (i < 10)
            i = i + 1;
        assert(i == 10); // holds
    }

    public void f3() {
        int i = 0;
        while (i < 9)
            i = plusOne(i);
        assert(i == 10); // does not hold
    }

    public void f4() {
        int i = 0;
        while (i < 10)
            i = plusOne(i);
        assert(i == 10); // holds
    }
}
