class A {
//  int value = 0;
}

class SatArrayInit
{
  public static void main(String[] args)
  {
    int size = 10;
    A[] array = new A[size];

    for (int i = 0; i < size; i++) {
      array[i] = new A();  
      //assert array[i] != null;    
    }
    assert array[3] != null;
  }
}
