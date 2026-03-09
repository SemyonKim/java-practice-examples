package puzzlers.effectivejava.ch9.item62;

import java.util.HashMap;
import java.util.Map;

/**
 * <h2>Avoid strings where other types are more appropriate</h2>
 *
 * <p>
 * <b>Core Principle:</b> Strings are intended for text. Using them as substitutes for
 * numbers, booleans, enums, aggregate types, or "capabilities" (keys) leads to fragile,
 * slow, and error-prone code. Always prefer specialized types or custom classes.
 * </p>
 *
 * <h3>Misuse Categories</h3>
 * <ul>
 * <li><b>Value Types:</b> Avoid keeping numeric or boolean data as strings; parse them
 * into {@code int}, {@code double}, or {@code boolean} immediately.</li>
 * <li><b>Enum Types:</b> Use proper {@code enum} types instead of string constants
 * for sets of values.</li>
 * <li><b>Aggregate Types:</b> Don't use "compound strings" (e.g., {@code "Class#ID"})
 * to represent objects. If you need to pack multiple fields together, create a
 * private static member class.</li>
 * <li><b>Capabilities:</b> Don't use strings as keys to grant access to functionality.
 * This creates a shared global namespace where name collisions lead to security
 * vulnerabilities and bugs.</li>
 * </ul>
 *
 * <h3>Advantages of Specific Types</h3>
 * <ul>
 * <li><b>Type Safety:</b> Compiler catches errors that would only appear at runtime
 * with strings.</li>
 * <li><b>Performance:</b> Avoids repeated parsing and the overhead of string manipulation.</li>
 * <li><b>Flexibility:</b> Custom types can implement {@code equals}, {@code hashCode},
 * and {@code toString} appropriately.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Initial Overhead:</b> Writing a small class or enum takes slightly more
 * effort than concatenating a string.</li>
 * <li><b>Serialization:</b> When communicating with external systems, you eventually
 * have to convert to/from strings, but internal logic should stay typed.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch6.item34 Enums
 * @see puzzlers.effectivejava.ch4.item24 NestedClasses
 */
public class StringMisuse {

    /**
     * INAPPROPRIATE: Using a string as an aggregate type (compound key).
     * Chaos ensues if className contains the delimiter '#'.
     */
    public String getCompoundKey(String className, long id) {
        return className + "#" + id;
    }

    /**
     * BETTER: Using a dedicated class for an aggregate type.
     */
    public static class CompoundKey {
        private final String className;
        private final long id;

        public CompoundKey(String className, long id) {
            this.className = className;
            this.id = id;
        }
        // equals and hashCode omitted for brevity but required here
    }

    /**
     * BROKEN: String-based "Capability" API.
     * Vulnerable to name collisions and malicious access.
     */
    static class BrokenThreadLocal {
        private static final Map<String, Object> storage = new HashMap<>();
        public static void set(String key, Object value) { storage.put(key, value); }
        public static Object get(String key) { return storage.get(key); }
    }

    /**
     * CORRECT: Modern ThreadLocal implementation (Parameterized Capability).
     * Unforgeable, typesafe, and collision-proof.
     */
    public final static class BetterThreadLocal<T> {
        private final Map<Thread, T> threadMap = new HashMap<>();

        public BetterThreadLocal() { }
        public void set(T value) { threadMap.put(Thread.currentThread(), value); }
        public T get() { return threadMap.get(Thread.currentThread()); }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // 1. Problem with Strings as Capabilities: Collision!
        String sharedKey = "user_id";
        BrokenThreadLocal.set(sharedKey, 123);
        // Another part of the system accidentally uses the same string
        BrokenThreadLocal.set(sharedKey, 456);
        System.out.println("Broken value (collision): " + BrokenThreadLocal.get(sharedKey));

        // 2. The Robust Way: Each instance is its own unique capability
        BetterThreadLocal<Integer> userId = new BetterThreadLocal<>();
        BetterThreadLocal<Integer> otherData = new BetterThreadLocal<>();

        userId.set(123);
        otherData.set(456); // No collision possible
        System.out.println("Correct userId: " + userId.get());
        System.out.println("Correct otherData: " + otherData.get());
    }
}