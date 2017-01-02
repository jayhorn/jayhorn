class A {
}

class SatHavoc
{
  public static void main(String[] args)
  {
    A[] array = new A[10];
    assert array[1] == null;
  }
}
