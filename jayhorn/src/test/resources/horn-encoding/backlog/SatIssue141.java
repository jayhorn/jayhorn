public class SatIssue141 {

    public static void sort(Object[] a) {
        Object[] aux = (Object[]) a.clone();
        mergeSort(aux);
    }

    private static void mergeSort(Object[] src) {
    }

    public static void main(String[] args) {
	java.util.Random r = new java.util.Random();
        final int N = r.nextInt(100);
        if (N < 1) return;

        Integer data[] = new Integer[N];
        for (int i = 0; i < N; i++) {
            data[i] = r.nextInt(); 
        }

        sort(data);
    }

}
