class Basic2
{
  void other_method(int q)
  {
    q++;
  }
}

class SatBasic {
    public static void main(String[] args) {
      some_method(123, 456);
      Basic2 b=new Basic2();
      b.other_method(123);
    }
    
    public static void some_method(int p, int q)
    {
    }

    int some_field;
}

