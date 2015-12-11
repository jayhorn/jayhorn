package dynamic_tests;

public class DynamicTest04 {
	/* This tests if the ExceptionTransformer 
	   handles exceptional method returns 
	   correctly.
	*/

	public int nestedThrowTest01() {
		return nestedThrow("X");
	}

	public int nestedThrowTest02() {
		return nestedThrow(null);
	}

	
	private int nestedThrow(Object o) {
		try {
			int result = throwSth(o);
			result ++;
			return result;
		} catch (Throwable t) {
			return -1;
		}
	}

	private int throwSth(Object o) {
		return o.hashCode();
	}
	
}
