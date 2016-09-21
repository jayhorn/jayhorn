public class SatIssue86 {
  public static void main(String[] args) {
        int N = 5;
        try {
                N = Integer.parseInt(args[0]);
        } catch (Exception e) { }

        int a[] = new int[N];
        for (int i = 0; i < N; i++) { 
            a[i] = 2;
        }
  }
}
