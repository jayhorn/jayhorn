

public class Ackermann01_false {

	
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
		int m = Integer.parseInt(args[0]);

	    int n = Integer.parseInt(args[1]);
	
	    int result = ack(m,n);
	    if (m < 2 || result > 4) {
	        assert false;
	    } else {
	    	assert true;
	    }
	
	}
}
