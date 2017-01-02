class SatParameters {
  int i;
  
  public void set(int i, boolean b) {
    this.i = i;
  }

  static boolean b = true;
  
  public static void main (String args[]) {
    SatParameters a = new SatParameters();
    a.set(3, true);
    a.set(3, b);
  }
}
