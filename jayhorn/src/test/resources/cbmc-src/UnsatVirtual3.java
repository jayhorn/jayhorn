interface A
{
  public void f();
};

class B implements A
{
  public void f()
  {
    assert false;
  }
};

class UnsatVirtual3
{
  public static void main(String[] args)
  {
    A a = new B();
    a.f();
  }
}

