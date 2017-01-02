public class SatLibrary02 {
	public static void main(final java.lang.String[] args) {
		String s = "one,two,three";
		String[] strings = s.split(",");
		if (strings != null)
			assert (strings[1].equals("two"));
	}
}
