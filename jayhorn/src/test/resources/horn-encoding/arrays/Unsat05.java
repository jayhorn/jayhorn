public class Unsat05 {
	public static void main(final java.lang.String[] args) {
		int a[] = new int[4];
		a[10] = 42;
		assert (a[10] != 42);
	}
}
