class SatCampy01 {

  static int count = 0;

  private static int mid(int i, int j) {
    assert(i <= j);
    int m;
    for (m = j; m-i > j-m; m--);
    return m;
  }
    

  public static int BinarySearch(int arr[], int n) {
    int fst = 0;
    int lst = arr.length - 1;
    int mid = -1;
    while (fst <= lst) {
      //mid = (fst + lst) / 2;
      mid = mid(fst,lst);
      if (arr[mid] < n)
        fst = mid + 1;
      else if (arr[mid] == n)
        break;
      else lst = mid - 1;
      count++;
    }
    return mid;
  }

  public static void main(String args[]) {
    try {
      if (args.length >= 2) {
        int n = Integer.parseInt(args[0]);
        int arr[] = new int[args.length-1];
        for (int i = 1; i < args.length; i++)
          arr[i-1] = Integer.parseInt(args[i]);
        int res = BinarySearch(arr, n);
        //System.out.println(res);
        assert(count <= arr.length+1);
        // what we actually want to prove: assert(count <= Math.log(arr.length)+1);
      }
    } catch (Exception e) { }
  }
}
