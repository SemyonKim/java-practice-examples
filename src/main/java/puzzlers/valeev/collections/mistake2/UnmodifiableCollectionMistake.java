package puzzlers.valeev.collections.mistake2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Trying to Modify an Unmodifiable Collection
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - mistake 73
 * <p>
 * Problem:
 * Java's type system does not distinguish between mutable and immutable
 * collections. Both implement the same interfaces (List, Set, Map), meaning
 * mutation methods like add() or remove() are always visible but may throw
 * UnsupportedOperationException at runtime.
 * <p>
 * Expected behavior:
 * Developers often expect that any object implementing List or Set is
 * fully modifiable unless explicitly stated otherwise by the type or
 * documentation.
 * <p>
 * Actual behavior:
 * - Many JDK factory methods return collections that are unmodifiable
 * (List.of(), Collections.emptyList()) or partially modifiable
 * (Arrays.asList() allows set() but not add()/remove()).
 * - Some methods return different implementation types depending on the
 * input size, leading to "heisenbugs" that only appear under specific
 * data conditions (e.g., empty inputs).
 * <p>
 * Explanation:
 * - Type Erasure of Intent: Because Mutability is not encoded in the type
 * system, the compiler cannot warn you when you call .add() on a
 * List.of() result.
 * - The Optimization Trap: A method might return a mutable ArrayList
 * normally, but return Collections.emptyList() as an "optimization"
 * when there is no data. This creates a code path that crashes only
 * when the result is empty.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case 1: Arrays.asList("a")
 * - .set(0, "b") succeeds (modifies the underlying array).
 * - .add("c") throws UnsupportedOperationException.
 * - Case 2: Inconsistent Returns
 * - sanitize(new String[]{"a"}) returns a mutable ArrayList.
 * - sanitize(new String[]{}) returns Collections.emptyList().
 * - result.add("x") works in development but crashes in production
 * when an empty array is processed.
 * <p>
 * Fixes:
 * - Defensive Copying: Always wrap returned collections in List.copyOf()
 * or use Collectors.toUnmodifiableList() to ensure consistency.
 * - API Design: Assume all incoming and outgoing collections are
 * unmodifiable unless documented otherwise.
 * - Consistency: Avoid returning different mutability types from the
 * same method based on logic branches.
 * - Library Support: Use Guava’s ImmutableList or similar types to
 * capture mutability intent in the type signature.
 * <p>
 * Lesson:
 * - Mutability is a runtime property in Java collections, not a
 * compile-time guarantee.
 * - To prevent fragile code, treat every collection you didn't create
 * locally as unmodifiable.
 * <p>
 * Output:
 * - Processing "a, b": [A, B, Custom...]
 * - Processing "": Exception in thread "main" java.lang.UnsupportedOperationException
 */
public class UnmodifiableCollectionMistake {

    // BUG: Returns mutable list for data, but unmodifiable list for empty input
    static List<String> sanitize(String[] input) {
        if (input.length == 0) {
            return Collections.emptyList(); // Unmodifiable
        }
        List<String> list = new ArrayList<>(); // Modifiable
        for (String s : input) {
            s = s.trim();
            if (!s.isEmpty()) list.add(s);
        }
        return list;
    }

    // FIX: Always return an unmodifiable list regardless of input
    static List<String> sanitizeFixed(String[] input) {
        return Arrays.stream(input)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableList());
    }

    public static void main(String[] args) {
        String[] data = {"a ", " b"};
        String[] empty = {};

        System.out.println("--- Testing with Data ---");
        List<String> strings1 = sanitize(data);
        strings1.add("Custom..."); // Works fine
        System.out.println("Result: " + strings1);

        System.out.println("\n--- Testing with Empty Array ---");
        try {
            List<String> strings2 = sanitize(empty);
            System.out.println("List acquired. Attempting to add element...");
            strings2.add("Custom..."); // Crashes here
        } catch (UnsupportedOperationException e) {
            System.out.println("Caught Exception: This code crashed because sanitize() " +
                    "returned an unmodifiable emptyList.");
        }

        // Demonstrating partially modifiable collections
        System.out.println("\n--- Partially Modifiable (Arrays.asList) ---");
        List<String> backedByArray = Arrays.asList("Fixed", "Size");
        backedByArray.set(0, "Changed"); // Works
        System.out.println("Updated: " + backedByArray);
        try {
            backedByArray.add("New"); // Fails
        } catch (UnsupportedOperationException e) {
            System.out.println("Arrays.asList does not support add().");
        }
    }
}