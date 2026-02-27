package puzzlers.valeev.comparing.mistake10;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Returning Random Numbers from a Comparator
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 68
 * <p>
 * Problem:
 * Why does attempting to shuffle a Stream by using a random comparator
 * (Math.random() > 0.5 ? 1 : -1) cause the application to crash with an
 * IllegalArgumentException?
 * <p>
 * Expected behavior:
 * Developers sometimes expect that returning random values from a comparator
 * will result in a perfectly shuffled list without needing to leave the
 * Stream API.
 * <p>
 * Actual behavior:
 * Comparators must be deterministic and follow strict mathematical rules.
 * A random comparator violates symmetry and transitivity. When the input size
 * reaches 32 or more, Java's TimSort algorithm detects these violations and
 * throws an IllegalArgumentException.
 * <p>
 * Explanation:
 * - Contract Violation: The contract requires that if compare(a, b) is 1,
 * then compare(b, a) must be -1. Randomness makes it possible for both to
 * return 1, which is a logical impossibility for a sort algorithm.
 * - TimSort Logic: To optimize performance, TimSort checks if the comparison
 * logic is "sane." On small arrays (< 32), it might skip these checks,
 * giving the illusion that the "shuffle-via-sort" works.
 * - Non-shuffling: Even if it didn't crash, sorting is not shuffling.
 * Sorting algorithms move elements based on comparisons; a random result
 * doesn't guarantee a uniform distribution of positions.
 * <p>
 * Step-by-step (The Mechanics):
 * - A Stream of 40 integers is passed to .sorted((a, b) -> Math.random() > 0.5 ? 1 : -1).
 * - TimSort begins partitioning the data.
 * - It compares element A to element B and gets 1.
 * - Later, it compares B to A and gets 1 again (due to randomness).
 * - TimSort identifies that the comparison is inconsistent and throws
 * IllegalArgumentException to prevent data corruption or infinite loops.
 * <p>
 * Fixes:
 * - Use Collections.shuffle(List<?>): This is the standard, efficient,
 * and correct way to randomize element order.
 * - For Streams: Collect the stream into a List, shuffle the list,
 * then process further if needed.
 * - Avoid (a, b) -> -1: Using a constant -1 to reverse a list is also
 * dangerous and not guaranteed to work across different JVM versions.
 * <p>
 * Lesson:
 * - Sorting and Shuffling are fundamentally different operations.
 * - A Comparator must always be a "pure" function: same inputs must
 * always yield the same result.
 * - Never abuse an API (like .sorted()) to perform a task it wasn't
 * designed for, especially when a dedicated method (shuffle) exists.
 * <p>
 * Output:
 * - Small list (size 10): [3, 0, 9, 2, 5, 8, 1, 7, 4, 6] (Success by luck)
 * - Large list (size 32): May throw an exception in thread "main" java.lang.IllegalArgumentException:
 * Comparison method violates its general contract!
 */
public class RandomComparatorMistake {

    public static void main(String[] args) {
        // Small list - often appears to work
        List<Integer> smallInput = IntStream.range(0, 10).boxed().collect(Collectors.toList());
        try {
            List<Integer> smallShuffled = smallInput.stream()
                    .sorted((a, b) -> Math.random() > 0.5 ? 1 : -1)
                    .collect(Collectors.toList());
            System.out.println("Small list shuffled: " + smallShuffled);
        } catch (IllegalArgumentException e) {
            System.out.println("Small list failed: " + e.getMessage());
        }

        // Large list - will likely crash due to TimSort invariant checks
        System.out.println("\n--- Attempting large list (size 32) ---");
        List<Integer> largeInput = IntStream.range(0, 32).boxed().collect(Collectors.toList());
        try {
            List<Integer> largeShuffled = largeInput.stream()
                    .sorted((a, b) -> Math.random() > 0.5 ? 1 : -1)
                    .collect(Collectors.toList());
            System.out.println("Large list shuffled: " + largeShuffled);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught Expected Error: " + e.getMessage());
        }

        // The CORRECT way
        System.out.println("\n--- The Correct Way ---");
        List<Integer> correctShuffle = IntStream.range(0, 32).boxed().collect(Collectors.toList());
        Collections.shuffle(correctShuffle);
        System.out.println("Correctly shuffled: " + correctShuffle);
    }
}