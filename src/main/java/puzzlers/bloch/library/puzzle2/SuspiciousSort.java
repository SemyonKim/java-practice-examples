package puzzlers.bloch.library.puzzle2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * The Integer Overflow Comparator Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 65
 * <p>
 * Problem:
 * Why does using a subtraction-based comparator {@code (o2 - o1)} fail to
 * sort an array of random integers in descending order, often resulting
 * in an "UNORDERED" status?
 * <p>
 * Explanation:
 * - The {@code compare(T o1, T o2)} contract requires a positive return value
 * if {@code o1 > o2}, negative if {@code o1 < o2}, and zero if they are equal.
 * - While {@code o2 - o1} seems like a clever shortcut for descending order,
 * it fails when the difference exceeds {@code Integer.MAX_VALUE}.
 * - If {@code o2} is a large positive number and {@code o1} is a large negative
 * number, the result of {@code o2 - o1} will overflow and wrap around to a
 * negative value.
 * - Example: {@code 2,000,000,000 - (-2,000,000,000)} should be 4 billion,
 * but in 32-bit signed math, it wraps to {@code -294,967,296}.
 * - The comparator tells the sort algorithm that {@code 2B} is "smaller"
 * than {@code -2B}, breaking the transitivity required by {@code Arrays.sort}.
 * <p>
 * The "Illegal" Result:
 * - The array remains largely unsorted or "UNORDERED" because the comparison
 * logic is mathematically inconsistent due to overflow.
 * <p>
 * Step-by-step (The Fixes):
 * 1. **Use Integer.compare:** Since Java 7, the static method {@code Integer.compare(o2, o1)}
 * is the safest way to perform this comparison without overflow.
 * 2. **Manual Comparison:** Use explicit {@code if/else} or the ternary operator:
 * {@code (o1 < o2) ? 1 : (o1 > o2) ? -1 : 0}.
 * 3. **Comparator.reverseOrder():** Use the built-in library methods which
 * are already optimized and safe.
 * <p>
 * Lesson:
 * - Never use subtraction-based comparators for signed numeric types (int, long, float, double).
 * - Overflow is a silent killer in logic-heavy operations like sorting.
 * <p>
 * Output:
 * // UNORDERED
 */
public class SuspiciousSort {
    public static void main(String[] args) {
        Random rnd = new Random();
        Integer[] arr = new Integer[100];

        for (int i = 0; i < arr.length; i++) arr[i] = rnd.nextInt();

        Comparator<Integer> cmp = new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                // BUG: Subtraction can overflow
                return o2 - o1;
            }
        };
        Arrays.sort(arr, cmp);
        System.out.println(order(arr));
    }

    enum Order {ASCENDING, DESCENDING, CONSTANT, UNORDERED}

    static Order order(Integer[] a) {
        boolean ascending = false;
        boolean descending = false;

        for (int i = 1; i < a.length; i++) {
            ascending |= (a[i] > a[i - 1]);
            descending |= (a[i] < a[i - 1]);
        }

        if (ascending && !descending) return Order.ASCENDING;
        if (descending && !ascending) return Order.DESCENDING;
        if (!ascending) return Order.CONSTANT; // All elements equal
        return Order.UNORDERED; // Arrays is not sorted
    }
}