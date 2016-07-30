class SatCopy {
  int i;

  public static void main(String[] args)  { 
    SatCopy a = new SatCopy();
    a.i = 42;
    SatCopy b = a;
    assert b.i==42;
  }
}
