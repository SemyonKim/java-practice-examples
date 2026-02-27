package puzzlers.valeev.comparing.mistake4;

import java.math.BigDecimal;

/**
 * Comparing BigDecimals with Different Scales
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 58
 * <p>
 * Problem:
 * Why does comparing two BigDecimal objects that represent the exact same
 * numerical value (like 1.0 and 1.00) return false when using .equals()?
 * <p>
 * Expected behavior:
 * Most developers expect BigDecimal.equals() to function like mathematical
 * equality, where the number of trailing zeros does not affect the result.
 * <p>
 * Actual behavior:
 * The equals() method in BigDecimal compares both the "unscaled value" and
 * the "scale." If the scales differ, the objects are considered unequal even
 * if they are numerically identical.
 * <p>
 * Explanation:
 * - Internal Structure: A BigDecimal consists of an unscaled BigInteger value
 * and an integer scale. For example, 0.1234 can be stored as 1234 * 10^(-4)
 * (scale 4) or 12340 * 10^(-5) (scale 5).
 * - Strict Equality: The equals() implementation is strict; it requires the
 * internal representation to be identical. Since a scale of 1 is not the
 * same as a scale of 2, the check fails.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: "0" vs "0.0"
 * - zero1 is created from "0". Its unscaled value is 0, and its scale is 0.
 * - zero2 is created from "0.0". Its unscaled value is 0, and its scale is 1.
 * - Comparison: zero1.equals(zero2) checks (0 == 0) AND (scale 0 == scale 1).
 * - Result: false.
 * <p>
 * Fixes:
 * - Use compareTo(): This method ignores scale and compares only the
 * numerical value. If a.compareTo(b) == 0, the values are numerically equal.
 * - Normalize with stripTrailingZeros(): If you must use equals(), call this
 * method first. It returns a BigDecimal with the numerical value but the
 * smallest possible scale.
 * <p>
 * Lesson:
 * - For BigDecimal, .equals() defines "identity" (structural equality), while
 * .compareTo() defines "value" (numerical equality).
 * - Be consistent: using BigDecimal as a key in a TreeMap (which uses compareTo)
 * will behave differently than using it in a HashMap (which uses equals/hashCode).
 * <p>
 * Output:
 * - zero1.equals(zero2) -> false
 * - zero1.compareTo(zero2) == 0 -> true
 */
public class BigDecimalScaleMistake {

    public static void main(String[] args) {
        // Initialization with different scales
        BigDecimal zero1 = new BigDecimal("0");
        BigDecimal zero2 = new BigDecimal("0.0");

        System.out.println("--- BigDecimal Scale Comparison ---");
        System.out.println("Scale of zero1: " + zero1.scale()); // 0
        System.out.println("Scale of zero2: " + zero2.scale()); // 1

        System.out.println("\n--- Equality Results ---");
        System.out.println("zero1.equals(zero2): " + zero1.equals(zero2)); // false
        System.out.println("zero1.compareTo(zero2) == 0: " + (zero1.compareTo(zero2) == 0)); // true

        // Using normalization
        System.out.println("\n--- After stripTrailingZeros() ---");
        BigDecimal norm1 = zero1.stripTrailingZeros();
        BigDecimal norm2 = zero2.stripTrailingZeros();

        System.out.println("norm1.scale(): " + norm1.scale());
        System.out.println("norm2.scale(): " + norm2.scale());
        System.out.println("norm1.equals(norm2): " + norm1.equals(norm2)); // true
    }
}