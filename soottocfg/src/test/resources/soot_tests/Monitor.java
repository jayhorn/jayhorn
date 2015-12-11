package soot_tests;

public class Monitor {
	Object o;

	public void doSth() {

		synchronized (o) {

		}

		System.out.println();
	}

}
