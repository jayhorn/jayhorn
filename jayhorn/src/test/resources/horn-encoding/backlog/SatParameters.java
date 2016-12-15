class SatParameters {
  int i;
  
  public void set(int i, boolean b) {
    this.i = i;
  }

  public static void main (String args[]) {
    SatParameters a = new SatParameters();
    a.set(3, true);
  }
}
