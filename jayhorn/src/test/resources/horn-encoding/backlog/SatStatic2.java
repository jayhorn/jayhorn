
class A {
  public static int i;
};

class SatStatic2 {
  public static void main(String[] args)  { 
    assert A.i == 0;
  }
}
