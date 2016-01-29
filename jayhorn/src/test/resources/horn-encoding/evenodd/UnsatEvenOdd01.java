import java.util.Random;

public class UnsatEvenOdd01 {

	static int isOdd(int n) {
		if (n == 0) {
			return 0;
		} else if (n == 1) {
			return 1;
		} else {
			return isEven(n - 1);
		}
	}

	static int isEven(int n) {
		if (n == 0) {
			return 1;
		} else if (n == 1) {
			return 0;
		} else {
			return isOdd(n - 1);
		}
	}

	public static void main(String[] args) {
		Random rand = new Random(42);
		int n = rand.nextInt();
		if (n < 0) {
			return;
		}
		int result = isEven(n);
		int mod = n % 2;
		if (result < 0 || result == mod) {
			return;
		} else {
			assert false;
		}
	}
}
