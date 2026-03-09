package puzzlers.effectivejava.ch11.item78;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h2>Synchronize access to shared mutable data</h2>
 *
 * <p>
 * <b>Core Principle:</b> Synchronization is required not only for mutual exclusion
 * (preventing inconsistent states) but also for reliable communication between
 * threads (ensuring changes made by one thread are visible to others).
 * </p>
 *
 * <h3>Advantages of Proper Synchronization</h3>
 * <ul>
 * <li><b>Mutual Exclusion:</b> Prevents a thread from observing an object in an inconsistent
 * state while it is being modified by another thread.</li>
 * <li><b>Thread Visibility:</b> Guarantees that a thread entering a synchronized block
 * sees the effects of all previous modifications guarded by the same lock.</li>
 * <li><b>Liveness:</b> Prevents "hoisting" optimizations by the VM that can lead to
 * infinite loops (liveness failures).</li>
 * <li><b>Safety:</b> Ensures that complex operations, such as increments, are performed
 * atomically when using proper locking or atomic variables.</li>
 * </ul>
 *
 * <h3>Limitations and Hazards</h3>
 * <ul>
 * <li><b>Performance Overhead:</b> While often small, synchronization does incur a
 * cost compared to unsynchronized access.</li>
 * <li><b>Complexity:</b> Failing to synchronize both reads and writes leads to
 * intermittent, platform-dependent bugs that are extremely difficult to debug.</li>
 * <li><b>Volatile Limitation:</b> The {@code volatile} modifier provides visibility
 * but does <b>not</b> provide mutual exclusion for compound operations (e.g., {@code ++}).</li>
 * </ul>
 *
 * <h3>Key Takeaways</h3>
 * <ul>
 * <li><b>Synchronization is a Two-Way Street:</b> You must synchronize both the write <b>and</b>
 * the read. Synchronizing only one is insufficient.</li>
 * <li><b>The Volatile Keyword:</b> Use it only when you need the "communication" effect (visibility)
 * and the operation being performed is naturally atomic (like assignment).</li>
 * <li><b>Atomic Utilities:</b> Prefer {@code java.util.concurrent.atomic} for counters and
 * single-variable synchronization; they are designed for high performance and safety.</li>
 * <li><b>Confine Mutability:</b> The best strategy is to avoid sharing mutable data altogether.
 * Use effectively immutable objects or confine data to a single thread.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item17 Immutability
 * @see puzzlers.effectivejava.ch9.item59 Know and Use Libraries
 * @see puzzlers.effectivejava.ch11.item81 Prefer Concurrency Utilities
 */
public class SharedMutableData {

    // --- 1. Proper Synchronization for Communication ---
    private static boolean stopRequested;

    private static synchronized void requestStop() {
        stopRequested = true;
    }

    private static synchronized boolean stopRequested() {
        return stopRequested;
    }

    /**
     * Demonstrates liveness fix using synchronized methods.
     * Both read and write must be synchronized to guarantee visibility.
     */
    public void synchronizedStop() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!stopRequested()) {
                i++;
            }
            System.out.println("Synchronized thread stopped at i = " + i);
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        requestStop();
        backgroundThread.join();
    }

    // --- 2. Using Volatile for Communication ---
    // Volatile is sufficient here because we only need visibility, not mutual exclusion.
    private static volatile boolean volatileStopRequested;

    public void volatileStop() throws InterruptedException {
        Thread backgroundThread = new Thread(() -> {
            int i = 0;
            while (!volatileStopRequested) {
                i++;
            }
            System.out.println("Volatile thread stopped at i = " + i);
        });
        backgroundThread.start();

        TimeUnit.SECONDS.sleep(1);
        volatileStopRequested = true;
        backgroundThread.join();
    }

    // --- 3. Atomic Variables for Safety and Performance ---
    private static final AtomicLong nextSerialNum = new AtomicLong();

    /**
     * Better alternative to synchronized incrementing.
     * Provides both atomicity and visibility without explicit locking.
     */
    public static long generateSerialNumber() {
        return nextSerialNum.getAndIncrement();
    }

    // --- Client Usage ---

    public static void main(String[] args) throws InterruptedException {
        SharedMutableData example = new SharedMutableData();

        System.out.println("Starting Synchronized Test...");
        example.synchronizedStop();

        System.out.println("Starting Volatile Test...");
        example.volatileStop();

        System.out.println("Generated Serial Number: " + generateSerialNumber());
        System.out.println("Generated Serial Number: " + generateSerialNumber());
    }
}