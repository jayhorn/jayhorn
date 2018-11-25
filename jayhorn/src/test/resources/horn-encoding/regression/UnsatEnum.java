
class Env {

  public enum WaterLevelEnum { low, normal, high }

  private WaterLevelEnum waterLevel = WaterLevelEnum.low;

  public void waterRise() {
    switch (waterLevel) {
    case low:
      waterLevel = WaterLevelEnum.normal;
      break;
    case normal:
      waterLevel = WaterLevelEnum.high;
      break;
    }
  }

  public WaterLevelEnum getWaterLevel() { return waterLevel; }

}

public class UnsatEnum {
    public static void main(String args[]) {
        Env e = new Env ();
        e.waterRise();
        assert e.getWaterLevel() == Env.WaterLevelEnum.high;
    }
}
