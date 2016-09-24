public class SatIssue86Minimal {
	public static void main(String[] args) {
		int N = args.length;
		//passes if we propagate N.
		for (int i = 0; i < N; i++) {
			args[i] = null;
		}
	}
}
