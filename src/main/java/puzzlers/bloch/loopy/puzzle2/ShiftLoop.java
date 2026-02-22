package puzzlers.bloch.loopy.puzzle2;

/**
 * The Infinite Shift Loop Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 27
 * <p>
 * Problem:
 * Why does a while loop that shifts by an increasing index 'i' never
 * terminate, while an iterative shift on the value itself works?
 * <p>
 * Example (The Infinite Loop):
 * int i = 0;
 * while (-1 << i != 0) i++;
 * System.out.println(i);
 * <p>
 * Improvement (The Correct Approach):
 * int distance = 0;
 * for (int i = -1; i != 0; i <<= 1) distance++;
 * System.out.println(distance);
 * <p>
 * Explanation:
 * - In the first example, as 'i' reaches 32, the expression `-1 << i`
 * does NOT shift the bits out to zero. Java masks the shift distance
 * to the low-order 5 bits (distance % 32).
 * - Therefore, `-1 << 32` becomes `-1 << 0`, which is -1. The loop
 * condition `-1 != 0` remains true forever.
 * - In the second example, the value of 'i' itself is updated
 * iteratively (`i <<= 1`). Every shift pulls in a zero from the right.
 * - This process does not "ignore" the sign bit; it treats it like any
 * other bit, eventually pushing it (and all other 1s) out the left side.
 * <p>
 * Step-by-step (Improvement):
 * 1. Start: i = 1111...1111 (-1)
 * 2. Iteration 1: i = 1111...1110
 * 3. Iteration 31: i = 1000...0000
 * 4. Iteration 32: i = 0000...0000 (Loop terminates)
 * <p>
 * Lesson:
 * - You cannot empty a variable by shifting it by its bit-width
 * using a variable distance due to the "Modulo 32" rule.
 * - Iterative shifting (`<<=`) correctly moves every bit,
 * including the sign bit, until the value becomes zero.
 * - Note: Java has no `<<<` operator because `<<` already
 * pulls in zeros regardless of the sign bit.
 * <p>
 * Output:
 * // The while loop hangs.
 * // The for loop prints: 32
 */
public class ShiftLoop {
    public static void main(String[] args) {
        // This part is the improvement that works
        int distance = 0;
        for (int i = -1; i != 0; i <<= 1) {
            distance++;
        }
        System.out.println("Iterations to zero: " + distance);
    }
}