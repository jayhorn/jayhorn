class A
{
  public static int i=17;
  public static int j;
  
  int x,y;
  public A() {
	  x=3;
  }
  
};

class Unsat01
{
  public static void main(String[] args)
  {
    assert A.i == 0;
    A.i = 999;
    assert A.i == 999;
    
    A foo = new A();
  }
}
