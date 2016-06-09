/**
 * 
 */
package soottocfg.soot.transformers;

/**
 * @author schaef
 * Sketch:
 * 
 * Inline access to final fields to minimize the number of heap interactions.
 * E.g., given a class
 * class A {
 *   public final int x = 42;
 * } 
 * and a method
 * 
 * void foo() {
 *   A a = new A();
 *   int i = a.x;
 * }
 * 
 * we replace the final field x of a by a local variable ax and get
 * void foo() {
 *   int ax=42
 *   A a = new A(ax);
 *   int i = ax;
 * }
 * 
 * in addition, we add an initialization statement to the constructor
 * of A and make A.x non-final:
 * public A(int ax) {
 *   x = ax;
 * } 
 */
public class FinalFieldTransformer {

	public void inlineFinalFields() {
		
	}
}
