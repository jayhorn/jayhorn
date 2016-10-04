class A
{
  public void f(){}
};

class B extends A
{
  public void f()
  {
	  super.f();
    assert false;
  }
};

class UnsatVirtual2
{
  public static void main(String[] args)
  {
    A a=new B();
    a.f();
  }
}

