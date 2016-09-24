public class UnsatObjectFromLib {
	public static void main(String args[]) {
		Object i = Integer.valueOf(1);
		assert(i instanceof String);
	}
}
