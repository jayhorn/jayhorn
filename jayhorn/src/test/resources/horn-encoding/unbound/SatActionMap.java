import java.util.Random;

class SatActionMap {
	
	int size;

	public SatActionMap(int size) {
		this.size = size;
	}

	public static void main(final java.lang.String[] args) {

		Random rand = new Random(42);
		int N = rand.nextInt(42);
		if (N<4 || N>5) return;

		//N = 5;
		
		SatActionMap map = new SatActionMap(N);
		assert(map.size==N);
	}
}
