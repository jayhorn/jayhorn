/**
 * 
 */
package jayhorn.soot.util;


/**
 * @author schaef
 * Collects all sort of constants and lookup tables need during the
 * translation
 */
public class SootKitchenSink {

//	public PointsToAnalysis pointsToAnalysis = null;
//	
//	public AbstractJimpleBasedICFG iCfg = null;

	public MethodInfo currentMethod = null;
	
	private static SootKitchenSink instance = null;
	
	public static SootKitchenSink v() {
		if (instance==null) {
			instance = new SootKitchenSink();
		}
		return instance;
	}
}
