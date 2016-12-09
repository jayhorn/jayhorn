public class Unsat06 {
	public static void main(final java.lang.String[] args) {
		int size = 10;
		int a[] = new int[size];
		for (int i = 0; i < size; i++) {
			a[i] = i;
		}
		assert a[3] != 3;
	}
}
