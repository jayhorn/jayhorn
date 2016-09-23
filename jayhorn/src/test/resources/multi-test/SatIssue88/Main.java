
import java.util.Random;
import MinePumpSystem.Environment;

public class Main {

	public static void main(String[] args) {
		randomSequenceOfActions(3);
	}

	public static boolean getBoolean() {
		Random random = new Random();
		return random.nextBoolean();
	}

	public static void randomSequenceOfActions(int maxLength) {
		Actions a = new Actions();

		int counter = 0;
		while (counter < maxLength) {
			counter++;

			boolean action1 = getBoolean();

			if (action1) {
				a.waterRise();
			}
		}
	}
}
