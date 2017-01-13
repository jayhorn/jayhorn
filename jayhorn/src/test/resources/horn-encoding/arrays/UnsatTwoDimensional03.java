public class UnsatTwoDimensional03 {
	public static void main(final java.lang.String[] args) {
		int a[][] = new int[4][4];
		a[1][2] = 42;
		assert (a[1][2] != 42);
	}
}
