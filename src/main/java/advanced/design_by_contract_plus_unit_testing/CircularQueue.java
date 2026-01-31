package advanced.design_by_contract_plus_unit_testing;

import java.util.*;

/*
 * Read order:
 * 1. [CircularQueueException.java](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueueException.java)
 * 2. [CircularQueue.java](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueue.java)
 * 3. [CircularQueueTest.java](src/test/java/advanced/design_by_contract_plus_unit_testing/CircularQueueTest.java)
 */

/**
 * Demonstration of combining Design by Contract (DbC) principles
 * with Unit Testing in Java.
 * <p>
 * This example implements a small First-In, First-Out (FIFO) queue
 * using a "circular" array. When the end of the array is reached,
 * the queue wraps back around to the beginning.
 * <p>
 * Contractual definitions enforced in this implementation:
 * 1. Precondition (put): Null elements are not allowed to be added.
 * 2. Precondition (put): It is illegal to put elements into a full queue.
 * 3. Precondition (get): It is illegal to get elements from an empty queue.
 * 4. Postcondition (get): Null elements cannot be produced from the array.
 * 5. Invariant: The region containing objects cannot contain nulls.
 * 6. Invariant: The region not containing objects must contain only nulls.
 * <p>
 * The design-by-contract support methods (precondition, postcondition,
 * invariant) clarify the code and enforce correctness. Preconditions
 * are always checked, while postconditions and invariants are used
 * with assertions (so they can be disabled for performance).
 * <p>
 * The dump() helper method returns a String representation of the
 * queue state for debugging purposes.
 */
public class CircularQueue {
    // Internal storage for queue elements
    private Object[] data;

    // 'in' indicates the next available storage space
    private int in = 0;

    // 'out' indicates the next gettable object
    private int out = 0;

    // Flag to indicate if the queue has wrapped around
    private boolean wrapped = false;

    /**
     * Constructor initializes the queue with a fixed size.
     * After construction, the invariant must hold true.
     */
    public CircularQueue(int size) {
        data = new Object[size];
        assert invariant();
    }

    /**
     * @return true if the queue is empty.
     * Empty means 'in' and 'out' coincide and wrapped is false.
     */
    public boolean empty() {
        return !wrapped && in == out;
    }

    /**
     * @return true if the queue is full.
     * Full means 'in' and 'out' coincide and wrapped is true.
     */
    public boolean full() {
        return wrapped && in == out;
    }

    public boolean isWrapped() { return wrapped; }

    /**
     * Put an item into the queue.
     * Preconditions:
     * - Item must not be null.
     * - Queue must not be full.
     * Invariant is checked before and after insertion.
     */
    public void put(Object item) {
        precondition(item != null, "put() null item");
        precondition(!full(), "put() into full CircularQueue");
        assert invariant();

        data[in++] = item;

        // Wrap around if we reach the end of the array
        if(in >= data.length) {
            in = 0;
            wrapped = true;
        }

        assert invariant();
    }

    /**
     * Get an item from the queue.
     * Preconditions:
     * - Queue must not be empty.
     * Postconditions:
     * - Returned item must not be null.
     * Invariant is checked before and after removal.
     */
    public Object get() {
        precondition(!empty(), "get() from empty CircularQueue");
        assert invariant();

        Object returnVal = data[out];
        data[out] = null; // Clear the slot after retrieval
        out++;

        // Wrap around if we reach the end of the array
        if(out >= data.length) {
            out = 0;
            wrapped = false;
        }

        assert postcondition(returnVal != null, "Null item in CircularQueue");
        assert invariant();

        return returnVal;
    }

    // -------------------------------
    // Design-by-contract support methods
    // -------------------------------

    /**
     * Precondition check: always enforced.
     * Throws exception if condition fails.
     */
    private static void precondition(boolean cond, String msg) {
        if(!cond) throw new CircularQueueException(msg);
    }

    /**
     * Postcondition check: used with assert.
     * Returns true if condition holds, otherwise throws exception.
     */
    private static boolean postcondition(boolean cond, String msg) {
        if(!cond) throw new CircularQueueException(msg);
        return true;
    }

    /**
     * Invariant check: ensures internal validity of the queue.
     * - No nulls in the region holding objects.
     * - Only nulls outside the region holding objects.
     * Expensive if done at start and end of every method, but useful
     * for debugging and ensuring correctness after modifications.
     */
    private boolean invariant() {
        // Check that no null values are in the region holding objects
        for(int i = out; i != in; i = (i + 1) % data.length)
            if(data[i] == null)
                throw new CircularQueueException("null in CircularQueue");

        // Check that only null values are outside the region holding objects
        if(full()) return true;
        for(int i = in; i != out; i = (i + 1) % data.length)
            if(data[i] != null)
                throw new CircularQueueException(
                        "non-null outside of CircularQueue range: " + dump());

        return true;
    }

    /**
     * Helper method to return a string representation of the queue state.
     * Useful for debugging instead of printing directly.
     */
    public String dump() {
        return "in = " + in +
                ", out = " + out +
                ", full() = " + full() +
                ", empty() = " + empty() +
                ", CircularQueue = " + Arrays.asList(data);
    }
}