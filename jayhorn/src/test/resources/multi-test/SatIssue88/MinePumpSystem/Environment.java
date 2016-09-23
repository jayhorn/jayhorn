package MinePumpSystem; 

public  class  Environment {	

	private int waterLevel = 1;

	public void waterRise() {
		switch (waterLevel) {
		case 0:
			waterLevel = 1;
			break;
		case 1:
			waterLevel = 2;
			break;
		}
	}
}
