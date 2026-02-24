package puzzlers.bloch.advanced.puzzle2;

/**
 * The Computational equality (==) violates Reflexivity and Transitivity, but honors Symmetry
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 87
 * <p>
 * Problem:
 * In mathematics, an equivalence relation must be reflexive, symmetric, and
 * transitive. Why does Java's {@code ==} operator fail to meet these
 * mathematical standards for primitive types?
 * <p>
 * Explanation:
 * While we expect {@code ==} to behave like the mathematical {@code =}, Java's
 * implementation for primitives contains edge cases that break two of the
 * three fundamental properties of equivalence:
 * <p>
 * 1. **Reflexivity (x = x):**
 * - Violated by **NaN** (Not-a-Number).
 * - According to IEEE 754 and the JLS, {@code Double.NaN == Double.NaN} is
 * always {@code false}. A value is not equal to itself.
 * <p>
 *
 * <p>
 * 2. **Transitivity (x = y and y = z -> x = z):**
 * - Violated by **precision-losing conversions** during numeric promotion.
 * - When comparing a high-precision type ({@code int} or {@code long}) to a
 * lower-precision floating-point type ({@code float} or {@code double}),
 * the integer is promoted to the floating-point type.
 * - Multiple distinct large integers can map to the same floating-point
 * representation.
 * <p>
 *
 * <p>
 * 3. **Symmetry (x = y -> y = x):**
 * - This is the **only** property that Java's {@code ==} consistently honors
 * for primitive types. If {@code a == b}, then {@code b == a} is guaranteed.
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Reflexive Failure:** Assign {@code double x = Double.NaN;}. The
 * expression {@code x == x} evaluates to {@code false}.
 * 2. **Transitive Failure:**
 * - Let x be a large {@code long}.
 * - Let f be a {@code float} created from x.
 * - Let y be x + 1.
 * - Because {@code float} has only 24 bits of significand, it cannot
 * distinguish between x and y if they are large enough.
 * - Thus, {@code x == f} is true, {@code f == y} is true, but
 * {@code x == y} is false.
 * <p>
 * The Fix:
 * - When dealing with floating-point numbers, never use {@code ==}. Use an
 * "epsilon" comparison: {@code Math.abs(a - b) < epsilon}.
 * - For {@code NaN}, use {@code Double.isNaN(x)} or {@code Float.isNaN(x)}.
 * - Be cautious when comparing mixed types (e.g., {@code long} and {@code double}).
 * <p>
 * Lesson:
 * - Computational equality is not always mathematical equality.
 * - Floating-point arithmetic and automatic type promotion introduce
 * "fuzzy" boundaries that break classical logic.
 * <p>
 * Output:
 * // Reflexivity: false (for NaN)
 * // Transitivity: false (for large integer/float comparisons)
 * // Symmetry: true
 */
public class EqualityParadox {
    public static void main(String[] args) {
        // 1. Reflexivity Violation
        double n = Double.NaN;
        System.out.println("Reflexive (NaN == NaN): " + (n == n));

        // 2. Transitivity Violation (int -> float)
        int i = 16777217; // 2^24 + 1
        float f = i;      // Lost precision: becomes 16777216.0
        int j = 16777216; // 2^24

        // i == f (true due to promotion), f == j (true), but i == j (false)
        System.out.println("Transitive: " + (i == f && f == j && i != j));

        // 3. Symmetry (Always holds for primitives)
        System.out.println("Symmetry: " + ((i == f) == (f == i)));
    }
}