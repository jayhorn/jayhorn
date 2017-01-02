public class SatObjectFromLib {
	public static void main(String args[]) {
		Object i = Integer.valueOf(1);
		if (i != null)
			assert(i instanceof Integer);
	}
}
