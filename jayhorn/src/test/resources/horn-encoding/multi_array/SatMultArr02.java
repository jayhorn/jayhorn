/**
 * @author schaef
 *
 */
public class SatMultArr02 {

//	private static int[][][] mult = new int [17][2][];
	
	public static void main(String[] args) {
		int[][] mult = new int [17][2];
	    mult[3][0] = 2;
	    mult[0][4] = 2;
	    assert mult[3][0]==mult[0][4];	    
	}	
	
}
