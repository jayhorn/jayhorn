/**
 * 
 */
package soottocfg.cfg.type;

import soot.RefType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class StringType extends ReferenceType {

	/**
	 *
	 */
	// TODO: This is a random number distinct from other serialVersionUIDs. Is this the intended value?
	private static final long serialVersionUID = -5047960968146256817L;
	private static final StringType instance = new StringType(
			SootTranslationHelpers.v().getMemoryModel().lookupClassVariable(
					SootTranslationHelpers.v().getClassConstant(RefType.v("java.lang.String"))));

	public StringType(ClassVariable var) {
		super(var);

	}

	public static StringType instance() {
		return instance;
	}
}
