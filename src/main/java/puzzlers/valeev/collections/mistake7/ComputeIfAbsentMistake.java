package puzzlers.valeev.collections.mistake7;

import java.util.*;

/**
 * Concurrent Modification in Map.computeIfAbsent()
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 80
 * <p>
 * Problem:
 * Why does calling {@code computeIfAbsent()} throw a {@code ConcurrentModificationException}
 * when the mapping function itself adds a different entry to the same map?
 * <p>
 * Expected behavior:
 * Developers expect {@code computeIfAbsent()} to be a thread-safe or at least a
 * stable "search-and-insert" operation that allows the mapping function to
 * perform further logic, including caching sub-results in the same map.
 * <p>
 * Actual behavior:
 * If the mapping function modifies the map (e.g., by calling {@code computeIfAbsent}
 * or {@code put} for a different key), the map's internal structure may change.
 * Modern Java versions (HashMap since Java 9, TreeMap since Java 15) detect
 * this and throw {@code ConcurrentModificationException}.
 * <p>
 * Explanation:
 * - Optimization Cost: {@code computeIfAbsent()} is faster than {@code get/put}
 * because it finds the insertion bucket only once. It holds a reference to
 * this "spot" while the mapping function runs.
 * - Structural Corruption: If the mapping function adds a new key, it might
 * trigger a rehash or tree restructuring. The "spot" held by the original
 * call is now invalid or points to the wrong location.
 * - Version Differences: In Java 8, this could silently corrupt the HashMap.
 * Java 9+ added explicit checks to fail-fast.
 *
 * <p>
 * Step-by-step (Recursive Caching Failure):
 * - Call {@code cache.computeIfAbsent("a", ...)}: Map finds the bucket for "a".
 * - Mapping Function starts: It needs to calculate "a", which depends on "b".
 * - Call {@code cache.computeIfAbsent("b", ...)}: This is a recursive call.
 * - Structural Change: Adding "b" increments the map's {@code modCount} and
 * might trigger a resize.
 * - Return to "a": The original call detects that {@code modCount} has
 * changed while it was "in progress" and throws the exception.
 * <p>
 * Ways to Avoid This Mistake:
 * - Fallback to get/put: The "classic" approach is safer for recursive
 * dependencies. {@code get()} and {@code put()} are independent operations;
 * once {@code get()} finishes, the map is not in an "intermediary" state.
 * - Side-effect Free Functions: Ensure your mapping function does not
 * modify the map it belongs to.
 * - Bottom-Up Loading: Pre-populate the cache with dependencies before
 * requesting the top-level item.
 * <p>
 * Lesson:
 * - {@code computeIfAbsent()} is an atomic-like operation on a single bucket.
 * Modifying the map during this window violates the method's contract.
 * - Just because a unit test passes (due to a specific call order) doesn't
 * mean the code is safe; if the cache is empty, the same code may fail.
 * <p>
 * Output:
 * - Top-down (empty cache): Throws ConcurrentModificationException
 * - Bottom-up (pre-cached): Successfully returns [b, c, d]
 */
public class ComputeIfAbsentMistake {
    private final Map<String, String> data = Map.of(
            "a", "$b$ + $c$",
            "b", "1",
            "c", "$d$ * 2",
            "d", "3"
    );

    private final Map<String, Set<String>> refCache = new HashMap<>();

    public static void main(String[] args) {
        ComputeIfAbsentMistake demo = new ComputeIfAbsentMistake();

        // --- Failure Case: Top-Down ---
        try {
            System.out.println("Attempting Top-Down calculation for 'a'...");
            demo.transitiveRefs("a");
        } catch (ConcurrentModificationException e) {
            System.out.println("Caught Expected: ConcurrentModificationException");
        }

        // --- Success Case: Bottom-Up ---
        demo.refCache.clear();
        System.out.println("\nAttempting Bottom-Up calculation...");
        demo.transitiveRefs("d"); // Cache d
        demo.transitiveRefs("c"); // Cache c (depends on d)
        demo.transitiveRefs("b"); // Cache b
        System.out.println("Result for 'a' after pre-caching: " + demo.transitiveRefs("a"));
    }

    private Set<String> transitiveRefs(String variable) {
        // This is the "smart" but dangerous line
        return refCache.computeIfAbsent(variable, this::calc);
    }

    private Set<String> calc(String variable) {
        Set<String> result = new HashSet<>();
        String expression = data.getOrDefault(variable, "");

        for (String ref : immediateRefs(expression)) {
            result.add(ref);
            // Indirectly calls refCache.computeIfAbsent again!
            result.addAll(transitiveRefs(ref));
        }
        return result;
    }

    private Set<String> immediateRefs(String expression) {
        String[] parts = expression.split("\\$");
        Set<String> refs = new HashSet<>();
        for (int i = 1; i < parts.length; i += 2) {
            refs.add(parts[i]);
        }
        return refs;
    }

    /**
     * The Safe Alternative
     */
    private Set<String> safeTransitiveRefs(String variable) {
        Set<String> result = refCache.get(variable);
        if (result == null) {
            result = calc(variable);
            refCache.put(variable, result);
        }
        return result;
    }
}