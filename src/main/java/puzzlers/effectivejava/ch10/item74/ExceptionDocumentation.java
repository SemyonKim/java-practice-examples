package puzzlers.effectivejava.ch10.item74;

import java.io.IOException;
import java.nio.file.FileSystemException;

/**
 * <h2>Document all exceptions thrown by each method</h2>
 *
 * <p>
 * <b>Core Principle:</b> A description of the exceptions thrown by a method is a
 * critical part of its documentation. Use the Javadoc {@code @throws} tag to document
 * every exception—both checked and unchecked—that a method can throw. This forms
 * the contract of the method's preconditions and failure modes.
 * </p>
 *
 * <p><i>Note: All methods in this class throw a {@link NullPointerException} if a
 * null object reference is passed to any parameter.</i></p>
 *
 * <h3>Advantages of Thorough Exception Documentation</h3>
 * <ul>
 * <li><b>Complete API Contract:</b> Tells the user exactly how to use the method
 * safely and what errors to expect.</li>
 * <li><b>Defines Preconditions:</b> Documenting unchecked exceptions is the best
 * way to describe the preconditions for successful execution.</li>
 * <li><b>Visual Cues:</b> Using {@code @throws} in Javadoc without the {@code throws}
 * keyword in the method signature visually identifies an exception as unchecked.</li>
 * <li><b>Interface Consistency:</b> Documentation in interfaces allows multiple
 * implementations to share a common behavioral contract.</li>
 * </ul>
 *
 * <h3>Guidelines and Limitations</h3>
 * <ul>
 * <li><b>Declare Checked Exceptions Individually:</b> Never declare that a method
 * throws a broad superclass like {@code Exception} or {@code Throwable}.</li>
 * <li><b>Don't Declare Unchecked Exceptions:</b> While you must document them in
 * Javadoc, do not include them in the method's {@code throws} clause.</li>
 * <li><b>Ideal vs. Real World:</b> It is not always possible to document every
 * inherited unchecked exception, especially when calling third-party libraries.</li>
 * <li><b>Class-Level Docs:</b> If an exception (like {@code NullPointerException})
 * is thrown by every method for the same reason, document it at the class level.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch8.item56 Write doc comments for all exposed API elements
 * @see puzzlers.effectivejava.ch10.item70 Checked exceptions for recoverable conditions
 * @see puzzlers.effectivejava.ch10.item75 Include failure-capture information
 */
public class ExceptionDocumentation {

    /**
     * Demonstrates proper documentation of both checked and unchecked exceptions.
     *
     * @param fileName The name of the file to process (must not be empty).
     * @throws IOException If an I/O error occurs (Checked: declared in signature).
     * @throws IllegalArgumentException If the fileName is empty (Unchecked: not in signature).
     */
    public void processFile(String fileName) throws IOException {
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty.");
        }

        // Simulating a checked exception
        if (fileName.equals("locked.txt")) {
            throw new FileSystemException(fileName, null, "Access denied");
        }

        System.out.println("Processing: " + fileName);
    }

    /**
     * BAD PRACTICE: Using a broad exception superclass.
     * This obscures specific errors and provides no guidance to the caller.
     *
     * @throws Exception because we were too lazy to be specific.
     */
    public void lazyMethod() throws Exception {
        throw new IOException("Specific error lost in a generic wrapper.");
    }

    // --- Client Usage ---

    /**
     * The main method is the only exception where throwing {@code Exception}
     * is acceptable, as it is called only by the VM.
     */
    public static void main(String[] args) throws Exception {
        ExceptionDocumentation doc = new ExceptionDocumentation();

        // 1. Handling documented checked exception
        try {
            doc.processFile("locked.txt");
        } catch (IOException e) {
            System.err.println("Checked Exception handled: " + e.getMessage());
        }

        // 2. Observing precondition failure (documented unchecked exception)
        try {
            doc.processFile("");
        } catch (IllegalArgumentException e) {
            System.err.println("Unchecked Exception (Precondition failure): " + e.getMessage());
        }

        // 3. Class-level documentation example
        try {
            doc.processFile(null);
        } catch (NullPointerException e) {
            System.err.println("NPE caught as per class-level documentation.");
        }
    }
}