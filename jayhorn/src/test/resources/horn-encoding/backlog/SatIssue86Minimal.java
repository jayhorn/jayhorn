public class SatIssue86Minimal {
	public static void main(String[] args) {
		//int N = args.length;
		//passes if we propagate N.
		//for (int i = 0; i < N; i++) {
		if (args != null && args.length > 0)
			args[0] = null;
		//}
	}
}
