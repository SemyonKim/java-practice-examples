package puzzlers.valeev.collections.mistake6;

import java.util.*;

/**
 * Reading the Collection inside Collection.removeIf()
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 79
 * <p>
 * Problem:
 * Is it safe to call {@code collection.contains()} or other query methods on the
 * collection itself from within the {@code removeIf} predicate?
 * <p>
 * Expected behavior:
 * Developers might expect a consistent result where the predicate either always
 * sees the original state of the collection or always sees the state including
 * previous removals.
 * <p>
 * Actual behavior:
 * The result is non-deterministic and depends on the specific collection
 * implementation and the Java version. Some collections see the "original"
 * state, while others see a "mutating" state.
 * <p>
 * Explanation:
 * - ArrayList (Optimization): To avoid O(n^2) performance caused by repeated
 * array shifts, {@code ArrayList} evaluates the predicate for all elements
 * first, stores the results in a bitmask, and then performs a single pass to
 * remove elements. The predicate always sees the original collection.
 * - TreeSet (Default): {@code TreeSet} typically uses the default
 * {@code Collection} implementation, which iterates and calls
 * {@code iterator.remove()} immediately. Here, the predicate sees the
 * collection change in real-time.
 * - ArrayDeque (Java 8 vs 9+): In Java 8, {@code ArrayDeque} used the
 * iterator-based approach (seeing changes). In Java 9, it was optimized
 * to the bulk-removal approach (seeing the original state).
 * <p>
 *
 * <p>
 * Step-by-step (The Logic Failure):
 * - Input:
 * - Predicate: {@code x -> collection.contains(x - 1)}
 * - Case A (ArrayList):
 * - Check 1: {@code contains(0)} -> false
 * - Check 2: {@code contains(1)} -> true
 * - Check 3: {@code contains(2)} -> true
 * - Result: (Both 2 and 3 removed because their predecessors existed initially).
 * - Case B (TreeSet):
 * - Check 1: {@code contains(0)} -> false (1 stays)
 * - Check 2: {@code contains(1)} -> true (2 is removed)
 * - Check 3: {@code contains(2)} -> false (3 stays because 2 was just removed!)
 * <p>
 * Fixes:
 * - Immutable Streams: Use {@code collection.stream().filter(...).toList()} to
 * ensure you are querying a stable, original state.
 * - Separate Collection: Query a copy of the collection if you must modify
 * the original in place.
 * - Avoid Side Effects: The predicate in {@code removeIf} should ideally be
 * stateless and independent of the collection's current size or content.
 * <p>
 * Lesson:
 * - Never query the collection being modified within {@code removeIf}.
 * - Unlike {@code add()}, querying doesn't trigger {@code ConcurrentModificationException},
 * making this a silent and dangerous logic bug.
 * <p>
 * Output (Java 9+):
 * - ArrayList result: [1, 8, 13]
 * - TreeSet result: [1, 3, 8, 10, 13]
 * - ArrayDeque result: [1, 8, 13] (or [1, 3, 8, 10, 13] in Java 8)
 */
public class RemoveIfQueryMistake {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 8, 9, 10, 13, 14);

        // ArrayList: Uses bulk-removal (sees original state)
        Collection<Integer> arrayList = new ArrayList<>(numbers);
        process("ArrayList", arrayList);

        // TreeSet: Uses iterator-based removal (sees mutating state)
        Collection<Integer> treeSet = new TreeSet<>(numbers);
        process("TreeSet", treeSet);

        // ArrayDeque: Behavior changed in Java 9 (now sees original state)
        Collection<Integer> deque = new ArrayDeque<>(numbers);
        process("ArrayDeque", deque);
    }

    static void process(String type, Collection<Integer> c) {
        c.removeIf(x -> c.contains(x - 1));
        System.out.println(type + " result: " + c);
    }
}