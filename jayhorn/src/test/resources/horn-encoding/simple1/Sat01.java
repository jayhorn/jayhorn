
public class Sat01 {

	public static int plusOne(int x) {
		return x + 1;
	}

	public static void main(String[] args) {
		int i = 0;
		while (i < 10)
			i = plusOne(i);
		assert (i == 10); // holds
	}

}
