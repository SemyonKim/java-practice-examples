package puzzlers.valeev.collections.mistake8;

import java.util.*;
import java.io.*;

/**
 * Violating Iterator Contracts
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 81
 * <p>
 * Problem:
 * Why do custom Iterators often work perfectly in for-each loops but fail
 * spectacularly when used with utilities like {@code Collections.min()}?
 * <p>
 * Expected behavior:
 * An {@code Iterator} should follow a strict contract:
 * - {@code hasNext()} must be idempotent (calling it multiple times shouldn't change the result).
 * - {@code next()} must throw {@code NoSuchElementException} if no elements remain.
 * - {@code next()} should be callable even if {@code hasNext()} wasn't called immediately before.
 * <p>
 * Actual behavior:
 * Many developers implement "lazy" iterators that advance their internal state
 * inside {@code hasNext()}. While for-each loops are "polite" and call
 * {@code hasNext()} exactly once before each {@code next()}, other utilities
 * might call {@code next()} directly or call {@code hasNext()} repeatedly,
 * breaking the fragile state of naive implementations.
 * <p>
 * Explanation:
 * - For-each Loop Logic: It always follows the sequence: {@code hasNext()} -> {@code next()} -> Body.
 * - The "Aggressive" Utility: {@code Collections.min()} calls {@code next()}
 * immediately to get a starting value. If your iterator prepares the value
 * only inside {@code hasNext()}, it returns a default/stale value (like -1).
 * - Idempotency: {@code hasNext()} is a query, not a command. It should report
 * the state of the world, not change it.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics of a Broken Range):
 * - Naive Iterator starts with {@code cur = -1}.
 * - {@code Collections.min()} calls {@code next()} immediately.
 * - {@code next()} simply returns {@code cur}, which is still -1.
 * - The first element is incorrectly reported as -1 instead of 0.
 * <p>
 * Fixes:
 * - Decouple State: Move state advancement (e.g., {@code cur++}) into {@code next()}.
 * - Explicit Checks: Always call {@code hasNext()} inside {@code next()} and
 * throw {@code NoSuchElementException} if it returns false.
 * - Peeking Logic: For complex sources (like {@code BufferedReader}), use a
 * temporary "peek" field and a boolean flag to track if the next value
 * has already been fetched.
 * - Re-use Existing Tools: Instead of manual implementation, use
 * {@code IntStream.range(0, max).iterator()} or {@code reader.lines().iterator()}.
 * <p>
 * Lesson:
 * - An Iterator is a contract, not just a loop helper. If it only works in
 * a for-each loop, it's not a valid Iterator; it's just a loop-shaped accident.
 * - Always test your iterators by calling {@code hasNext()} twice or not at all.
 * <p>
 * Output:
 * - For-each range(10): 0 1 2 ... 9
 * - Collections.min(range(10)): -1 (The bug!)
 * - Collections.min(fixedRange(10)): 0 (The fix!)
 */
public class IteratorContractMistake {

    public static void main(String[] args) {
        // --- Demonstration of the Range Bug ---
        Collection<Integer> brokenRange = range(10);
        System.out.println("Collections.min on broken range: " + Collections.min(brokenRange));

        // --- Demonstration of the Fixed Range ---
        Collection<Integer> fixedRange = fixedRange(10);
        System.out.println("Collections.min on fixed range: " + Collections.min(fixedRange));

        // --- Demonstration of the BufferedReader adapter ---
        String data = "Line 1\nLine 2";
        BufferedReader reader = new BufferedReader(new StringReader(data));
        Iterator<String> it = asIterator(reader);

        // Testing idempotency: calling hasNext twice
        if (it.hasNext() && it.hasNext()) {
            System.out.println("First line: " + it.next());
        }
    }

    // BROKEN: state changes in hasNext()
    static Collection<Integer> range(int max) {
        return new AbstractCollection<>() {
            public int size() { return max; }
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int cur = -1;
                    public boolean hasNext() { return ++cur < max; }
                    public Integer next() { return cur; }
                };
            }
        };
    }

    // FIXED: state changes in next()
    static Collection<Integer> fixedRange(int max) {
        return new AbstractCollection<>() {
            public int size() { return max; }
            public Iterator<Integer> iterator() {
                return new Iterator<>() {
                    int cur = 0;
                    public boolean hasNext() { return cur < max; }
                    public Integer next() {
                        if (!hasNext()) throw new NoSuchElementException();
                        return cur++;
                    }
                };
            }
        };
    }

    // COMPLEX CASE: BufferedReader with look-ahead
    static Iterator<String> asIterator(BufferedReader reader) {
        return new Iterator<>() {
            String nextValue;

            public boolean hasNext() {
                if (nextValue != null) return true;
                try {
                    nextValue = reader.readLine();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
                return nextValue != null;
            }

            public String next() {
                if (!hasNext()) throw new NoSuchElementException();
                String result = nextValue;
                nextValue = null; // Clear the cache
                return result;
            }
        };
    }
}