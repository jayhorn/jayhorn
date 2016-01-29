import java.util.Random;

public class UnsatAckermann01 {

	static int ackermann(int m, int n) {
		if (m == 0) {
			return n + 1;
		}
		if (n == 0) {
			return ackermann(m - 1, 1);
		}
		return ackermann(m - 1, ackermann(m, n - 1));
	}

	public static void main(String[] args) {
		Random rand = new Random(42);
		int m = rand.nextInt();
		int n = rand.nextInt();
		int result = ackermann(m, n);
		if (m < 2 || result >= 4) {
			return;
		} else {
			assert false;
		}
	}
}
