class UnsatAssert3
{
  public static void main(String[] args)
  {
    java.util.Random random = new java.util.Random(42);
    
    int i=random.nextInt();
    
    if(i>=1000)
      if(!(i>1000))
        assert(false); // should fail
  }
}

