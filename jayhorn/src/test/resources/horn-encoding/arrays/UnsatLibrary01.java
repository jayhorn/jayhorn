public class UnsatLibrary01 {
	public static void main(final java.lang.String[] args) {
		String s = "one,two,three";
		String[] strings = s.split(",");
		assert (strings.length<3);
	}
}
