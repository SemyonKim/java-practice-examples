package puzzlers.effectivejava.ch4.item21;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * <h2>Design interfaces for posterity</h2>
 *
 * <p>
 * <b>Core Principle:</b> Design interfaces with extreme care and thorough testing before release.
 * While Java 8's default methods allow adding functionality to existing interfaces, they carry
 * significant risks of breaking existing implementations at runtime by violating their invariants.
 * </p>
 *
 * <h3>Advantages (of Default Methods)</h3>
 * <ul>
 * <li><b>Ease of Implementation:</b> Extremely useful for providing standard method implementations
 * when an interface is first created (Item 20).</li>
 * <li><b>Evolutionary Path:</b> Provides a way to add critical new methods to existing interfaces
 * that would otherwise be impossible to change.</li>
 * </ul>
 *
 * <h3>Limitations & Risks</h3>
 * <ul>
 * <li><b>Runtime Failures:</b> Existing implementations may compile without error but fail at
 * runtime because a "hidden" default method was injected without the implementor's consent.</li>
 * <li><b>Invariant Violation:</b> Default methods (like {@code removeIf}) often cannot maintain
 * specific class promises, such as synchronization or atomicity.</li>
 * <li><b>Permanent Flaws:</b> Once an interface is released with a flaw, it is nearly impossible
 * to correct without breaking clients.</li>
 * <li><b>Scope Constraints:</b> Default methods cannot be used to remove methods or change
 * existing method signatures.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item18 WrapperClasses
 * @see puzzlers.effectivejava.ch4.item20 InterfacesVsAbstractClasses
 */
public class PosterityInterfaceDemo {

    /**
     * Mock interface representing the evolution of Collection in Java 8.
     */
    interface MockCollection<E> extends Iterable<E> {
        // Pre-Java 8 method
        int size();

        /**
         * Default method added for "posterity".
         * Risk: This implementation knows nothing about synchronization.
         */
        default boolean removeIf(Predicate<? super E> filter) {
            Objects.requireNonNull(filter);
            boolean removed = false;
            Iterator<E> each = iterator();
            while (each.hasNext()) {
                if (filter.test(each.next())) {
                    each.remove();
                    removed = true;
                }
            }
            return removed;
        }
    }

    /**
     * A wrapper class (Item 18) that synchronizes all calls.
     * This class was written before the "removeIf" default method existed.
     */
    static class SynchronizedCollection<E> implements MockCollection<E> {
        private final MockCollection<E> c;
        private final Object mutex = new Object();

        SynchronizedCollection(MockCollection<E> c) {
            this.c = Objects.requireNonNull(c);
        }

        @Override
        public int size() {
            synchronized (mutex) { return c.size(); }
        }

        @Override
        public Iterator<E> iterator() {
            synchronized (mutex) { return c.iterator(); }
        }

        // removeIf is NOT overridden here.
        // It inherits the default implementation which is NOT synchronized.
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Scenario: A legacy library provides a SynchronizedCollection.
        // A Java 8+ client calls removeIf, expecting thread safety.

        MockCollection<String> myCol = new SynchronizedCollection<>(new SimpleCollection<>());

        System.out.println("Executing removeIf on a synchronized wrapper...");
        try {
            // This call is NOT thread-safe despite being on a "Synchronized" collection,
            // because removeIf uses the default implementation which lacks synchronization.
            myCol.removeIf(s -> s.startsWith("Test"));
            System.out.println("Operation completed (but risked ConcurrentModificationException in multi-threaded env).");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/**
 * Simple mock implementation for demonstration.
 */
class SimpleCollection<E> implements PosterityInterfaceDemo.MockCollection<E> {
    @Override public int size() { return 0; }
    @Override public Iterator<E> iterator() { return java.util.Collections.emptyIterator(); }
}