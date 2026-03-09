package puzzlers.effectivejava.ch2.item7;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * <h2>Eliminate obsolete object references</h2>
 *
 * <p>
 * <b>Core Principle:</b> Manually null out object references once they become obsolete,
 * especially in classes that manage their own memory. An obsolete reference is one
 * that will never be dereferenced again.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Prevents Memory Leaks:</b> Ensures that objects (and everything they reference) are eligible for garbage collection.</li>
 * <li><b>Failure Atomicity:</b> Nulling out references causes an immediate {@code NullPointerException} if the program
 * attempts to use an obsolete reference, catching bugs early.</li>
 * <li><b>Performance:</b> Reduces GC overhead and memory footprint, preventing disk paging and {@code OutOfMemoryError}.</li>
 * </ul>
 *
 * <h3>Limitations / Precautions</h3>
 * <ul>
 * <li><b>Avoid Over-nulling:</b> Nulling out every variable is unnecessary and clutters code. The best way to manage
 * references is to define variables in the narrowest possible scope.</li>
 * <li><b>Self-Managed Memory:</b> Be especially alert in classes that manage their own storage pool (like arrays).</li>
 * <li><b>Cache Management:</b> Caches are a common source of leaks. Use {@code WeakHashMap} for keys with
 * external life cycles or periodically cleanse the cache (e.g., using {@code LinkedHashMap.removeEldestEntry}).</li>
 * <li><b>Callbacks/Listeners:</b> Registering callbacks without deregistering them causes leaks. Store them as
 * weak references to allow GC.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item29 Generics
 * @see puzzlers.effectivejava.ch9.item57 LocalVariableScope
 */
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    /**
     * Corrected pop implementation.
     * Simply decrementing the size leaves an "obsolete reference" in the array.
     */
    public Object pop() {
        if (size == 0)
            throw new EmptyStackException();

        Object result = elements[--size];

        // ELIMINATE OBSOLETE REFERENCE
        // Without this, the GC wouldn't know the object at elements[size] is free.
        elements[size] = null;

        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

    // --- Client Usage & Cache Examples ---

    public static void main(String[] args) {
        Stack stack = new Stack();
        stack.push("Active Object");

        // When we pop, the internal array reference is nulled out.
        Object popped = stack.pop();
        System.out.println("Popped: " + popped);

        // --- Example: Preventing leaks in Caches ---
        // Use WeakHashMap when the entry should die when the key is no longer referenced externally.
        Map<Key, String> cache = new WeakHashMap<>();
        Key key = new Key();
        cache.put(key, "Temporary Data");

        key = null; // Now the entry in the WeakHashMap is eligible for GC.
    }
}

/** Mock key class for cache example */
class Key {}