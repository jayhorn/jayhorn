import java.util.Random;

public class SatFibonacci01 {

	static int fibonacci(int n) {
		if (n < 1) {
			return 0;
		} else if (n == 1) {
			return 1;
		} else {
			return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}

	static void main(String[] args) {
		Random rand = new Random(42);

		int x = rand.nextInt();
		if (x > 46 || x == -2147483648) {
			return;
		}
		int result = fibonacci(x);
		if (result >= x - 1) {
			return;
		} else {
			assert false;
		}
	}
}
