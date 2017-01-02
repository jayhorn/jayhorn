/**
 * @author schaef
 *
 */
public class SatMultArr02 {
	public static void main(String[] args) {
		int[][] mult = new int [17][2];
	    mult[1][0] = 42;
	    mult[0][1] = 42;
	    assert mult[1][0]==mult[0][1];	    
	}	
	
}
