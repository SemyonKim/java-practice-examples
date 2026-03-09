package puzzlers.effectivejava.ch10.item73;

import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * <h2>Throw exceptions appropriate to the abstraction</h2>
 *
 * <p>
 * <b>Core Principle:</b> Higher layers should catch lower-level exceptions and, in their place,
 * throw exceptions that can be explained in terms of the higher-level abstraction. This
 * practice, known as <b>exception translation</b>, prevents implementation details from
 * leaking into the API and protects client code from breaking when underlying
 * implementations change.
 * </p>
 *
 * <h3>Advantages of Exception Translation and Chaining</h3>
 * <ul>
 * <li><b>Encapsulation:</b> It prevents the "pollution" of higher-layer APIs with
 * lower-level implementation details (e.g., a UI layer shouldn't see a {@code SQLException}).</li>
 * <li><b>API Stability:</b> If the lower-level implementation changes, the higher-level
 * exceptions remain consistent, avoiding breaking changes for clients.</li>
 * <li><b>Chaining for Debugging:</b> <b>Exception chaining</b> allows the higher-level
 * exception to carry the lower-level cause, integrating stack traces for easier analysis.</li>
 * <li><b>Clarity:</b> The exception thrown is contextually relevant to the task the
 * user actually called.</li>
 * </ul>
 *
 * <h3>Guidelines and Limitations</h3>
 * <ul>
 * <li><b>Avoid Overuse:</b> The best way to deal with lower-level exceptions is to
 * avoid them entirely by checking the validity of parameters before passing them down.</li>
 * <li><b>Silent Workarounds:</b> If prevention is impossible, try to handle the
 * exception silently and log it, insulating the caller from the problem entirely.</li>
 * <li><b>Propagate Appropriately:</b> Only use translation if the lower-level method
 * does not guarantee that its exceptions are already appropriate for the higher level.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item20 Skeletal implementation
 * @see puzzlers.effectivejava.ch8.item49 Validity checks
 * @see puzzlers.effectivejava.ch10.item75 Failure-capture information
 * @see java.lang.Throwable#getCause()
 */
public class AbstractionAppropriateExceptions<E> {

    /**
     * Example of Exception Translation.
     * Taken from the spirit of {@code AbstractSequentialList}.
     *
     * @param index position of the element to return
     * @param i list iterator for the lower-level abstraction
     * @return the element at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    public E get(int index, ListIterator<E> i) {
        try {
            // Lower-level abstraction (ListIterator) might throw NoSuchElementException
            return i.next();
        } catch (NoSuchElementException e) {
            // Exception Translation: convert to a context-aware exception
            throw new IndexOutOfBoundsException("Index: " + index);
        }
    }

    /**
     * Example of Exception Chaining.
     * Used when the lower-level cause is helpful for debugging.
     */
    public void performHighLevelTask() throws HigherLevelException {
        try {
            triggerLowerLevelError();
        } catch (LowerLevelException cause) {
            // Exception Chaining: The cause is passed to the constructor
            throw new HigherLevelException(cause);
        }
    }

    private void triggerLowerLevelError() throws LowerLevelException {
        throw new LowerLevelException("Low-level database connection failed.");
    }

    // --- Implementation Details / Custom Exceptions ---

    /**
     * A higher-level exception that supports chaining.
     */
    public static class HigherLevelException extends Exception {
        // Exception with chaining-aware constructor
        public HigherLevelException(Throwable cause) {
            super(cause);
        }
    }

    public static class LowerLevelException extends Exception {
        public LowerLevelException(String message) {
            super(message);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        AbstractionAppropriateExceptions<String> demo = new AbstractionAppropriateExceptions<>();

        // 1. Demonstrating Exception Chaining
        try {
            demo.performHighLevelTask();
        } catch (HigherLevelException e) {
            System.out.println("Caught: " + e.getClass().getSimpleName());
            System.out.println("Actual Cause: " + e.getCause());
        }

        // 2. Demonstrating Silent Workaround / Logging (Conceptual)
        try {
            // In a real scenario, this might log and return a default value
            // instead of letting the exception propagate to the user.
            System.out.println("\nIdeally, lower-level exceptions are handled silently if possible.");
        } catch (Exception e) {
            // Log.error("Error handled silently", e);
        }
    }
}