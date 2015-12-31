package faultlocalization;

/**
 * @author schaef
 *
 */
//@SuppressWarnings("unused")
public class TruePositives01 {
	
	public boolean stringCompare() {
		String s = "hallo";
		if (s == "wurstsalat") return false;
		return true;
	}

	public void loopOfByOne(int [] arr) {
		for (int i=0; i<=arr.length;i++) {
			arr[i]=i; // INFEASIBLE
		}
	}

	public int nested(boolean b) {
		int i = 0;
		int y=2;
		if (b) {
			i++;	
			y+=17;
		} else {
			i--;
			y-=100;
		}
		y--;
		assert(i==0);
		return y;
	}
}



