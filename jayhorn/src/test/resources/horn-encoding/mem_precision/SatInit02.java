class SatInit02 {
  private int i;

  public void setI(int i) {
    this.i = i;
  }

  public static void main(String[] args) {
     SatInit02 a = new SatInit02();
     a.i++;
     assert a.i==1;
  }
}

