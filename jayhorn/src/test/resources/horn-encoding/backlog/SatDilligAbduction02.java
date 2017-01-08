public class SatDilligAbduction02 {

	public static void main(String[] args) {

		int x=0;
		int y=0;
		int n = args.length;
		
		while(x<n) {
			x+=1;
			y+=2;
		}
		assert(x+y!=5);
	}
}
