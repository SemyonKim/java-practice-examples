package puzzlers.valeev.structure.mistake1;

import java.util.stream.IntStream;

/**
 * Loop Overflow (Infinite Loops at Boundary Values)
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 21
 * <p>
 * Problem:
 * When using a "less-than-or-equal-to" (<=) condition in a loop, if the upper bound
 * is the maximum value of the type (e.g., Integer.MAX_VALUE), the loop counter
 * will overflow after the final iteration, causing an infinite loop.
 * <p>
 * Expected behavior:
 * The loop should execute until {@code i} reaches {@code hi}, then terminate.
 * <p>
 * Actual behavior:
 * If {@code hi == Integer.MAX_VALUE}, the expression {@code i++} after the final
 * iteration causes {@code i} to wrap around to {@code Integer.MIN_VALUE}.
 * Since {@code Integer.MIN_VALUE <= Integer.MAX_VALUE} is true, the loop continues
 * indefinitely.
 * <p>
 * Explanation:
 * - Two's Complement Wrap-around: In Java, integer types are signed. Incrementing
 * the maximum value results in the minimum value (e.g., 2,147,483,647 + 1 = -2,147,483,648).
 * - Termination Failure: The loop condition {@code i <= hi} never evaluates to false
 * because no value of {@code int} is greater than {@code Integer.MAX_VALUE}.
 * <p>
 * Fixes:
 * - Add a lower-bound check to the condition: {@code for (int i = lo; lo <= i && i <= hi; i++)}.
 * This detects when {@code i} has wrapped around.
 * - Use a {@code long} for the loop counter if the range allows it.
 * - Use the Stream API: {@code IntStream.rangeClosed(lo, hi)}. The internal
 * implementation of {@code rangeClosed} handles the {@code MAX_VALUE} case correctly.
 * <p>
 * Lesson:
 * - Be extremely cautious with inclusive loops ({@code <=}) when the boundary
 * could potentially be the limit of the data type.
 * - Modern functional approaches like {@code IntStream} are often safer and
 * more expressive than manual index management.
 */
public class LoopOverflowMistake {

    static void process(int i) {
        // Dummy processing method
    }

    static void demonstrateMistake(int lo, int hi) {
        System.out.println("Starting mistake loop...");
        // MISTAKE: If hi is Integer.MAX_VALUE, this is an infinite loop
        // for (int i = lo; i <= hi; i++) {
        //     process(i);
        //     if (i % 1_000_000_000 == 0) System.out.println("Still running: " + i);
        // }
    }

    static void demonstrateFixedWithCondition(int lo, int hi) {
        System.out.println("Starting fixed loop (condition-based)...");
        // FIX: lo <= i ensures we haven't wrapped around to a negative number
        for (int i = lo; lo <= i && i <= hi; i++) {
            process(i);
        }
        System.out.println("Loop finished successfully.");
    }

    static void demonstrateFixedWithStream(int lo, int hi) {
        System.out.println("Starting fixed loop (Stream-based)...");
        // FIX: rangeClosed handles MAX_VALUE properly
        IntStream.rangeClosed(lo, hi).forEach(LoopOverflowMistake::process);
        System.out.println("Stream finished successfully.");
    }

    public static void main(String[] args) {
        int lo = Integer.MAX_VALUE - 5;
        int hi = Integer.MAX_VALUE;

        // demonstrateMistake(lo, hi); // WARNING: This will never end
        demonstrateFixedWithCondition(lo, hi);
        demonstrateFixedWithStream(lo, hi);
    }
}