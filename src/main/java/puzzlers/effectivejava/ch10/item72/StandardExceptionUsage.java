package puzzlers.effectivejava.ch10.item72;

/**
 * <h2>Favor the use of standard exceptions</h2>
 *
 * <p>
 * <b>Core Principle:</b> High-quality APIs strive for code reuse, and exceptions are
 * no exception. Reusing standard Java exceptions makes an API easier to learn,
 * more readable, and reduces the memory footprint of the application.
 * </p>
 *
 * <h3>Advantages of Standard Exception Reuse</h3>
 * <ul>
 * <li><b>Ease of Learning:</b> Matches established conventions that programmers already know.</li>
 * <li><b>Readability:</b> Prevents API clutter from unnecessary custom exception classes.</li>
 * <li><b>Performance:</b> Fewer classes mean a smaller memory footprint and less time spent loading classes.</li>
 * <li><b>Consistency:</b> Ensures that error handling behaves predictably across different libraries.</li>
 * </ul>
 *
 * <h3>Guidelines and Limitations</h3>
 * <ul>
 * <li><b>Documented Semantics:</b> Do not reuse an exception based on its name alone;
 * the cause for throwing it must match the exception’s official documentation.</li>
 * <li><b>Avoid Generic Types:</b> Never throw {@code Exception}, {@code RuntimeException},
 * {@code Throwable}, or {@code Error} directly. They are too broad to catch reliably.</li>
 * <li><b>Serialization:</b> Custom exceptions are serializable, which carries a maintenance
 * cost. Subclass standard exceptions only if you must provide additional data.</li>
 * <li><b>Ambiguity:</b> In cases where an error could be either an illegal state or an illegal
 * argument (e.g., dealing more cards than remain), use {@code IllegalStateException} if
 * *no* argument would have worked, otherwise use {@code IllegalArgumentException}.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch8.item49 Validity checks
 * @see puzzlers.effectivejava.ch10.item75 Failure-capture information
 * @see java.lang.IllegalArgumentException
 * @see java.lang.IllegalStateException
 */
public class StandardExceptionUsage {

    private int cardsInDeck = 52;
    private boolean initialized = false;

    public void init() {
        this.initialized = true;
    }

    /**
     * Demonstrates common standard exceptions.
     *
     * @param handSize Number of cards to deal.
     * @param index The position to peek at.
     * @param name The name of the player (must not be null).
     * @throws IllegalStateException if the deck is not initialized.
     * @throws NullPointerException if name is null.
     * @throws IllegalArgumentException if handSize is negative.
     * @throws IndexOutOfBoundsException if index is out of range.
     */
    public void dealHand(int handSize, int index, String name) {
        // 1. NullPointerException: Standard for prohibited nulls
        if (name == null) {
            throw new NullPointerException("Player name cannot be null");
        }

        // 2. IllegalStateException: State is inappropriate for the call
        if (!initialized) {
            throw new IllegalStateException("Deck must be initialized before dealing");
        }

        // 3. IllegalArgumentException: Specific parameter value is bad
        if (handSize < 0) {
            throw new IllegalArgumentException("Hand size cannot be negative: " + handSize);
        }

        // 4. Tie-breaker rule: IllegalStateException vs IllegalArgumentException
        // If the caller asked for 10 cards but only 5 are left:
        // If NO value (e.g. even 1) would have worked, it's state.
        // If 5 would have worked but 10 didn't, it's an argument issue.
        if (handSize > cardsInDeck) {
            throw new IllegalArgumentException("Requested " + handSize + " but only " + cardsInDeck + " left.");
        }

        // 5. IndexOutOfBoundsException: Specifically for index parameters
        if (index < 0 || index >= cardsInDeck) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + cardsInDeck);
        }

        cardsInDeck -= handSize;
        System.out.println("Dealt " + handSize + " cards to " + name);
    }

    /**
     * Demonstrates UnsupportedOperationException.
     * Commonly used for optional interface methods.
     */
    public void deleteDeck() {
        throw new UnsupportedOperationException("This deck implementation is immutable regarding deletion.");
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        StandardExceptionUsage deck = new StandardExceptionUsage();

        try {
            deck.dealHand(5, 0, "Alice");
        } catch (IllegalStateException e) {
            System.err.println("Caught expected state error: " + e.getMessage());
        }

        deck.init();

        try {
            deck.dealHand(-1, 0, "Bob");
        } catch (IllegalArgumentException e) {
            System.err.println("Caught expected argument error: " + e.getMessage());
        }

        try {
            deck.dealHand(5, 100, "Charlie");
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Caught expected index error: " + e.getMessage());
        }
    }
}