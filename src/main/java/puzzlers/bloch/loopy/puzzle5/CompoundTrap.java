package puzzlers.bloch.loopy.puzzle5;

/**
 * The Shifting Short Infinite Loop
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 31
 * <p>
 * Problem:
 * Why does `while (i != 0) i >>>= 1;` never terminate if `i` is a `short`
 * initialized to -1?
 * <p>
 * Example:
 * short i = -1;
 * while (i != 0) {
 * i >>>= 1; // Infinite loop!
 * }
 * <p>
 * Explanation:
 * - The compound assignment `i >>>= 1` is equivalent to:
 * `i = (short)(i >>> 1)`
 * - Step 1 (Promotion): `i` (-1) is promoted to an `int`. Because
 * it is a signed `short`, it becomes `0xFFFFFFFF`.
 * - Step 2 (Shift): `0xFFFFFFFF >>> 1` results in `0x7FFFFFFF`.
 * - Step 3 (Cast): The `int` is cast back to `short`. The high bits
 * are truncated, and `0x7FFFFFFF` becomes `0xFFFF`.
 * - Result: After the operation, `i` is still -1!
 * <p>
 * Step-by-step:
 * 1. `i` is -1 (`0xFFFF`)
 * 2. Promote to int: `0xFFFFFFFF`
 * 3. Logical Shift: `0x7FFFFFFF`
 * 4. Cast to short: `0xFFFF` (Value is back to -1)
 * 5. Loop condition `-1 != 0` is always true.
 * <p>
 * Lesson:
 * - Compound assignment operators silently cast the result back to
 * the left-hand side's type.
 * - This truncation can "undo" the work of an unsigned right shift
 * by removing the zero that was just shifted into the high-order bits.
 * <p>
 * Safer alternative:
 * Use an `int` for bitwise manipulation loops to avoid narrowing casts.
 * <p>
 * Output:
 * // The loop hangs indefinitely.
 */
public class CompoundTrap {
    public static void main(String[] args) {
        short i = -1;
        // WARNING: Running this will cause an infinite loop
        // while (i != 0) {
        //     i >>>= 1;
        // }

        // Let's see one iteration:
        i = (short)(i >>> 1);
        System.out.println("Value after one >>>= 1: " + i); // Still prints -1
    }
}