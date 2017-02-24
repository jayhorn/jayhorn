
class A {
  public static int i;
};

class SatStatic3 {
  public static void main(String[] args)  {
	/*
	 * The problem here is that the first assert introduces
	 * an if-then-else which prevents us from removing some 
	 * pull/push opertaions. 
	 */
    assert A.i == 0;
    A.i = 5;
    assert A.i!=0;
  }
}
