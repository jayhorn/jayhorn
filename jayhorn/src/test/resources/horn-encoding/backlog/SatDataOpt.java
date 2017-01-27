class SatDataOpt {

  private int innerIt, outerIt;

  public static void main(String args[]) {
    SatDataOpt x = new SatDataOpt();
    // these get removed
    x.innerIt = 2;
    x.outerIt = 4;


    int cost = 0;
    for (int i = 0; i < x.outerIt; i++) {
      if (args!=null) {
        if (x.outerIt-i>=0) {
          for (int j = 0; j < x.innerIt; j++) {
            cost++;
          }
        }
      }
    }

    assert(cost <= 8);
  }
}
