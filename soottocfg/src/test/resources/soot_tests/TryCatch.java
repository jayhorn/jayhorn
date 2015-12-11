package soot_tests;

public class TryCatch {

	@SuppressWarnings("finally")
	int doSth(Object o) {
		int i = 0;
		try {
			o.notify();
			i = 1;
		} catch (NullPointerException e) {
			i = -1;
		} finally {
			return i;
		}
	}

}
