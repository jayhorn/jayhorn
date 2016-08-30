class A {
  int i[];
}

public class Sat03 {
	public static void main(final java.lang.String[] args) {
		A a = new A();
		a.i = new int[4];
		int x=a.i.length;
		assert (x == 4);
	}
}
