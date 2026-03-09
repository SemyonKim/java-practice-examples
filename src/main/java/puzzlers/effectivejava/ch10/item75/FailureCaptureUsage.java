package puzzlers.effectivejava.ch10.item75;

/**
 * <h2>Include failure-capture information in detail messages</h2>
 *
 * <p>
 * <b>Core Principle:</b> To facilitate post-mortem analysis, an exception's detail message
 * should capture all parameters and fields that contributed to the failure. This information
 * is often the only lead a programmer has when a failure is not easily reproducible.
 * </p>
 *
 * <h3>Advantages of Failure-Capture Data</h3>
 * <ul>
 * <li><b>Diagnostic Precision:</b> Distinguishes between different error types, such as
 * "fencepost errors" versus "wild values" in index-based operations.</li>
 * <li><b>Programmatic Recovery:</b> Providing accessor methods (especially for checked
 * exceptions) allows calling code to potentially fix the issue and retry.</li>
 * <li><b>Centralized Logic:</b> Using specialized constructors ensures that high-quality
 * detail messages are generated consistently across the API.</li>
 * <li><b>Auditability:</b> Provides SREs and developers with the exact state of the
 * application at the moment of failure.</li>
 * </ul>
 *
 * <h3>Guidelines and Limitations</h3>
 * <ul>
 * <li><b>No Sensitive Data:</b> <b>Never</b> include passwords, encryption keys, or
 * personally identifiable information (PII) in detail messages.</li>
 * <li><b>Data over Prose:</b> Avoid lengthy descriptions; the stack trace and source
 * code provide the context. Focus on the raw values that caused the crash.</li>
 * <li><b>Not for End-Users:</b> Exception messages are for developers/SREs. User-level
 * error messages should be localized and user-friendly, whereas detail messages prioritize content.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item12 Always override toString
 * @see puzzlers.effectivejava.ch10.item70 Checked vs Unchecked exceptions
 * @see puzzlers.effectivejava.ch10.item74 Document all exceptions
 */
public class FailureCaptureUsage {

    /**
     * A custom exception demonstrating the failure-capture idiom.
     * It requires the data in the constructor to ensure a high-quality message.
     */
    public static class DetailedIndexOutOfBoundsException extends IndexOutOfBoundsException {
        private final int lowerBound;
        private final int upperBound;
        private final int index;

        /**
         * Constructs a DetailedIndexOutOfBoundsException.
         *
         * @param lowerBound the lowest legal index value
         * @param upperBound the highest legal index value plus one
         * @param index the actual index value that failed
         */
        public DetailedIndexOutOfBoundsException(int lowerBound, int upperBound, int index) {
            // Generate a detail message that captures the failure automatically
            super(String.format("Lower bound: %d, Upper bound: %d, Index: %d",
                    lowerBound, upperBound, index));

            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.index = index;
        }

        // Accessors for programmatic recovery/analysis
        public int getLowerBound() { return lowerBound; }
        public int getUpperBound() { return upperBound; }
        public int getIndex() { return index; }
    }

    /**
     * Example method that validates bounds and throws the detailed exception.
     *
     * @param index The index to check.
     * @param size The size of the container.
     * @throws DetailedIndexOutOfBoundsException if the index is out of bounds.
     */
    public void checkBounds(int index, int size) {
        if (index < 0 || index >= size) {
            // It is hard for the programmer NOT to capture the failure here
            throw new DetailedIndexOutOfBoundsException(0, size, index);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        FailureCaptureUsage demo = new FailureCaptureUsage();
        int listSize = 10;
        int badIndex = 50;

        try {
            demo.checkBounds(badIndex, listSize);
        } catch (DetailedIndexOutOfBoundsException e) {
            System.err.println("--- Developer Log ---");
            // toString() automatically includes our detailed message
            System.err.println("Exception: " + e);

            System.err.println("\n--- Programmatic Access ---");
            System.err.println("Failed Index: " + e.getIndex());
            System.err.println("Valid Range: [" + e.getLowerBound() + ", " + e.getUpperBound() + ")");

            // Note: We do NOT show this to an end-user; we'd show "Invalid input" instead.
        }
    }
}