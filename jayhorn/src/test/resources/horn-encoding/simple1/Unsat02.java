
public class Unsat02 {

	public static int plusOne(int x) {
		return x + 1;
	}

	public static void main(String[] args) {
		int i = 0;
		while (i < 9)
			i = plusOne(i);
		assert (i == 10); // does not hold
	}
}
