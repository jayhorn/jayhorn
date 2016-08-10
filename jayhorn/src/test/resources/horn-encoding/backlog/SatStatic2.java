
class A {
  public static int i;
};

class SatStatic {
  public static void main(String[] args)  { 
    assert A.i == 0;
  }
}