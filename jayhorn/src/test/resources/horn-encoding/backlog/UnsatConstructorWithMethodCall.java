class UnsatConstructorWithMethodCall {
  public int i;
  public int j;
  public int k;

  public UnsatConstructorWithMethodCall() {
    this.i = 1;
    setJ(2);
    this.k = 3;
  }

  void setJ(int j) {
    this.j = j;
  }

  public static void main(String args[]) {
    SatConstructorWithMethodCall x = new SatConstructorWithMethodCall();
    assert (x.i != 1 || x.j != 2 || x.k != 3);
  }
}
