import java.util.Random;

public class Ackermann01_true {

	
	public static int ack(int m, int n) {
		    if (m==0) {
		        return n+1;
		    }
		    if (n==0) {
		        return ack(m-1,1);
		    }
		    return ack(m-1,ack(m,n-1));
		}
	
	public static void main(String[] args){
		Random randomGenerator = new Random();
		int m = randomGenerator.nextInt(10000);
	    if (m < 0 || m > 3) {
	        assert false;
	    }
	    int n = randomGenerator.nextInt(10000);
	    if (n < 0 || n > 23) {
	        assert false;
	    }
	    int result = ack(m,n);
	    if (m < 0 || n < 0 || result >= 0) {
	        assert false;
	    } else {
	    	assert true;
	    }
	
	}
}
