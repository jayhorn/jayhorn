
public class UnsatIssue123 {
	public static void main(String[] args) {
		java.util.Random random = new java.util.Random(42);
		int i=0;
		/*
		 * The problem is that inlining nextBoolean will just put one uninitializied
		 * boolean in place of the call. This doesn't work if the call happens inside
		 * a loop.
		 */
		while (random.nextBoolean()) i=1;
		assert i==0;
	}
}
