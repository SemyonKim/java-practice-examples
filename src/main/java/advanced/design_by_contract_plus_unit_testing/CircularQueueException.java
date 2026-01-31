package advanced.design_by_contract_plus_unit_testing;

/*
 * Read order:
 * 1. [CircularQueueException.java](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueueException.java)
 * 2. [CircularQueue.java](src/main/java/advanced/design_by_contract_plus_unit_testing/CircularQueue.java)
 * 3. [CircularQueueTest.java](src/test/java/advanced/design_by_contract_plus_unit_testing/CircularQueueTest.java)
 */

// Custom exception used to report errors in CircularQueue
public class CircularQueueException extends RuntimeException {
    public CircularQueueException(String why) {
        super(why);
    }
}