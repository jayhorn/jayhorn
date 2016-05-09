public class SatLibrary02 {
	public static void main(final java.lang.String[] args) {
		String s = "one,two,three";
		String[] strings = s.split(",");
		assert (strings[1].equals("two"));
	}
}
