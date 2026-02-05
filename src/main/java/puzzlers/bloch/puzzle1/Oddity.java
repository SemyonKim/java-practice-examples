package puzzlers.bloch.puzzle1;

/**
 * Oddity
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   Which of the following methods correctly determines if an integer is odd?
 * <p>
 * Expected behavior:
 *   Return true for odd numbers, false for even numbers.
 * <p>
 * Actual behavior:
 *   - Using `i % 2 == 1` fails for negative odd numbers.
 *   - Correct approaches include `i % 2 != 0` or bit‑wise checking.
 * <p>
 * Lesson:
 *   `%` in Java is the remainder operator, not modulus.
 *   A safer and faster alternative is to check the least significant bit.
 * <p>
 * Key takeaway:
 *   Use `i % 2 != 0` or `(i & 1) != 0` for a reliable oddness test.
 */
public class Oddity {

    static boolean isOddIncorrect(int i) {
        return i % 2 == 1;
    }

    static boolean isOddCorrect(int i) {
        return i % 2 != 0;
    }

    static boolean isOddBitwise(int i) {
        return (i & 1) != 0;
    }

    public static void main(String[] args) {
        int[] testValues = {2, -2, 3, -3};

        for (int val : testValues) {
            System.out.printf("Value: %d%n", val);
            System.out.printf("  isOddIncorrect: %b%n", isOddIncorrect(val));
            System.out.printf("  isOddCorrect:   %b%n", isOddCorrect(val));
            System.out.printf("  isOddBitwise:   %b%n%n", isOddBitwise(val));
        }

        // Micro-benchmark: compare % vs & for oddness check
        final int iterations = 100_000_000;
        int dummy = 0;

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (isOddCorrect(i)) dummy++;
        }
        long durationMod = System.nanoTime() - start;

        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (isOddBitwise(i)) dummy++;
        }
        long durationBitwise = System.nanoTime() - start;

        System.out.println("Benchmark results:");
        System.out.printf("  %% operator:   %d ns%n", durationMod);
        System.out.printf("  & operator:   %d ns%n", durationBitwise);
        System.out.printf("  Dummy result: %d%n", dummy);
    }
}