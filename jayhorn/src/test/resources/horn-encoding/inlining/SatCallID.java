class SatCallID
{
  public int i=0;
  public void foo(int x) {
	  this.i = x;
  }

  public void asrt1() {
	  foo(3);
	  assert i==3;
  }

  public void asrt2() {
	  foo(42);
	  assert i==42;
  }
  
  
  public static void main(String[] args)
  {
	  SatCallID a = new SatCallID();
	  SatCallID b = new SatCallID();
//	  a.foo(1);
//	  b.foo(2);	  
//	  assert a.i!=b.i;
	  
	  a.asrt1();
	  a.asrt2();
  }
}

