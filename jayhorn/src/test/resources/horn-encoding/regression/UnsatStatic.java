
class A {
  public static int i;
};

class UnsatStatic {
  public static void main(String[] args)  { 
    assert A.i != 0;
  }
}