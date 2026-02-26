package puzzlers.valeev.numbers.mistake2;

/**
 * Not a Number (NaN) Values
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 40, Section 2
 * <p>
 * Problem:
 * {@code Double.NaN} and {@code Float.NaN} are the only values in Java that are not
 * equal to themselves. This leads to silent failures in logic when using standard
 * comparison operators (==, <, >).
 * <p>
 * Expected behavior:
 * Developers often assume that if a value {@code a} is neither greater than nor
 * less than {@code b}, then {@code a} must be equal to {@code b}.
 * <p>
 * Actual behavior:
 * - Reflexivity: {@code Double.NaN == Double.NaN} is {@code false}.
 * - Total Failure: Any comparison involving NaN ({@code <}, {@code <=}, {@code >},
 * {@code >=}, {@code ==}) returns {@code false}, except for {@code !=} which
 * returns {@code true}.
 * - Implicit Branching: In an {@code if-else if-else} chain, if either operand
 * is NaN, the logic will always fall through to the final {@code else} block,
 * regardless of whether the values are "mathematically" equal.
 * <p>
 *
 * <p>
 * Explanation:
 * - IEEE 754: The standard defines NaN as an unordered value. It represents
 * undefined or unrepresentable operations (like 0.0/0.0).
 * - Static Analysis: Tools like IntelliJ IDEA or Error Prone can detect
 * explicit {@code == Double.NaN} checks, but they cannot easily detect
 * implicit NaN issues in complex logic.
 * <p>
 * Ways to Avoid:
 * - Use Library Methods: Always use {@code Double.isNaN(value)} or
 * {@code Float.isNaN(value)} instead of {@code ==}.
 * - Defensive Assertions: If a method should not receive NaN, use
 * {@code assert !Double.isNaN(a);} to catch bugs during development.
 * - Public API Validation: For public methods, throw an
 * {@code IllegalArgumentException} if NaN is detected to prevent
 * corrupted state further down the line.
 * <p>
 * Lesson:
 * - NaN is a "poison" value; once it enters a calculation, it tends to stay.
 * - Standard comparison logic assumes a total ordering that NaN breaks.
 */
public class NaNMistake {

    // --- Example 1: The Comparison Failure ---
    public static void demonstrateIdentity() {
        // Both print false
        System.out.println("NaN == NaN: " + (Double.NaN == Double.NaN));
        System.out.println("Float NaN == Float NaN: " + (Float.NaN == Float.NaN));
    }

    // --- Example 2: The Logic Fall-through ---
    public static void process(double a, double b) {
        if (a < b) {
            System.out.println("a is less than b");
        } else if (a > b) {
            System.out.println("a is greater than b");
        } else {
            // This branch is taken if a == b OR if either is NaN!
            System.out.println("a is equal to b (or someone is NaN)");
        }
    }

    // --- Example 3: Proper Validation ---
    public static void safeProcess(double a) {
        if (Double.isNaN(a)) {
            throw new IllegalArgumentException("Input cannot be NaN");
        }
        // Proceed with logic...
    }

    public static void main(String[] args) {
        demonstrateIdentity();

        System.out.print("Comparing 5.0 and NaN: ");
        process(5.0, Double.NaN);

        try {
            safeProcess(Double.NaN);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected error: " + e.getMessage());
        }
    }
}