package puzzlers.effectivejava.ch10.item76;

import java.util.*;

/**
 * <h2>Strive for failure atomicity</h2>
 *
 * <p>
 * <b>Core Principle:</b> A failed method invocation should leave the object in the state
 * it was in prior to the invocation. This property, called <b>failure atomicity</b>,
 * is essential for allowing callers to recover from exceptions (especially checked ones)
 * without the object becoming corrupted or inconsistent.
 * </p>
 *
 * <h3>Ways to Achieve Failure Atomicity</h3>
 * <ul>
 * <li><b>Immutability:</b> Immutable objects (Item 17) are naturally failure-atomic.
 * Since state never changes, a failed operation simply fails to create a new object.</li>
 * <li><b>Validity Pre-checks:</b> Check parameters for validity (Item 49) before
 * modifying the object's state to catch exceptions early.</li>
 * <li><b>Logic Reordering:</b> Order the computation so that steps likely to throw
 * exceptions occur before any state-modifying code.</li>
 * <li><b>Temporary Copies:</b> Perform the operation on a temporary copy of the
 * data and replace the actual data only if the operation succeeds.</li>
 * <li><b>Recovery Code:</b> Intercept failures and roll back the object's state
 * (rarely used, mostly for durable data structures).</li>
 * </ul>
 *
 * <h3>Limitations and Considerations</h3>
 * <ul>
 * <li><b>Concurrency:</b> If multiple threads modify an object without synchronization,
 * a {@code ConcurrentModificationException} likely leaves the object in a broken state.</li>
 * <li><b>Performance Cost:</b> For some complex operations, maintaining failure
 * atomicity might significantly increase memory usage or execution time.</li>
 * <li><b>Errors vs. Exceptions:</b> You don't need to strive for failure atomicity
 * when throwing {@code Error} (like {@code AssertionError}), as these are unrecoverable.</li>
 * <li><b>Documentation:</b> If a method is <i>not</i> failure-atomic, the API
 * documentation must explicitly state what state the object will be left in.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item17 Minimize mutability
 * @see puzzlers.effectivejava.ch8.item49 Check parameters for validity
 * @see puzzlers.effectivejava.ch10.item73 Throw exceptions appropriate to abstraction
 */
public class FailureAtomicCollection<E> {
    private Object[] elements;
    private int size = 0;

    public FailureAtomicCollection(int capacity) {
        elements = new Object[capacity];
    }

    /**
     * Demonstrates Failure Atomicity via Validity Pre-check.
     * If we didn't check size, the index decrement would persist even after
     * an ArrayIndexOutOfBoundsException, corrupting the stack state.
     */
    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null; // Eliminate obsolete reference
        return result;
    }

    /**
     * Demonstrates Failure Atomicity via Temporary Copy.
     * The original list remains untouched if the sort (or a modification) fails.
     */
    public void sortAndReplace(List<E> input, Comparator<? super E> c) {
        // 1. Perform operation on a temporary copy
        List<E> temp = new ArrayList<>(input);
        temp.sort(c);

        // 2. Replace the contents only after success
        this.elements = temp.toArray();
        this.size = temp.size();
    }

    /**
     * Demonstrates Failure Atomicity via Logic Reordering.
     * We perform the potential ClassCastException check during the 'search'
     * before any structural modification to the tree occurs.
     */
    public void addToTreeMap(TreeMap<E, String> map, E key, String value) {
        // TreeMap naturally checks if 'key' is comparable before insertion.
        map.put(key, value);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        FailureAtomicCollection<String> stack = new FailureAtomicCollection<>(10);

        // 1. Recovery after Failure Atomicity (Pre-check)
        try {
            stack.pop();
        } catch (EmptyStackException e) {
            System.out.println("Caught exception. Size is still: " + stack.size);
            // Because of the pre-check, size didn't become -1.
        }

        // 2. Recovery after Temporary Copy
        List<String> data = new ArrayList<>(Arrays.asList("b", "a", "c"));
        try {
            // Imagine a comparator that fails midway
            stack.sortAndReplace(data, (s1, s2) -> {
                if (s1.equals("a")) throw new RuntimeException("Sort failed!");
                return s1.compareTo(s2);
            });
        } catch (RuntimeException e) {
            System.out.println("Sort failed, but internal state remains consistent.");
        }
    }
}