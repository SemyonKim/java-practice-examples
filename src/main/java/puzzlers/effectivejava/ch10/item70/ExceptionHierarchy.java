package puzzlers.effectivejava.ch10.item70;

/**
 * <h2>Use checked exceptions for recoverable conditions and runtime exceptions for programming errors</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use <b>checked exceptions</b> for conditions from which the caller
 * can reasonably be expected to recover. Use <b>runtime exceptions</b> to indicate programming
 * errors (precondition violations). <b>Errors</b> are reserved for the JVM; do not implement
 * new {@code Error} subclasses or throw them (except for {@code AssertionError}).
 * </p>
 *
 * <h3>Checked Exceptions</h3>
 * <ul>
 * <li><b>Advantages:</b> Mandates recovery by forcing the caller to handle the exception.
 * Acts as a clear API signal that the condition is a possible and manageable outcome.</li>
 * <li><b>Limitations:</b> Can be verbose for the caller as they must catch or propagate the exception.
 * Should be avoided if the caller cannot do anything to recover (see Item 71).</li>
 * </ul>
 *
 * <h3>Unchecked Throwables (RuntimeExceptions & Errors)</h3>
 * <ul>
 * <li><b>Advantages:</b> Keeps the API clean and uncluttered for conditions where recovery
 * is impossible or when the failure is due to a bug in the client's code.</li>
 * <li><b>Limitations:</b> Does not provide a compile-time requirement for handling,
 * meaning the program will simply terminate the thread if the exception is unhandled.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch3.item12 ToString
 * @see puzzlers.effectivejava.ch10.item71 AvoidUnnecessaryCheckedExceptions
 * @see puzzlers.effectivejava.ch10.item75 FailureCaptureInformation
 * @see puzzlers.effectivejava.ch10.item77 DontIgnoreExceptions
 */
public class ExceptionHierarchy {

    /**
     * A checked exception for a recoverable condition.
     * It includes accessor methods to aid in recovery (Item 75).
     */
    public static class InsufficientFundsException extends Exception {
        private final long shortfall;

        public InsufficientFundsException(long shortfall) {
            super("Insufficient funds: shortfall of " + shortfall);
            this.shortfall = shortfall;
        }

        /**
         * Provides the caller with data needed to recover.
         * Prevents the user from having to parse the detail string (Item 12).
         */
        public long getShortfall() {
            return shortfall;
        }
    }

    private long balance = 100;

    /**
     * Demonstrates the distinction between checked and runtime exceptions.
     *
     * @param amount the amount to spend
     * @throws InsufficientFundsException if the amount exceeds the balance (Recoverable)
     * @throws IllegalArgumentException if the amount is negative (Programming Error)
     */
    public void spend(long amount) throws InsufficientFundsException {
        // 1. Programming Error: Precondition violation -> RuntimeException
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative: " + amount);
        }

        // 2. Recoverable Condition: External factor -> Checked Exception
        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance);
        }

        balance -= amount;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ExceptionHierarchy account = new ExceptionHierarchy();

        try {
            // Attempting an action that might fail due to external state
            account.spend(150);
        } catch (InsufficientFundsException e) {
            // RECOVERY: The API provides methods to help the user fix the situation
            long needed = e.getShortfall();
            System.out.println("Transaction failed. Shortfall: " + needed +
                    ". Please deposit more funds.");
        }

        // This will throw a RuntimeException, indicating a bug in the calling code
        // account.spend(-50);
    }
}