class SatAssert1
{
  public static void main(String[] args)
  {
	  //int N = Integer.parseInt(args[0]);
	  
    java.util.Random random = new java.util.Random(42);
    
    int i=random.nextInt();
        


    if(i>=10)
      assert i>=10 : "my super assertion"; // should hold

    if(i>=20)
      assert i>=10 : "my super assertion"; // should hold
  }
}

