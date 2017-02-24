public class UnsatInnerClass
{
    private int T = 42;
    
    public UnsatInnerClass() {
        new Node();
        assert false;
    }
    
    public class Node
    {
      int test = T;
    }

  public static void main(String args[]) {
    UnsatInnerClass bt = new UnsatInnerClass();
  }
}
