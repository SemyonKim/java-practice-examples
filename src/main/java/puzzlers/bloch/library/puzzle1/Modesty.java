package puzzlers.bloch.library.puzzle1;

/**
 * The Absolute Minimum Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 64
 * <p>
 * Problem:
 * If we iterate through every single possible {@code int} value and
 * categorize them into three buckets (0, 1, and 2), how are they
 * distributed, and why does the process crash?
 * <p>
 * Explanation (The Math of 2^32):
 * - A Java {@code int} is a 32-bit signed value, meaning there are exactly
 * 2^32 (4,294,967,296) possible distinct integers.
 * - When dividing this total "population" of numbers into 3 buckets,
 * the distribution is: 2^32 = 1,431,655,765 * 3 + 1.
 * - Because there is a remainder of 1, the buckets cannot be perfectly equal.
 * - The sequence starts at {@code Integer.MIN_VALUE} (-2,147,483,648).
 * Mathematically, -2,147,483,648 = 1 by mod 3.
 * - Since the sequence starts in Bucket 1 and has one "extra" number (Integer.MAX_VALUE = 2^31 - 1)
 * beyond a perfect multiple of 3, Bucket 1 receives the additional value (2^31 = 2 by mod 3 -> 2 - 1 = 1).
 * <p>
 * The Distribution Calculation:
 * 1. **Bucket 0:** floor(2^32 / 3) = 1,431,655,765
 * 2. **Bucket 1:** ceil(2^32 / 3) = 1,431,655,766
 * 3. **Bucket 2:** floor(2^32 / 3) = 1,431,655,765
 * <p>
 * The "Illegal" Result:
 * - The program crashes immediately because {@code Math.abs(Integer.MIN_VALUE)}
 * returns a negative value (-2,147,483,648).
 * - In Java, {@code -2147483648 % 3} results in {@code -2}.
 * - Indexing {@code histogram[-2]} throws an {@code ArrayIndexOutOfBoundsException}.
 * <p>
 * Lesson:
 * - The 32-bit integer space is asymmetrical. There is one more negative
 * number than there are positive numbers, which is why {@code Math.abs}
 * fails on the minimum value.
 * - Uniform distribution is only possible when the modulus is a power of two.
 */
public class Modesty {
    public static void main(String[] args) {
        final int MODULUS = 3;
        int[] histogram = new int[MODULUS];

        int i = Integer.MIN_VALUE;

        try {
            // This loop attempts to visit every single integer value
            do {
                //Output: Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: -2
                //histogram[Math.abs(i) % MODULUS]++;

                //Expected output: 1431655765 1431655766 1431655765
                histogram[mod(i, MODULUS)]++;
            } while (i++ != Integer.MAX_VALUE);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Crashed on value: " + i);
            throw e;
        }

        for (int count : histogram) {
            System.out.print(count + " ");
        }
    }

    //Fixer helper method
    private static int mod(int i, int modulus){
        int result = i % modulus;
        return result < 0 ? result + modulus : result;
    }
}