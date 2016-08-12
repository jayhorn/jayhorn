import java.util.Random;

public class SatMccarthy91 {
    private static int f(int n) {
        if (n > 100)
            return n - 10;
        else
            return f(f(n + 11));
    }

    public static void main(String[] args) {
    	int x = args.length;
    	//Random randomGenerator = new Random();
    	//int x= ;
        int y = f(x);
        assert(x > 101 || y == 91); // holds
    }

}
