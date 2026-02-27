package puzzlers.valeev.collections.mistake1;

import java.util.*;

/**
 * Using Null Values in Maps
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - mistake 72
 * <p>
 * Problem:
 * Even in map implementations that support nulls (like HashMap), different methods
 * within the Map interface treat null values inconsistently—some treat null as
 * an "absent" value, while others treat it as a "present" value.
 * <p>
 * Expected behavior:
 * Developers expect consistent behavior where a key mapped to null is always
 * treated as a "present" mapping across all API methods.
 * <p>
 * Actual behavior:
 * - Some methods (containsKey, getOrDefault) recognize the null mapping.
 * - Some methods (compute, computeIfPresent) treat a null mapping as if the key
 * is absent or remove the mapping if a function returns null.
 * - The merge() method throws a NullPointerException if a null value is
 * passed as an argument, despite the map supporting nulls.
 * - The putIfAbsent() and computeIfAbsent() methods differ in how they
 * overwrite or remove existing null mappings.
 * <p>
 * Explanation:
 * - Ambiguity: In many Map methods, a null return value is ambiguous; it
 * could mean the key is missing or the key is mapped to null.
 * - Implementation Bugs: This inconsistency is so subtle that it even led
 * to a year-long bug in the OpenJDK TreeMap implementation (Java 15/16).
 * <p>
 * Step-by-step (The Mechanics):
 * - Case 1: getOrDefault("x", "default")
 * - If "x" is mapped to null, it returns null, not the default.
 * - Case 2: merge("x", null, ...)
 * - Throws NullPointerException immediately, even if the map allows nulls.
 * - Case 3: computeIfAbsent vs compute
 * - computeIfAbsent("x", k -> null) keeps an existing null mapping.
 * - compute("x", (k, v) -> null) removes an existing null mapping.
 * <p>
 * Fixes:
 * - Avoid Nulls: The best approach is to avoid null values in maps entirely.
 * - Use Optional: Map<String, Optional<String>> is a type-safe way to represent
 * the possible absence of a value.
 * - Stick to Simple Methods: If nulls are required (legacy code), prefer
 * containsKey(), getOrDefault(), and put() for predictable results.
 * - Strict Implementations: Consider using map implementations that
 * explicitly disallow null keys and values (like ConcurrentHashMap).
 * <p>
 * Lesson:
 * - Null in maps is a "leaky abstraction" that behaves differently depending
 * on which method you call.
 * - Relying on null values makes code fragile and prone to subtle logic
 * errors that are hard to catch during code review.
 * <p>
 * Output:
 * - map.getOrDefault("x"): null
 * - map.merge("x", "1"): {x=1}
 * - map.computeIfAbsent: {x=null}
 * - map.compute: {}
 */
public class NullInMapsMistake {

    public static void main(String[] args) {
        System.out.println("--- Inconsistent Null Handling in Maps ---");

        // 1. Basic Support (containsKey vs getOrDefault)
        Map<String, String> map = new HashMap<>();
        map.put("x", null);
        System.out.println("containsKey('x'): " + map.containsKey("x"));
        System.out.println("getOrDefault('x', 'default'): " + map.getOrDefault("x", "default"));

        // 2. The merge() trap
        try {
            System.out.println("\n--- merge() Behavior ---");
            map.clear();
            map.put("x", null);
            map.merge("x", "1", (oldV, newV) -> null); // null mapping replaced with "1"
            System.out.println("After first merge: " + map);

            map.merge("x", "1", (oldV, newV) -> null); // function returns null, removes mapping
            System.out.println("After second merge: " + map);

            System.out.print("Attempting merge with null value argument: ");
            map.merge("y", null, (oldV, newV) -> "val"); // Throws NPE
        } catch (NullPointerException e) {
            System.out.println("Caught Expected NullPointerException");
        }

        // 3. putIfAbsent vs computeIfAbsent
        System.out.println("\n--- Absence Method Behavior ---");
        map.clear();
        map.putIfAbsent("x", null);
        System.out.println("After putIfAbsent(null): " + map);
        map.putIfAbsent("x", "1"); // Overwrites the null!
        System.out.println("After putIfAbsent('1') over null: " + map);

        // 4. compute vs computeIfAbsent
        System.out.println("\n--- compute vs computeIfAbsent ---");
        map.clear();
        map.put("x", null);
        map.computeIfAbsent("x", k -> null); // Does nothing, keeps {x=null}
        System.out.println("After computeIfAbsent (returns null): " + map);

        map.compute("x", (k, v) -> null); // Removes the mapping!
        System.out.println("After compute (returns null): " + map);
    }
}