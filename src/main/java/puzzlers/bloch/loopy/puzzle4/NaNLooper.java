package puzzlers.bloch.loopy.puzzle4;

/**
 * The Non-Self-Equal Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 29
 * <p>
 * Problem:
 * How can you create a loop `while (i != i)` that runs forever?
 * <p>
 * Example:
 * double i = Double.NaN;
 * while (i != i) {
 * // This loop is infinite!
 * }
 * <p>
 * Explanation:
 * - According to IEEE 754 floating-point arithmetic and
 * JLS 15.21.1, the value `NaN` (Not a Number) is unordered.
 * - This means the numerical comparison operators (<, <=, >, >=)
 * return false if either or both operands are NaN.
 * - Crucially, the equality operator (==) returns false if either
 * operand is NaN, even if BOTH are NaN.
 * - Conversely, the inequality operator (!=) returns true if
 * either operand is NaN.
 * <p>
 * Step-by-step:
 * 1. Initialize `i` as `Double.NaN`.
 * 2. Evaluate `i != i`.
 * 3. Because `i` is NaN, the comparison `NaN != NaN` is true.
 * 4. The loop continues indefinitely.
 * <p>
 * Lesson:
 * - `NaN` is the only value in Java that is not equal to itself.
 * - To check if a value is NaN, never use `x == Double.NaN`.
 * Instead, use `Double.isNaN(x)`.
 * <p>
 * Safer alternative:
 * if (Double.isNaN(i)) { ... }
 * <p>
 * Output demonstration:
 * double i = Double.NaN;
 * System.out.println(i == i); // Prints false
 * System.out.println(i != i); // Prints true
 */
public class NaNLooper {
    public static void main(String[] args) {
        double i = Double.NaN;

        if (i != i) {
            System.out.println("i is definitely NaN!");
        }
    }
}