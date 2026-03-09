package puzzlers.effectivejava.ch8.item55;

import java.util.*;

/**
 * <h2>Return optionals judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use {@code Optional<T>} as a return type for methods that might
 * not be able to return a result and where it is critical that the client confronts this
 * possibility. It serves as a safer, more expressive alternative to returning {@code null}
 * or throwing an exception.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Client Awareness:</b> Forces the API user to handle the case where a value is missing,
 * similar to a checked exception but with less boilerplate.</li>
 * <li><b>Null Safety:</b> Reduces the risk of {@code NullPointerException} by providing a
 * container that explicitly represents "nothing."</li>
 * <li><b>Functional Fluency:</b> Provides powerful methods like {@code map}, {@code filter},
 * and {@code ifPresent} to handle results without explicit null checks.</li>
 * <li><b>Flexibility:</b> Allows the client to choose a default value, a default action,
 * or a specific exception to throw if the value is absent.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Performance Cost:</b> An {@code Optional} is an object that must be allocated and
 * initialized. Avoid in high-frequency, performance-critical loops.</li>
 * <li><b>Boxing Overhead:</b> Using {@code Optional<Integer>} is double-boxing.
 * <i>Note: Use {@code OptionalInt}, {@code OptionalLong}, or {@code OptionalDouble} instead.</i></li>
 * <li><b>Container Wrapping:</b> Never return {@code Optional<List<T>>}; return an empty list instead (Item 54).</li>
 * <li><b>Usage Restrictions:</b> Rarely appropriate as collection keys, values, elements,
 * or instance fields (with few exceptions).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item2 Builders
 * @see puzzlers.effectivejava.ch5.item30 GenericMethods
 * @see puzzlers.effectivejava.ch7.item45 Streams
 * @see puzzlers.effectivejava.ch8.item54 ReturnEmptyCollections
 * @see puzzlers.effectivejava.ch9.item67 Optimization
 * @see puzzlers.effectivejava.ch10.item69 Exceptions
 * @see puzzlers.effectivejava.ch10.item71 CheckedExceptions
 */
public class MaxFinder {

    /**
     * Returns the maximum element of a collection as an Optional.
     * This forces the caller to decide what to do if the collection is empty.
     */
    public static <E extends Comparable<E>> Optional<E> max(Collection<E> c) {
        if (c.isEmpty()) {
            return Optional.empty();
        }

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return Optional.of(result);
    }

    /**
     * Modern stream-based implementation.
     * Stream terminal operations often return Optionals naturally.
     */
    public static <E extends Comparable<E>> Optional<E> maxStream(Collection<E> c) {
        return c.stream().max(Comparator.naturalOrder());
    }

    /**
     * Example of using primitive-specialized Optionals to avoid boxing.
     */
    public static OptionalInt maxInt(int[] array) {
        if (array.length == 0) return OptionalInt.empty();
        int max = array[0];
        for (int i : array) if (i > max) max = i;
        return OptionalInt.of(max);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "pear", "banana");
        List<String> emptyList = Collections.emptyList();

        // 1. Provide a default value
        String result1 = max(words).orElse("No words found");
        System.out.println("Max word: " + result1);

        // 2. Throw a custom exception
        try {
            max(emptyList).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            System.out.println("Caught expected exception for empty list.");
        }

        // 3. Functional transformation (map)
        // Instead of: isPresent() ? get().pid() : "N/A"
        Optional<String> upperMax = max(words).map(String::toUpperCase);
        upperMax.ifPresent(m -> System.out.println("Uppercase Max: " + m));

        // 4. Using primitive variants
        OptionalInt maxVal = maxInt(new int[]{10, 20, 30});
        maxVal.ifPresent(v -> System.out.println("Max Int: " + v));

        // 5. Stream integration (Java 9+)
        List<Optional<String>> optionalList = List.of(Optional.of("A"), Optional.empty(), Optional.of("C"));
        long count = optionalList.stream()
                .flatMap(Optional::stream) // Turns Stream<Optional<T>> into Stream<T>
                .count();
        System.out.println("Non-empty optionals: " + count);
    }
}