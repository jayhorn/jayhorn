
public class UnsatInnerClass {

  public static void main(String[] args) {
    Node m = new Node();
    assert m.elem != 0;
  }

  static class Node {
    int elem;

    Node swapNode() {
      if (elem == 0)
        assert false;

      return this;
    }
  }
}
