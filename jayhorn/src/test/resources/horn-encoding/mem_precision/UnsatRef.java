
class A
{
  public int i;
};

class B
{
  public A a;
}


class UnsatRef
{
	  public static void main(String[] args)
	  {
	    A a = new A();
	    a.i = 42;
	    B b = new B();
	    b.a = a;
	    assert b.a.i != 42;
	  }
}
