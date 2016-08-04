class A extends Exception
{
}

class B extends A {}

class UnsatThrows
{
  public static void main(String[] args)
  {
    A a = new B();
    try {
      throw a;
    }
    catch (A e)
    {
      assert false;
    }
  }
}
