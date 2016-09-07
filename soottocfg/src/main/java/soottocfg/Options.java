/**
 * 
 */
package soottocfg;

/**
 * @author schaef
 *
 */
public class Options {

	private boolean useBuiltInSpecs = false;

	private boolean trackAllocSite = false;

	public boolean getTrackAllocSite() {
		return trackAllocSite;
	}

	public void setTrackAllocSite(boolean val) {
		trackAllocSite = val;
	}

	public boolean useBuiltInSpecs() {
		return useBuiltInSpecs;
	}

	public void setBuiltInSpecs(boolean val) {
		useBuiltInSpecs = val;
	}

	private static Options options;

	public static void resetInstance() {
		options = null;
	}

	public static Options v() {
		if (null == options) {
			options = new Options();
		}
		return options;
	}

	private Options() {
	}
}
