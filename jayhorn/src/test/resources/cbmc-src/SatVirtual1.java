class A
{
  public void f(){}
};

class B extends A
{
  public void f()
  {
    assert false;
  }
};

class SatVirtual1
{
  public static void main(String[] args)
  {
    A a=new A();
    a.f();
  }
}

