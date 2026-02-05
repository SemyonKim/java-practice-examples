package puzzlers.bloch.puzzle1;

/**
 * Puzzle 1: Oddity
 * <p>
 * Source: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   Which of the following methods correctly determines if an integer is odd?
 * <p>
 *     static boolean isOdd(int i) {
 *         return i % 2 == 1;
 *     }
 * <p>
 * Expected behavior:
 *   Return true for odd numbers, false for even numbers.
 * <p>
 * Actual behavior:
 *   - Works for positive odd numbers.
 *   - Fails for negative odd numbers because in Java, the remainder operator (%)
 *     can yield negative results. For example:
 *       -3 % 2 == -1
 *     So isOdd(-3) incorrectly returns false.
 * <p>
 * Lesson:
 *   Do not rely on `i % 2 == 1` to test oddness. Instead, use:
 *     return i % 2 != 0;
 * <p>
 *   This correctly handles both positive and negative odd numbers.
 * <p>
 * Key takeaway:
 *   Always remember that `%` in Java is the remainder operator, not the
 *   mathematical modulus. Its result has the same sign as the dividend.
 */
public class Oddity {

    // Incorrect implementation
    static boolean isOddIncorrect(int i) {
        return i % 2 == 1;
    }

    // Correct implementation
    static boolean isOddCorrect(int i) {
        return i % 2 != 0;
    }

    public static void main(String[] args) {
        int[] testValues = {2, -2, 3, -3};

        for (int val : testValues) {
            System.out.printf("Value: %d%n", val);
            System.out.printf("  isOddIncorrect: %b%n", isOddIncorrect(val));
            System.out.printf("  isOddCorrect:   %b%n%n", isOddCorrect(val));
        }
    }
}