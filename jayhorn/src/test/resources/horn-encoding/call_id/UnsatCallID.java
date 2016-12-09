class UnsatCallID
{
  public int i=0;
  public void set(int x) {
	  this.i = x;
  }

  public int get() {
	  return this.i;
  }
  public static void main(String[] args)
  {
	  UnsatCallID a = new UnsatCallID();
	  int i = a.get();
	  int j = a.get();
	  assert false;
  }
}

