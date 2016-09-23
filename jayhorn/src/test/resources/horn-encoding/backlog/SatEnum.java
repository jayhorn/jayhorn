public class SatEnum {

	public enum WaterLevelEnum {low, normal, high}
	public WaterLevelEnum waterLevel = WaterLevelEnum.normal;

	public static void main (String args[]) {
		SatEnum a = new SatEnum();
		//a.waterLevel = WaterLevelEnum.low;
		//assert a.waterLevel!=null;
		assert WaterLevelEnum.low != null;
	}
}
