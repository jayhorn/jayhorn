package translation_tests;

public class TranslationTest01 {

//	public int noExceptions(int i) {
//		if (i == 0) {
//			return 1;
//		} else if (i == 2) {
//			return 2;
//		}
//		assert 2 == 1;
//		return 3;
//	}

//	String s1;
//	
//	public void virtualCalls(int i) {
//		Object o;
//		if (i>0) {
//			o = s1;
//		} else {
//			o = new TranslationTest01();
//		}
//		o.toString(); // may not be null.
//	}
	
	int x,y;
	
	public int withException() {
		int b;
		try {
			int a[] = new int[2];
			b=a[3];
			
		} catch (ArrayIndexOutOfBoundsException e) {
			b = 20;
		}
		b=30;
		return b;
	}
	
	
}