package puzzlers.effectivejava.ch7.item47;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <h2>Prefer Collection to Stream as a return type</h2>
 *
 * <p>
 * <b>Core Principle:</b> When writing a public API that returns a sequence of elements,
 * provide for both users who want to write stream pipelines and those who want to use
 * for-each loops. The {@code Collection} interface is generally the best return type
 * because it extends {@code Iterable} and provides a {@code stream()} method.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Versatility:</b> {@code Collection} supports both the Stream API and the
 * traditional {@code Iterable} for-each loop, making the API more "idiom-neutral."</li>
 * <li><b>Functional Completeness:</b> Provides useful metadata and operations like
 * {@code size()} and {@code contains(Object)}, which {@code Stream} and {@code Iterable} lack.</li>
 * <li><b>Performance:</b> Returning a {@code Collection} avoids the overhead of manual
 * adapters and type-casting required to iterate over a {@code Stream}.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Size Constraint:</b> The {@code size()} method returns an {@code int},
 * limiting the collection to {@code 2^{31} - 1} elements.</li>
 * <li><b>Memory Overhead:</b> Standard implementations (like {@code ArrayList})
 * may be memory-intensive for large sequences; this requires custom implementations.</li>
 * <li><b>Implementation Complexity:</b> For some sequences (like contiguous sublists),
 * implementing a custom {@code Collection} may be significantly more complex than returning a {@code Stream}.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item45 Streams
 */
public class SequenceReturner {

    /**
     * Adapter: Converts Stream to Iterable.
     * Useful when an API unfortunately only returns a Stream.
     */
    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }

    /**
     * Adapter: Converts Iterable to Stream.
     * Useful when an API unfortunately only returns an Iterable.
     */
    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Custom Collection Implementation: Returns the power set of a set.
     * Using AbstractList allows us to represent an exponential sequence (2^n)
     * without storing it all in memory.
     */
    public static final <E> Collection<Set<E>> powerSetOf(Set<E> s) {
        List<E> src = new ArrayList<>(s);
        if (src.size() > 30) {
            throw new IllegalArgumentException("Set too big: " + s);
        }

        return new AbstractList<Set<E>>() {
            @Override public int size() {
                return 1 << src.size(); // 2 to the power of src.size()
            }

            @Override public boolean contains(Object o) {
                return o instanceof Set && src.containsAll((Set<?>) o);
            }

            @Override public Set<E> get(int index) {
                Set<E> result = new HashSet<>();
                for (int i = 0; index != 0; i++, index >>= 1) {
                    if ((index & 1) == 1) {
                        result.add(src.get(i));
                    }
                }
                return result;
            }
        };
    }

    /**
     * Stream-based Implementation: Returns all contiguous sublists.
     * Chosen over Collection because a custom implementation would be too tedious
     * for this specific logic.
     */
    public static <E> Stream<List<E>> subListsOf(List<E> list) {
        return IntStream.range(0, list.size())
                .mapToObj(start ->
                        IntStream.rangeClosed(start + 1, list.size())
                                .mapToObj(end -> list.subList(start, end)))
                .flatMap(x -> x);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Set<String> fruitSet = Set.of("Apple", "Banana", "Cherry");

        // 1. Using Collection for both Iteration and Streams
        Collection<Set<String>> pSet = powerSetOf(fruitSet);

        System.out.println("--- Iterating over PowerSet (Collection) ---");
        for (Set<String> subset : pSet) {
            System.out.println(subset);
        }

        System.out.println("\n--- Streaming over PowerSet (Collection) ---");
        pSet.stream()
                .filter(s -> s.size() == 2)
                .forEach(System.out::println);

        // 2. Using the Adapter for a Stream-only method
        System.out.println("\n--- Iterating over SubLists (Stream) via Adapter ---");
        List<Integer> numbers = List.of(1, 2, 3);
        for (List<Integer> sub : iterableOf(subListsOf(numbers))) {
            System.out.println(sub);
        }
    }
}