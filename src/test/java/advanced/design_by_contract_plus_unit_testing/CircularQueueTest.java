package advanced.design_by_contract_plus_unit_testing;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/*
 * Read order:
 * 1. [CircularQueueException.java](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueueException.java)
 * 2. [CircularQueue.java](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueue.java)
 * 3. [CircularQueueTest.java](src/test/java/advanced/design_by_contract_plus_unit_testing/CircularQueueTest.java)
 */

/**
 * JUnit 5 test suite for the CircularQueue class.
 * <p>
 * Purpose:
 * - Demonstrates how to combine Design by Contract (DbC) checks
 *   with unit testing to validate correctness.
 * - Each test verifies a different contractual aspect of the queue.
 * <p>
 * Key points:
 * - The initialize() method pre-loads the queue with data so that
 *   each test starts with a partially full queue.
 * - Support methods showFullness() and showEmptiness() provide
 *   assertions and diagnostic output for queue state.
 * - Four test methods cover:
 *   1. Full queue behavior
 *   2. Empty queue behavior
 *   3. Null insertion handling
 *   4. Circularity (wrap-around behavior)
 * <p>
 * By combining DbC with unit testing, we gain both runtime checks
 * and automated regression tests. This provides a migration path:
 * DbC checks can be moved into unit tests rather than disabled,
 * ensuring continued validation of invariants.
 */
public class CircularQueueTest {

    // Test fixture: a queue of size 10
    private CircularQueue queue = new CircularQueue(10);

    // Counter used to generate test data
    private int i = 0;

    /**
     * Pre-load the queue with some data before each test.
     * Ensures the queue is partially full at the start.
     */
    @BeforeEach
    public void initialize() {
        while(i < 5) // Add 5 elements initially
            queue.put(Integer.toString(i++));
    }

    // -------------------------------
    // Support methods
    // -------------------------------

    /**
     * Assert that the queue is full and not empty.
     * Print diagnostic information.
     */
    private void showFullness() {
        assertTrue(queue.full());
        assertFalse(queue.empty());
        System.out.println(queue.dump());
    }

    /**
     * Assert that the queue is empty and not full.
     * Print diagnostic information.
     */
    private void showEmptiness() {
        assertFalse(queue.full());
        assertTrue(queue.empty());
        System.out.println(queue.dump());
    }

    // -------------------------------
    // Test cases
    // -------------------------------

    /**
     * Test filling the queue to capacity.
     * - Verify that attempting to put into a full queue
     *   throws a CircularQueueException.
     * - Confirm the queue reports full state correctly.
     */
    @Test
    public void full() {
        System.out.println("testFull");
        System.out.println(queue.dump());

        // Remove two items to make space
        System.out.println(queue.get());
        System.out.println(queue.get());

        // Fill until full
        while(!queue.full())
            queue.put(Integer.toString(i++));

        // Attempt to put into full queue
        String msg = "";
        try {
            queue.put("");
        } catch(CircularQueueException e) {
            msg = e.getMessage();
            System.out.println(msg);
        }
        assertEquals(msg, "put() into full CircularQueue");

        showFullness();
    }

    /**
     * Test emptying the queue completely.
     * - Verify that attempting to get from an empty queue
     *   throws a CircularQueueException.
     * - Confirm the queue reports empty state correctly.
     */
    @Test
    public void empty() {
        System.out.println("testEmpty");

        // Drain the queue
        while(!queue.empty())
            System.out.println(queue.get());

        // Attempt to get from empty queue
        String msg = "";
        try {
            queue.get();
        } catch(CircularQueueException e) {
            msg = e.getMessage();
            System.out.println(msg);
        }
        assertEquals(msg, "get() from empty CircularQueue");

        showEmptiness();
    }

    /**
     * Test inserting a null element.
     * - Verify that attempting to put null
     *   throws a CircularQueueException.
     */
    @Test
    public void nullPut() {
        System.out.println("testNullPut");

        String msg = "";
        try {
            queue.put(null);
        } catch(CircularQueueException e) {
            msg = e.getMessage();
            System.out.println(msg);
        }
        assertEquals(msg, "put() null item");
    }

    /**
     * Test circularity (wrap-around behavior).
     * - Fill the queue until full and verify wrapped state.
     * - Empty the queue completely and verify empty state.
     * - Repeat to ensure circular behavior is consistent.
     */
    @Test
    public void circularity() {
        System.out.println("testCircularity");

        // Fill until full
        while(!queue.full())
            queue.put(Integer.toString(i++));
        showFullness();
        assertTrue(queue.isWrapped());

        // Empty completely
        while(!queue.empty())
            System.out.println(queue.get());
        showEmptiness();

        // Fill again
        while(!queue.full())
            queue.put(Integer.toString(i++));
        showFullness();

        // Empty again
        while(!queue.empty())
            System.out.println(queue.get());
        showEmptiness();
    }
}