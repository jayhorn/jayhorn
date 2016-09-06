class SatNative {
  public static void main(String[] args)  { 
    String s1 = new String("test");
    String s2 = new String("test");
    assert(s1.intern()!=s2.intern());
  }
}
