class AAA
{
  public int i;
  public int j;
};

class UnsatFieldCopy
{
	  public static void main(String[] args)
	  {
	    AAA a = new AAA();
	    a.i = 42;
	    a.j = a.i;
	    assert a.j != a.i;
	  }
}
