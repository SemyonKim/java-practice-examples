package puzzlers.effectivejava.ch11.item79;

import java.util.*;
import java.util.concurrent.*;

/**
 * <h2>Avoid excessive synchronization</h2>
 *
 * <p>
 * <b>Core Principle:</b> To avoid liveness and safety failures, never cede control to
 * the client within a synchronized method or block. Do not invoke "alien" methods
 * (overridable methods or function objects) from within a synchronized region.
 * </p>
 *
 * <h3>Advantages of Minimizing Synchronization</h3>
 * <ul>
 * <li><b>Deadlock Prevention:</b> Avoiding alien methods inside locks prevents
 * circular wait conditions where a background thread tries to acquire a lock
 * held by a thread waiting for that background thread.</li>
 * <li><b>Liveness:</b> Using "open calls" (invoking alien methods outside of
 * synchronization) prevents a single slow client from blocking the entire system.</li>
 * <li><b>Performance:</b> Minimizes contention in multicore systems, allowing
 * better parallelism and VM optimizations.</li>
 * <li><b>Safety:</b> Prevents {@code ConcurrentModificationException} by avoiding
 * reentrant calls that modify a shared resource while it is being iterated.</li>
 * </ul>
 *
 * <h3>Limitations and Design Trade-offs</h3>
 * <ul>
 * <li><b>Snapshot Cost:</b> Moving alien methods outside synchronization often
 * requires taking a snapshot of the data, which adds a small memory and time overhead.</li>
 * <li><b>Internal vs. External:</b> Internal synchronization makes a class easier
 * for clients but can limit performance. Document your choice clearly.</li>
 * </ul>
 *
 * <h3>Critical Warnings</h3>
 * <ul>
 * <li><b>Reentrant Locks:</b> Java's locks are reentrant, which means if a thread holds a lock
 * and encounters another synchronized block guarded by the same lock, it succeeds.
 * While convenient, this can hide bugs where an object is in an inconsistent state,
 * turning a <b>liveness failure</b> (deadlock) into a <b>safety failure</b> (data corruption).</li>
 * <li><b>The Golden Rule:</b> Keep synchronized regions as small as possible. Perform only
 * the logic that absolutely requires exclusivity, then drop the lock before calling into
 * code you don't control (the "alien" methods).</li>
 * <li><b>Performance in the Multicore Era:</b> The cost of synchronization is no longer just
 * CPU cycles — it's <b>contention.</b> Excessive synchronization prevents different cores from
 * working on the data in parallel, effectively turning your multicore processor into a single-core one
 * for that section of the code.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item18 Composition
 * @see puzzlers.effectivejava.ch11.item78 SharedMutableData
 * @see puzzlers.effectivejava.ch11.item81 ConcurrencyUtilities
 * @see puzzlers.effectivejava.ch11.item82 ThreadSafetyDocumentation
 */
public class ObservableSet<E> extends ForwardingSet<E> {

    public ObservableSet(Set<E> set) { super(set); }

    // --- Approach 1: CopyOnWriteArrayList (The modern, preferred way) ---
    // This removes the need for explicit synchronization during iteration.
    private final List<SetObserver<E>> observers = new CopyOnWriteArrayList<>();

    public void addObserver(SetObserver<E> observer) {
        observers.add(observer);
    }

    public boolean removeObserver(SetObserver<E> observer) {
        return observers.remove(observer);
    }

    private void notifyElementAdded(E element) {
        // Open Call: Invoking alien method outside of any synchronized block
        for (SetObserver<E> observer : observers) {
            observer.added(this, element);
        }
    }

    /* // --- Approach 2: Manual Snapshot (If not using CopyOnWriteArrayList) ---
    private void notifyElementAddedManual(E element) {
        List<SetObserver<E>> snapshot = null;
        synchronized(observers) {
            snapshot = new ArrayList<>(observers);
        }
        for (SetObserver<E> observer : snapshot) {
            observer.added(this, element);
        }
    }
    */

    @Override public boolean add(E element) {
        boolean added = super.add(element);
        if (added) {
            notifyElementAdded(element);
        }
        return added;
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        boolean result = false;
        for (E element : c) {
            result |= add(element);
        }
        return result;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ObservableSet<Integer> set = new ObservableSet<>(new HashSet<>());

        // Scenario 1: Basic Observer
        set.addObserver((s, e) -> System.out.println("Added: " + e));

        // Scenario 2: Potential ConcurrentModificationException (Fixed by Open Calls)
        set.addObserver(new SetObserver<>() {
            public void added(ObservableSet<Integer> s, Integer e) {
                System.out.println("Conditional Removal Check: " + e);
                if (e == 23) {
                    // This would fail in a synchronized iteration, but works with Open Calls
                    s.removeObserver(this);
                }
            }
        });

        for (int i = 0; i < 25; i++) {
            set.add(i);
        }
    }
}

/**
 * Functional interface for the Observer pattern.
 */
@FunctionalInterface
interface SetObserver<E> {
    void added(ObservableSet<E> set, E element);
}

/**
 * Simplified Wrapper as per Item 18.
 */
class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s = s; }
    public void clear() { s.clear(); }
    public boolean contains(Object o) { return s.contains(o); }
    public boolean isEmpty() { return s.isEmpty(); }
    public int size() { return s.size(); }
    public Iterator<E> iterator() { return s.iterator(); }
    public boolean add(E e) { return s.add(e); }
    public boolean remove(Object o) { return s.remove(o); }
    public boolean containsAll(Collection<?> c) { return s.containsAll(c); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
    public boolean removeAll(Collection<?> c) { return s.removeAll(c); }
    public boolean retainAll(Collection<?> c) { return s.retainAll(c); }
    public Object[] toArray() { return s.toArray(); }
    public <T> T[] toArray(T[] a) { return s.toArray(a); }
    @Override public boolean equals(Object o) { return s.equals(o); }
    @Override public int hashCode() { return s.hashCode(); }
    @Override public String toString() { return s.toString(); }
}