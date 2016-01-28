

public class Addition_true {

	
	public static int addition(int m, int n) {
	    if (n == 0) {
	        return m;
	    }
	    if (n > 0) {
	        return addition(m+1, n-1);
	    }
	    if (n < 0) {
	        return addition(m-1, n+1);
	    }
	}
	
	public static void main(String[] args){
		int m = Integer.parseInt(args[0]);
		if (m < 0 || m > 2147483647) {
	        assert false;
	    }
	    int n = Integer.parseInt(args[1]);
	    if (n < 0 || n > 2147483647) {
	        assert false;
	    }
	    int result = addition(m,n);
	    assert (result == m + n);
	}
}
