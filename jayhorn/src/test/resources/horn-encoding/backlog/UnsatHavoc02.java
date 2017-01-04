
public class UnsatRandom01 {

	public static void main(String[] args) {
		java.util.Random random = new java.util.Random(42);

		int x = 0;
		while(random.nextBoolean()) {
			final int d = random.nextInt();
			if (d >= 0 ) {
				x=42;
			}
		}
		
		assert (x==0) ;
	}
}
