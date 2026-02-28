package puzzlers.valeev.library.mistake1;

import java.util.*;
import java.util.stream.*;

/**
 * Using Null Values in a Stream
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 85
 * <p>
 * Problem:
 * Why do some stream operations handle nulls perfectly while others, like findFirst()
 * or max(), throw a NullPointerException even when the stream itself is valid?
 * <p>
 * Expected behavior:
 * Developers often assume that if a collection can hold nulls, a stream of that
 * collection should be able to return those nulls through any terminal operation.
 * <p>
 * Actual behavior:
 * Many terminal operations return an {@code Optional}. Since {@code Optional}
 * cannot contain a null value, these operations throw a {@code NullPointerException}
 * if the "winning" element of the stream is null.
 * <p>
 * Explanation:
 * - Optional Constraint: Operations like {@code findFirst()}, {@code findAny()},
 * {@code max()}, and {@code min()} are designed to return an empty Optional
 * if the stream is empty. If the stream is NOT empty but the result is null,
 * they attempt to call {@code Optional.of(null)}, which is illegal.
 * - Inconsistency: Interestingly, {@code Collectors.maxBy()} and {@code minBy()}
 * behave differently; they return {@code Optional.empty()} if the result is null
 * instead of crashing.
 * - Collector Null-Safety:
 * - {@code toList()} and {@code toSet()}: Generally allow null elements
 * (depending on the underlying collection implementation).
 * - {@code toMap()}: Allows null keys but **strictly prohibits null values**.
 * This is often because it uses {@code Map.merge()} internally, where a
 * null value signifies entry removal.
 * - {@code groupingBy()}: **Strictly prohibits null keys**. Even though
 * HashMap supports null keys, {@code groupingBy} does not allow the
 * classifier function to return null.
 * <p>
 *
 * <p>
 * Step-by-step (The findFirst Failure):
 * - Create a stream: {@code Stream.of(null, 1, 2)}.
 * - Call {@code findFirst()}.
 * - The stream finds the first element: {@code null}.
 * - The implementation attempts to wrap it: {@code Optional.of(null)}.
 * - Result: {@code NullPointerException}.
 * <p>
 * Fixes:
 * - Sanitize Early: Use {@code .filter(Objects::nonNull)} at the start of your
 * pipeline to avoid downstream headaches.
 * - Documentation Check: Always verify if a collector or terminal operation
 * is "null-friendly" before passing potential nulls.
 * - JShell Testing: Use {@code Stream.of((Object)null).findFirst()} in a
 * REPL to quickly verify behavior.
 * <p>
 * Lesson:
 * - Nulls are "poison pills" for the Stream API.
 * - An absent Optional means "no element found," not "null element found."
 * <p>
 * Output:
 * - toList with null: [null, 1]
 * - maxBy with null: Optional.empty
 * - max() with null: Throws NullPointerException
 */
public class StreamNullMistake {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(null, 1, 2);
        Comparator<Integer> comparator = Comparator.nullsLast(Comparator.naturalOrder());

        // --- toList and toSet: Nulls are usually fine ---
        try {
            List<Integer> collected = list.stream().collect(Collectors.toList());
            System.out.println("toList result: " + collected);
        } catch (Exception e) {
            System.out.println("toList failed: " + e);
        }

        // --- maxBy vs max(): The inconsistency ---
        // maxBy returns Optional.empty
        Optional<Integer> maxByResult = list.stream()
                .collect(Collectors.maxBy(comparator));
        System.out.println("Collectors.maxBy result: " + maxByResult);

        // max() throws NPE
        try {
            list.stream().max(comparator);
        } catch (NullPointerException e) {
            System.out.println("stream.max() threw NullPointerException as expected");
        }

        // --- toMap: Values cannot be null ---
        try {
            Stream.of(1, 2).collect(Collectors.toMap(k -> k, k -> null));
        } catch (NullPointerException e) {
            System.out.println("toMap threw NPE because value was null");
        }

        // --- groupingBy: Keys cannot be null ---
        try {
            Stream.of(null, 1).collect(Collectors.groupingBy(k -> k));
        } catch (NullPointerException e) {
            System.out.println("groupingBy threw NPE because key (classifier result) was null");
        }
    }
}