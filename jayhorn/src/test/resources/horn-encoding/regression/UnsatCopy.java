class UnsatCopy {
  int i;

  public static void main(String[] args)  { 
    UnsatCopy a = new UnsatCopy();
    a.i = 42;
    UnsatCopy b = a;
    assert b.i==41;
  }
}
