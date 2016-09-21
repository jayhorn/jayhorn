public class SatIssue86 {
  public static void main(String[] args) {
	if (args.length<1) return;
        int N = 5;
        try {
                N = Integer.parseInt(args[0]);
        } catch (Exception e) { }
	if (N<0 || N>5) return;

        int a[] = new int[N];
        for (int i = 0; i < N; i++) { 
            a[i] = 2;
        }
  }
}
