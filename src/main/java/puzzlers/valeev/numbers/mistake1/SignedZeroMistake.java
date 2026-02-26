package puzzlers.valeev.numbers.mistake1;

import java.util.HashSet;
import java.util.Set;

/**
 * Signed Zero: +0.0 and -0.0
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 40, Section 1
 * <p>
 * Problem:
 * Java (following IEEE 754) distinguishes between positive zero (+0.0) and negative
 * zero (-0.0). While they are "equal" according to the primitive {@code ==} operator,
 * they behave as distinct values in collections, boxed types, and records.
 * <p>
 * Expected behavior:
 * Developers often expect all "zero" values to be treated identically in sets,
 * maps, and equality checks.
 * <p>
 * Actual behavior:
 * - Primitives: {@code 0.0 == -0.0} returns {@code true}.
 * - Boxing: {@code Double.valueOf(0.0).equals(Double.valueOf(-0.0))} returns {@code false}.
 * - Collections: A {@code HashSet<Double>} can contain both {@code 0.0} and {@code -0.0}
 * simultaneously.
 * - Records: Since Java 16, {@code record} equality uses {@code Double.compare()},
 * treating {@code +0.0} and {@code -0.0} as different.
 * <p>
 * Explanation:
 * - IEEE 754 Standard: This standard dictates that the sign bit is separate. A
 * multiplication like {@code -1 * 0.0} results in {@code -0.0} because the
 * operands have different signs.
 * - Desugaring/Boxing: While {@code ==} compares the mathematical value,
 * {@code Double.equals} and {@code Double.hashCode} are based on the raw bit
 * representation (where the sign bit differs).
 * - Precision Issues: Most decimal fractions (0.1, 0.2, 0.3) cannot be represented
 * exactly in binary, leading to cumulative errors (e.g., {@code 0.1 + 0.2 != 0.3}).
 * <p>
 * Ways to Avoid:
 * - Normalization: Before storing floating-point numbers in fields or collections,
 * normalize zeros: {@code if (d == -0.0) d = 0.0;}.
 * - Avoid Exact Comparisons: Do not use {@code equals()} or {@code ==} for
 * floating-point numbers; use a "tolerance" or "epsilon" check instead.
 * - Collection Choice: Be cautious when using {@code Double} as a key in a
 * {@code HashMap} or an element in a {@code HashSet}.
 * <p>
 * Lesson:
 * - Floating-point "zero" is not a single point but two distinct bit patterns.
 * - "Equality" in Java is context-dependent: {@code ==} is mathematical,
 * while {@code equals()} is representation-based.
 */
public class SignedZeroMistake {

    // --- Example 1: The Boxing Trap ---
    public static void demonstrateBoxing() {
        Double d = 0.0;
        System.out.println("Primitive comparison (d == -0.0): " + (d == -0.0)); // true
        System.out.println("Boxed comparison (d.equals(-0.0)): " + d.equals(-0.0)); // false
    }

    // --- Example 2: The Set Duplicate Trap ---
    public static void demonstrateSetIssue() {
        Set<Double> multiplications = new HashSet<>();
        for (double i = -1; i <= 1; i += 0.5) {
            for (double j = -1; j <= 1; j += 0.5) {
                multiplications.add(i * j);
            }
        }
        // Prints a set containing both -0.0 and 0.0
        System.out.println("Multiplication Set: " + multiplications);
        // Contains [1.0, 0.5, -0.0, -0.5, -1.0, 0.25, -0.25, 0.0]
    }

    // --- Example 3: Record Equality ---
    record Length(double d) {}

    public static void demonstrateRecord() {
        Length l1 = new Length(0.0);
        Length l2 = new Length(-0.0);
        System.out.println("Record equality (l1.equals(l2)): " + l1.equals(l2)); // false
    }

    // --- Example 4: Precision Accumulation ---
    public static void demonstratePrecision() {
        Set<Double> set = new HashSet<>();
        set.add(0.3);
        set.add(0.1 + 0.2);
        // Contains [0.3, 0.30000000000000004]
        System.out.println("Precision Set: " + set);
    }

    public static void main(String[] args) {
        demonstrateBoxing();
        demonstrateSetIssue();
        demonstrateRecord();
        demonstratePrecision();
    }
}