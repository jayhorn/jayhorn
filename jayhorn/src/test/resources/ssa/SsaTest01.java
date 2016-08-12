package ssa;

public class SsaTest01 {
//	public static int plusOne(int x) {
//		return x + 1;
//	}

	public void f1() {
		int i = 0;
		while (i < 9)
			i = i + 1;
		assert (i == 10);
	}

//	public void f2() {
//		int i = 0;
//		while (i < 9)
//			i = plusOne(i);
//		assert (i == 10);
//	}
}
