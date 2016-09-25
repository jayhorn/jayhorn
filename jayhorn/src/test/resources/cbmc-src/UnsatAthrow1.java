class A extends Exception
{
}

class UnsatAthrow1
{
  public static void main(String[] args)
  {
    A a = new A();
    try {
      throw a;
    }
    catch (Exception e)
    {
      assert false;
    }
  }
}
