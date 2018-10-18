
import java.util.Random;
public class Addition_true {

	
	public static int addition(int m, int n) {
	    if (n == 0) {
	        return m;
	    }
	    if (n > 0) {
	        return addition(m+1, n-1);
	    }
	    else{
	        return addition(m-1, n+1);
	    }
	}
	
	public static void main(String[] args){
		Random randomGenerator = new Random();
		int m = randomGenerator.nextInt(10000);
		if (m < 0 || m > 2147483647) {
	        return;
	    }
	    int n = randomGenerator.nextInt(10000);
	    if (n < 0 || n > 2147483647) {
	        return;
	    }
	    int result = addition(m,n);
	    assert (result == m + n);
	}
}
