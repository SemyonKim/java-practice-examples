package puzzlers.effectivejava.ch11.item82;

import java.util.*;

/**
 * <h2>Document thread safety</h2>
 *
 * <p>
 * <b>Core Principle:</b> Every class must clearly document its thread safety properties using
 * a carefully worded prose description or thread-safety annotations. The {@code synchronized}
 * modifier in a method declaration is an implementation detail and does not reliably indicate
 * thread safety.
 * </p>
 *
 * <h3>Levels of Thread Safety</h3>
 * <ul>
 * <li><b>Immutable:</b> Instances appear constant. No external synchronization is needed (e.g., {@code String}).</li>
 * <li><b>Unconditionally Thread-safe:</b> Instances are mutable, but internal synchronization
 * makes them safe for concurrent use without external locks (e.g., {@code ConcurrentHashMap}).</li>
 * <li><b>Conditionally Thread-safe:</b> Some sequences of operations require external synchronization
 * on a specific lock (e.g., {@code Collections.synchronizedMap} iterators).</li>
 * <li><b>Not Thread-safe:</b> Clients must provide their own external synchronization for
 * concurrent use (e.g., {@code ArrayList}).</li>
 * <li><b>Thread-hostile:</b> Unsafe even with external synchronization, usually due to
 * unsynchronized static data modification.</li>
 * </ul>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Client Clarity:</b> Prevents users from making incorrect assumptions that lead to
 * insufficient or excessive synchronization.</li>
 * <li><b>Security (Private Lock Idiom):</b> Using a private lock object prevents denial-of-service
 * attacks where a client holds the instance lock indefinitely.</li>
 * <li><b>Inheritance Safety:</b> Private locks prevent subclasses and base classes from
 * accidentally interfering with each other's synchronization logic.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Private Lock Restriction:</b> The private lock object idiom can only be used on
 * <b>unconditionally</b> thread-safe classes. Conditionally thread-safe classes must export
 * their lock so clients can perform atomic sequences.</li>
 * <li><b>Documentation Overhead:</b> Requires precise descriptions of which locks to acquire
 * for specific invocation sequences.</li>
 * </ul>
 *
 * <h3>Summary of Key Advice</h3>
 * <ol>
 * <li><b>Ignore the {@code synchronized} keyword:</b> It doesn't appear in Javadoc by default and
 * isn't a reliable indicator of thread safety.</li>
 * <li><b>State the Level:</b> Explicitly tell your users if the class is
 * Immutable, Unconditionally Thread-safe, etc.</li>
 * <li><b>Identify the Lock:</b> For conditionally thread-safe classes, clearly state which object
 * the client needs to lock on (usually the instance itself).</li>
 * <li><b>Use Private Locks:</b> For unconditionally thread-safe classes designed for inheritance or
 * high security, use a {@code private final Object lock} to encapsulate synchronization.</li>
 * </ol>
 *
 * @see puzzlers.effectivejava.ch11.item78 SharedMutableData
 * @see puzzlers.effectivejava.ch11.item79 ExcessiveSynchronization
 * @see puzzlers.effectivejava.ch4.item15 Encapsulation
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch4.item19 Inheritance
 */
public class ThreadSafetyDoc {

    // --- 1. Conditionally Thread-Safe Documentation Example ---

    /**
     * Returns a synchronized map.
     * <p>It is imperative that the user manually synchronize on the returned map
     * when iterating over any of its collection views.</p>
     */
    public <K, V> Map<K, V> getSynchronizedMap(Map<K, V> map) {
        return Collections.synchronizedMap(map);
    }

    // --- 2. Private Lock Object Idiom (Unconditionally Thread-safe) ---

    /**
     * The private lock object idiom thwarts denial-of-service attacks.
     * The lock field is final to prevent catastrophic unsynchronized access.
     */
    private final Object lock = new Object();

    public void performSafeAction() {
        synchronized (lock) {
            // Internal synchronization logic hidden from clients
            System.out.println("Action performed securely using private lock.");
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ThreadSafetyDoc example = new ThreadSafetyDoc();

        // Usage of Conditionally Thread-safe Object
        Map<String, String> m = example.getSynchronizedMap(new HashMap<>());
        m.put("key", "value");

        Set<String> s = m.keySet();
        // Must synchronize on 'm', not 's'!
        synchronized (m) {
            for (String key : s) {
                System.out.println("Iterating: " + key);
            }
        }

        // Usage of Private Lock Idiom
        example.performSafeAction();
    }
}