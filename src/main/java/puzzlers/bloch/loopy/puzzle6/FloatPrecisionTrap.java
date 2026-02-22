package puzzlers.bloch.loopy.puzzle6;

/**
 * The Floating-Point Step-or-Stall Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 34
 * <p>
 * Problem:
 * Why can a loop using a `float` counter either run forever OR
 * fail to execute even once?
 * <p>
 * Example:
 * final int START = 2_000_000_000;
 * int count = 0;
 * for (float f = START; f < START + 50; f++) count++;
 * System.out.println(count);
 * <p>
 * Explanation:
 * - A `float` uses 24 bits for its significand. An `int` uses 31 bits
 * (plus a sign bit). When you assign a large `int` to a `float`, you
 * lose precision immediately if the number requires more than 24 bits.
 * - Outcome A (Infinite Loop): Once `f` is 2 billion, its ULP (Unit
 * in the Last Place) is 128. Adding 1.0f to it results in 2,000,000,001,
 * but the `float` type cannot represent that. It rounds back to
 * 2,000,000,000. The loop "stalls" and runs forever.
 * - Outcome B (Immediate Termination): The loop condition `f < START + 50`
 * involves an `int` addition (`START + 50`) which is then converted
 * to a `float` for comparison. Because of the 128-unit gap, both
 * 2,000,000,000 and 2,000,000,050 might round to the same `float` value.
 * If `f` is not strictly less than the rounded limit, the loop
 * prints 0 and exits.
 * <p>
 * Step-by-step:
 * 1. `START` is 2,000,000,000.
 * 2. `float f` is initialized. Due to precision, it is an approximation.
 * 3. `f++` tries to add 1, but the gap (ULP) is too large (128), so
 * the value doesn't change.
 * 4. The comparison `f < START + 50` is performed using `float` math,
 * which is also subject to rounding errors.
 * <p>
 * Lesson:
 * - Never use floating-point types (`float`, `double`) as loop counters.
 * - The precision mismatch between `int` (31-bit) and `float` (24-bit)
 * makes increments of 1.0 invisible at large scales.
 * <p>
 * Output:
 * // Either hangs (infinite loop) or prints 0.
 */
public class FloatPrecisionTrap {
    public static void main(String[] args) {
        final int START = 2_000_000_000;
        int count = 0;

        // This loop is fundamentally unreliable
        for (float f = START; f < START + 50; f++) count++;

        System.out.println("Loop logic processed.");
    }
}