//class A extends Exception {
//}

class UnsatStatic {
	public static void main(String[] args) {
		Exception a = new RuntimeException();
		try {
			throw a;
		} catch (Exception e) {
			/* The catch block becomes:
			 * if (e <: Exception) {assert false;}
			 * However, some bug prevents e from being set.
			 */
			assert e instanceof RuntimeException;
		}
	}
}