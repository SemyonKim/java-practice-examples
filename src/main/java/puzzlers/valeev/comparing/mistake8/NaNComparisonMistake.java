package puzzlers.valeev.comparing.mistake8;

/**
 * Ignoring Possible NaN Values in Comparison Methods
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 66
 * <p>
 * Problem:
 * Why does a manual comparison of double values fail to maintain the basic
 * rules of reflexivity and symmetry when Double.NaN is involved?
 * <p>
 * Expected behavior:
 * Developers expect that for any value x, x.compareTo(x) should return 0,
 * and that if x.compareTo(y) > 0, then y.compareTo(x) must be < 0.
 * <p>
 * Actual behavior:
 * Using manual ternary checks like (d == o.d ? 0 : d < o.d ? -1 : 1) causes
 * NaN to be "greater than" everything, including itself. This violates the
 * comparison contract and can break sorting algorithms or TreeSet logic.
 * <p>
 * Explanation:
 * - The NaN Nature: According to IEEE 754, any comparison involving NaN
 * (except !=) returns false. Specifically, (NaN == NaN), (NaN < x), and
 * (x < NaN) are all false.
 * - Logic Trap: In the manual implementation, if (d == o.d) is false and
 * (d < o.d) is false, the code defaults to returning 1. Since both checks
 * fail for NaN, it always returns 1.
 * - 0.0 vs -0.0: Manual comparisons often treat 0.0 and -0.0 as equal,
 * but in some contexts (like strict mathematical ordering), 0.0 is
 * considered larger than -0.0.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: NaN vs 0.0
 * - NaN == 0.0 is false.
 * - NaN < 0.0 is false.
 * - Result: 1 (NaN is "greater than" 0).
 * - Case: 0.0 vs NaN
 * - 0.0 == NaN is false.
 * - 0.0 < NaN is false.
 * - Result: 1 (0 is "greater than" NaN).
 * - Violation: Both x > y and y > x are reported as true.
 * <p>
 * Fixes:
 * - Use Double.compare(d1, d2): This library method is specifically
 * designed to handle NaN and signed zeros. It treats NaN as equal to
 * itself and greater than Double.POSITIVE_INFINITY.
 * - Use Comparator.comparingDouble(): For custom comparators, use
 * the built-in JDK static factories.
 * - Precondition Checks: If your domain logic does not allow NaN,
 * validate inputs in the constructor using Double.isNaN().
 * <p>
 * Lesson:
 * - Floating-point arithmetic has edge cases that standard operators (==, <, >)
 * cannot handle correctly in a sorting context.
 * - Library methods like Double.compare() are tested for these corner cases
 * and should always be preferred over manual ternary logic.
 * <p>
 * Output:
 * - x.compareTo(y) > 0: true (NaN > 0)
 * - y.compareTo(x) > 0: true (0 > NaN)
 * - x.compareTo(x) > 0: true (NaN > NaN)
 */
public class NaNComparisonMistake {

    static class DoubleObj implements Comparable<DoubleObj> {
        final double d;

        DoubleObj(double d) {
            this.d = d;
        }

        @Override
        public int compareTo(DoubleObj o) {
            // BUG: Fails to handle NaN correctly
            return d == o.d ? 0 : d < o.d ? -1 : 1;
        }

        // FIX:
        // @Override
        // public int compareTo(DoubleObj o) {
        //     return Double.compare(this.d, o.d);
        // }
    }

    public static void main(String[] args) {
        DoubleObj x = new DoubleObj(Double.NaN);
        DoubleObj y = new DoubleObj(0.0);

        System.out.println("--- NaN Comparison Contract Violation ---");

        // Symmetry Violation
        System.out.println("x.compareTo(y) > 0 (NaN > 0): " + (x.compareTo(y) > 0));
        System.out.println("y.compareTo(x) > 0 (0 > NaN): " + (y.compareTo(x) > 0));

        // Reflexivity Violation
        System.out.println("x.compareTo(x) > 0 (NaN > NaN): " + (x.compareTo(x) > 0));

        // Using the library method to see the difference
        System.out.println("\n--- Correct Behavior (Double.compare) ---");
        System.out.println("Double.compare(NaN, 0.0): " + Double.compare(Double.NaN, 0.0));
        System.out.println("Double.compare(0.0, NaN): " + Double.compare(0.0, Double.NaN));
        System.out.println("Double.compare(NaN, NaN): " + Double.compare(Double.NaN, Double.NaN));
    }
}