class A {
}

class SatArrayAssign
{
  public static void main(String[] args)
  {
    int size=10;
    int int_array[]=new int[size];
    
    for(int i=0; i<size; i++)
      int_array[i]=i;

//    assert int_array[7] == 7;

    A what_not_array[]=new A[size];
    
    assert what_not_array.length == size;
  }
}

