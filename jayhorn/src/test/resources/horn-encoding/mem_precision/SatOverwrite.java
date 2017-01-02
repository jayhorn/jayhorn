class A
{
  public int i;
};

class SatOverwrite
{
	  public static void main(String[] args)
	  {
	    A a1 = new A();
	    a1.i = 42;
	    a1 = new A();
	    assert a1.i == 0;
	  }
}
