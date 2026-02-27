package puzzlers.valeev.comparing.mistake7;

import java.util.*;

/**
 * Using Subtraction when Comparing Numbers
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 65
 * <p>
 * Problem:
 * Why does using (a - b) in a compareTo method lead to intermittent sorting
 * errors, IllegalArgumentExceptions, or "missing" elements in a TreeSet?
 * <p>
 * Expected behavior:
 * Developers use subtraction because the Comparable contract allows any
 * negative/positive integer to represent "less than" or "greater than."
 * For small, positive numbers, this works perfectly.
 * <p>
 * Actual behavior:
 * When comparing a large positive number with a large negative number, the
 * result can overflow the 32-bit integer range. This flips the sign,
 * causing the comparison to return the mathematically opposite result.
 * <p>
 * Explanation:
 * - Overflow: 2,000,000,000 - (-2,000,000,000) results in -294,967,296
 * due to integer wrap-around.
 * - Transitivity Violation: Overflow breaks the rule that if x > y and y > z,
 * then x must be > z. This is the "General Contract" of Comparable.
 * - TimSort Thresholds: Java's sorting algorithm (TimSort) only checks for
 * contract violations on arrays of 32 elements or more. On smaller arrays,
 * it uses a simpler algorithm that silently produces wrong orders.
 * - Data Structure Corruption: TreeSet and TreeMap rely on the comparison
 * to build Red-Black trees. If the comparison is inconsistent, the tree
 * becomes "broken," making existing elements unfindable.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: The TimSort Exception
 * - Create an array of 32 IntObj instances using Random(209).
 * - TimSort runs, detects that the transitivity rule is violated, and throws
 * IllegalArgumentException. Note: seeds like 0-208 might not trigger it.
 * - Case: The TreeSet Disappearing Act
 * - Add a large negative, a large positive, and a zero to a TreeSet.
 * - Remove the large negative. Because the comparison logic is broken, the
 * tree traversal fails to locate the large positive element even though
 * it is still physically in the set.
 * - Case: Records and HashSet Treeification
 * - Java Records generate hashCode() automatically. When many records share
 * the same hash (collisions), HashSet converts the bucket into a
 * Red-Black tree, which then relies on the broken compareTo.
 * <p>
 * Fixes:
 * - Always use Integer.compare(a, b) or Long.compare(a, b).
 * - For multiple fields, use Comparator.comparingInt(obj -> obj.field).
 * - Never use subtraction for hash codes or numeric IDs.
 * <p>
 * Lesson:
 * - Subtraction is not a shortcut for comparison; it is a bug waiting for
 * sufficiently large data.
 * - Testing on small collections (< 32 elements) can hide critical sorting bugs.
 * - The 2021 Android 911 emergency call bug was caused by this exact
 * issue (account1.hashCode() - account2.hashCode()).
 * <p>
 * Output:
 * - x.compareTo(y) > 0: true
 * - y.compareTo(z) > 0: true
 * - x.compareTo(z) > 0: false (Transitivity Failure!)
 */
public class ComparisonSubtractionMistake {

    static class IntObj implements Comparable<IntObj> {
        final int i;
        IntObj(int i) { this.i = i; }

        @Override
        public int compareTo(IntObj o) {
            // BUG: Potential overflow
            return this.i - o.i;
        }
    }

    // Record using subtraction in its comparison logic
    record Point(int x, int y) implements Comparable<Point> {
        @Override
        public int compareTo(Point o) {
            if (this.x != o.x) {
                return this.x - o.x; // BUG
            }
            return this.y - o.y; // BUG
        }
    }

    public static void main(String[] args) {
        // Example 1: Transitivity Failure
        IntObj x = new IntObj(2_000_000_000);
        IntObj y = new IntObj(0);
        IntObj z = new IntObj(-2_000_000_000);

        System.out.println("--- Transitivity Check ---");
        System.out.println("x > y: " + (x.compareTo(y) > 0)); // true
        System.out.println("y > z: " + (y.compareTo(z) > 0)); // true
        System.out.println("x > z: " + (x.compareTo(z) > 0)); // false (Overflow!)

        // Example 2: TimSort Exception (Requires size >= 32 and specific seed >= 209)
        System.out.println("\n--- TimSort Invariant Check ---");
        try {
            Random random = new Random(209);
            IntObj[] objects = new IntObj[32];
            Arrays.setAll(objects, i -> new IntObj(random.nextInt()));
            Arrays.sort(objects);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected: " + e.getMessage());
        }

        // Example 3: TreeSet Corruption
        System.out.println("\n--- TreeSet Corruption ---");
        IntObj o1 = new IntObj(-2_000_000_000);
        IntObj o2 = new IntObj(2_000_000_000);
        IntObj o3 = new IntObj(0);

        TreeSet<IntObj> treeSet = new TreeSet<>(Arrays.asList(o1, o2, o3));
        System.out.println("Initial contains o2: " + treeSet.contains(o2)); // true

        treeSet.remove(o1);
        // After removal, the tree structure is interpreted incorrectly
        System.out.println("Contains o2 after removing o1: " + treeSet.contains(o2)); // false


        // Example 4: Record Corruption
        System.out.println("\n--- Record Corruption ---");
        Point[] points = new Point[20];
        Arrays.setAll(points, idx -> {
            // 20 points with the same hashcode
            // Hashcode = f(x) + y = f(x) + (-f(x)) = 0
            int xx = idx * 500_000_000;
            int yy = -new Point(xx, 0).hashCode();
            return new Point(xx, yy);
        });

        Set<Point> set = new HashSet<>(Arrays.asList(points));
        System.out.println("Initial contains points[14]: " + set.contains(points[14])); // true

        set.remove(points[1]);
        // After removal, the set structure is interpreted incorrectly
        System.out.println("Contains points[14] after removing points[1]: "
                + set.contains(points[14])); // false
    }
}