package puzzlers.bloch.loopy.puzzle3;

/**
 * The Floating-Point Precision Gap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 28
 * <p>
 * Problem:
 * Can you create an infinite loop where the condition is (i == i + 1)?
 * <p>
 * Explanation:
 * - Floating-point numbers (float/double) have a fixed number of
 * significant bits (the significand/mantissa).
 * - As the magnitude of a number increases, the distance between
 * representable numbers—called the ULP (Unit in the Last Place)—also
 * increases.
 * - For a `double`, once the value reaches 2^53, the ULP is 2.0.
 * This means the gap between that number and the next representable
 * double is larger than 1.
 * - When you add 1 to such a large number, the result is rounded
 * back to the original number because the computer cannot represent
 * the small change.
 * <p>
 * Step-by-step:
 * 1. Set i = 10,000,000,000,000,000 (10 quadrillion).
 * 2. Attempt i + 1.
 * 3. The gap (ULP) at this magnitude is greater than 1.0.
 * 4. Binary rounding rules cause (i + 1) to be rounded back to 'i'.
 * 5. The comparison (i == i) is true.
 * <p>
 * Lesson:
 * - Floating-point arithmetic is an approximation of real numbers.
 * - Do not use `float` or `double` for precise counting or loops
 * involving small increments on large values.
 * <p>
 * Safer alternative:
 * Use `long` for large integer counts or `BigDecimal` if you need
 * absolute precision with large decimal values.
 * <p>
 * Output demonstration:
 * double i = 1.0e16;
 * System.out.println(i == (i + 1)); // Prints true
 */
public class Looper {
    public static void main(String[] args) {
        double i = 1.0e16;
        System.out.println("Is " + i + " equal to " + (i + 1) + "? " + (i == i + 1));
    }
}