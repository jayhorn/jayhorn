public class Unsat01 {

	public static void main(String[] args) {
		int i = 0;
		while (i < 9)
			i = i + 1;
		assert (i == 10); // does not hold
	}
}
