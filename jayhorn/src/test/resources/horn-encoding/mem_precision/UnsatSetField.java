class A
{
  public int i;
};

class UnsatSetField
{
	  public static void main(String[] args)
	  {
	    A a=new A();
	    a.i = 999;
	    assert 999 != a.i;
	  }
}
