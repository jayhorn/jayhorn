package MinePumpSystem; 

public   class  Environment {
	

	public enum  WaterLevelEnum {
		low ,  normal ,  high}

	private WaterLevelEnum waterLevel = WaterLevelEnum.normal;

	void lowerWaterLevel() {
		switch (waterLevel) {
		case high:
			waterLevel = WaterLevelEnum.normal;
			break;
		case normal:
			waterLevel = WaterLevelEnum.low;
			break;
		}
	}


}
