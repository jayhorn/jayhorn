public class UnsatIssue142 {

    public static void sort(Object[] a) {
        Object[] aux = new Object[a.length];
        for (int i = 0; i < a.length; i++)
            aux[i] = a[i];
        assert false;
    }

    public static void main(String[] args) {
	java.util.Random r = new java.util.Random();
        final int N = r.nextInt(100);
        if (N < 1) return;

        Object data[] = new Integer[N];
        for (int i = 0; i < N; i++) {
            data[i] = r.nextInt(); 
        }

        sort(data);
    }

}
