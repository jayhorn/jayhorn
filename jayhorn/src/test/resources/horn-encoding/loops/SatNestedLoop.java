public class SatNestedLoop {
	public static void main(String args[]) { 
		int W = 10;
		int H = 10;
		int pixels[][] = new int[W][H];
		for (int x = 0; x < W; x++) {
			for (int y = 0; y < H; y++) {
				pixels[x][y] = 0;
			}
		}
		assert (pixels[2][3] == 0);
	}
}
