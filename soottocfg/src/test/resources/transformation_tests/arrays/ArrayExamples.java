/**
 * 
 */
package transformation_tests.arrays;

/**
 * @author schaef
 *
 */
public class ArrayExamples {

	private static int[][][] mult = new int [17][2][];
	
	public static void main(String[] args) {
	    int size=10;
	    int int_array[]=new int[size];
	    
	    for(int i=0; i<size; i++)
	      int_array[i]=i;
	    
	    
	    System.err.println(mult.length);
	    
	    mult[0][0] = new int[10];
	    mult[1] = new int[5][2];
	    
	    System.err.println(mult.length);
	}	
	
}
