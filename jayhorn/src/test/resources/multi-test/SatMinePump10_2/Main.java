
import java.util.Random;

public class Main {

	public static void main(String[] args) {
		randomSequenceOfActions(3);
	}

	public static void randomSequenceOfActions(int maxLength) {
		Actions a = new Actions();

		int counter = 0;
		while (counter < maxLength) {
			counter++;
			a.waterRise();
		}
	}
}
