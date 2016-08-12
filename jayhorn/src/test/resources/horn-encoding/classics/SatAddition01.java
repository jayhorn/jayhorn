import java.util.Random;

public class SatAddition01 {

static int addition(int m, int n) {
    if (n == 0) {
        return m;
    } else if (n > 0) {
        return addition(m+1, n-1);
    } else {
        return addition(m-1, n+1);
    }
}


public static void main(String[] args) {
	Random rand = new Random(42);
	int m = rand.nextInt();
    if (m < 0 || m > 2147483647) {
        return ;
    }
    int n = rand.nextInt();
    if (n < 0 || n > 2147483647) {
        return ;
    }
    int result = addition(m,n);
    if (result == m + n) {
        return ;
    } else {
    	assert false;
    }
}
}
