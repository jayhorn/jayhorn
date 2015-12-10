package dynamic_tests;

public class DynamicTest03 {

	
	public int exceptionTest() {
		int result = withExceptionMethod(0);
		result += withExceptionMethod(5);
		return result;
	}

	private void foo() {}
	
	private int withExceptionMethod(int i) {
		int b=0;
		try {
			int a[] = new int[2];
			b=a[i];
			foo();			
		} catch (ArrayIndexOutOfBoundsException e) {
			b = 20;				
		} finally {
			b+=2;			
		}
		return b;
	}
	
}
