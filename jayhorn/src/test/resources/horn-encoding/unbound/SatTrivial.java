import java.util.Random;

class SatTrivial {
	
	public static void main(final java.lang.String[] args) {

		Random rand = new Random(42);
		int N = rand.nextInt(42);
		if (N<4 || N>5) return;

		assert(N==4 || N==5);
	}
}
