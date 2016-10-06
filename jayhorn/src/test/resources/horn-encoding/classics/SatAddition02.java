import java.util.Random;

public class SatAddition02 {
	static int addition(int m, int n) {
		if (n == 0) {
			return m;
		} else if (n > 0) {
			return addition(m + 1, n - 1);
		} else {
			return addition(m - 1, n + 1);
		}
	}

	public static void main(String[] args) {
		Random rand = new Random(42);
		int m = rand.nextInt();
		int n = rand.nextInt();
		if (n<0 || m<0) return;
		int result = addition(m, n);
		if (m < 100 || n < 100 || result >= 200) {
			return;
		} else {
			assert false;
		}
	}
}
