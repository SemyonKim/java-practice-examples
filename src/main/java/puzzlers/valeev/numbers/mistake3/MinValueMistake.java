package puzzlers.valeev.numbers.mistake3;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * Double.MIN_VALUE is not the Minimal Value
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 40, Section 3
 * <p>
 * Problem:
 * Unlike {@code Integer.MIN_VALUE}, which is the most negative possible integer,
 * {@code Double.MIN_VALUE} is the smallest **positive** value.
 * <p>
 * Expected behavior:
 * Developers refactoring from {@code int} to {@code double} often assume
 * {@code Double.MIN_VALUE} is the "most negative" number and use it as a
 * starting point for finding a maximum value in an array.
 * <p>
 * Actual behavior:
 * - Value: {@code Double.MIN_VALUE} is approximately 4.9 * 10^(-324) (a tiny positive number).
 * - Logical Error: If an array contains only negative numbers or zero,
 * a "find max" algorithm starting with {@code Double.MIN_VALUE} will
 * incorrectly return 4.9 * 10^(-324) instead of the actual maximum.
 * <p>
 * Explanation:
 * - Asymmetry: Floating-point constants are named differently than integral ones.
 * {@code Double.MAX_VALUE} is the largest finite value, but its counterpart is
 * not {@code MIN_VALUE}.
 * - The Constant Gap: There is no single named constant for the "most negative
 * finite double." You must use {@code -Double.MAX_VALUE}.
 * <p>
 *
 * <p>
 * Ways to Avoid:
 * - Use Correct Constants: When you need the most negative finite number,
 * use {@code -Double.MAX_VALUE}.
 * - Use Infinity: For initialization in "find max" algorithms,
 * {@code Double.NEGATIVE_INFINITY} is usually safer and more expressive.
 * - Leverage Standard Libraries: Use {@code Arrays.stream(data).max()} or
 * Guava's {@code Doubles.max()}. These libraries are already hardened against
 * these edge cases.
 * - Review Refactors: Pay close attention when changing types from
 * {@code int} to {@code double}; the semantics of "MIN" change significantly.
 * <p>
 * Lesson:
 * - {@code Double.MIN_VALUE} is about precision (the value closest to zero),
 * not magnitude (the value furthest left on the number line).
 */
public class MinValueMistake {

    // --- The Incorrect Implementation ---
    /**
     * Erroneously uses Double.MIN_VALUE as the starting point.
     * Fails if the input contains only negative numbers.
     */
    static double incorrectMax(double... data) {
        double max = Double.MIN_VALUE; // ERROR: This is a positive number!
        for (double d : data) {
            max = Math.max(max, d);
        }
        return max;
    }

    // --- The Correct Implementation ---
    static double correctMax(double... data) {
        if (data.length == 0) throw new IllegalArgumentException("Array is empty");

        double max = Double.NEGATIVE_INFINITY;
        for (double d : data) {
            max = Math.max(max, d);
        }
        return max;
    }

    // --- Using Stream API ---
    static OptionalDouble streamMax(double... data) {
        return Arrays.stream(data).max();
    }

    public static void main(String[] args) {
        double[] negatives = {-5.0, -10.5, -2.0};

        System.out.println("Finding max of {-5.0, -10.5, -2.0}:");
        System.out.println("Incorrect result: " + incorrectMax(negatives)); // Prints 4.9E-324
        System.out.println("Correct result: " + correctMax(negatives));     // Prints -2.0

        System.out.println("\nValue of Double.MIN_VALUE: " + Double.MIN_VALUE);
    }
}