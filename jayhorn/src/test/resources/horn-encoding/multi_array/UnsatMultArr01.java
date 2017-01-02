/**
 * @author schaef
 *
 */
public class UnsatMultArr01 {

	private static int[][][] mult = new int [17][2][];
	
	public static void main(String[] args) {
	    mult[0][0] = new int[10];
	    mult[1] = new int[5][2];
	    
	    mult[0][0][1] = 3;	    
	    mult[1][2][0] = 3;

	    int[] a1 = mult[0][0];
	    int[][] a2 = mult[1];
	    
	    assert a1[1] != a2[2][0];
	}	
	
}
