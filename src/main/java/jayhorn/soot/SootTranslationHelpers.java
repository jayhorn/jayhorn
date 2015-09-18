/**
 * 
 */
package jayhorn.soot;

import jayhorn.cfg.type.Type;

/**
 * @author schaef
 *
 */
public enum SootTranslationHelpers {
	INSTANCE;
	
	public static SootTranslationHelpers v() {
		return INSTANCE;
	}

	public Type translateType(soot.Type sootType) {
		return null;
	}

}
