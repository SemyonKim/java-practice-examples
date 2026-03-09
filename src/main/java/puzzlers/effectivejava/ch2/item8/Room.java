package puzzlers.effectivejava.ch2.item8;

import java.lang.ref.Cleaner;

/**
 * <h2>Avoid finalizers and cleaners</h2>
 *
 * <p>
 * <b>Core Principle:</b> Avoid using finalizers and cleaners for primary resource
 * management. They are unpredictable, dangerous, and carry a high performance cost.
 * Use them only as a "safety net" or for terminating non-critical native peers.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Safety Net:</b> Acts as a last resort to release resources (like file handles)
 * if a client forgets to call {@code close()}.</li>
 * <li><b>Native Peer Cleanup:</b> Useful for reclaiming non-Java resources that the
 * garbage collector (GC) is unaware of.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>No Promptness:</b> There is no guarantee on when (or if) a finalizer or cleaner
 * will run, making them unsuitable for time-critical tasks.</li>
 * <li><b>Performance:</b> Finalizers can make object creation and destruction up to
 * 50 times slower; cleaners are about 5 times slower when used as a safety net.</li>
 * <li><b>Security Risk:</b> Finalizers open classes to "finalizer attacks," where
 * malicious subclasses exploit partially constructed objects.</li>
 * <li><b>Exception Handling:</b> Uncaught exceptions in finalizers are ignored,
 * potentially leaving objects in a corrupt state without warning.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item9 TryWithResources
 * @see puzzlers.effectivejava.ch4.item24 StaticNestedClasses
 */
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    /**
     * Resource that requires cleaning.
     * MUST NOT refer to the Room instance to avoid preventing GC.
     * Must be a static nested class.
     */
    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // Invoked by close method or cleaner
        @Override
        public void run() {
            System.out.println("Cleaning room...");
            numJunkPiles = 0;
        }
    }

    private final State state;
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        // Register the room and the cleaning action
        cleanable = cleaner.register(this, state);
    }

    @Override
    public void close() {
        // This ensures the cleaning logic runs at most once
        cleanable.clean();
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        documentAdultBehavior();
        documentTeenagerBehavior();
    }

    /**
     * Well-behaved client: uses try-with-resources.
     * Guaranteed to print "Goodbye" then "Cleaning room...".
     */
    private static void documentAdultBehavior() {
        System.out.println("--- Adult Scenario ---");
        try (Room myRoom = new Room(7)) {
            System.out.println("Goodbye");
        }
    }

    /**
     * Ill-behaved client: relies on the cleaner safety net.
     * Unpredictable: might never print "Cleaning room..." before the VM exits.
     */
    private static void documentTeenagerBehavior() {
        System.out.println("\n--- Teenager Scenario ---");
        new Room(99);
        System.out.println("Peace out");
        // On many machines, the program exits before the cleaner thread runs.
    }
}