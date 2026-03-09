package puzzlers.effectivejava.ch11.item81;

import java.util.concurrent.*;

/**
 * <h2>Prefer concurrency utilities to wait and notify</h2>
 *
 * <p>
 * <b>Core Principle:</b> Since Java 5, high-level concurrency utilities in {@code java.util.concurrent}
 * have made the low-level {@code wait} and {@code notify} methods largely obsolete. Use
 * concurrent collections and synchronizers to handle complex coordination and state-dependent
 * modifications safely and efficiently.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>High Performance:</b> Concurrent collections like {@code ConcurrentHashMap} manage
 * their own internal synchronization, allowing high concurrency without global locking.</li>
 * <li><b>Atomic Composition:</b> Collections provide state-dependent modify operations
 * (e.g., {@code putIfAbsent}, {@code compute}) that combine multiple steps into one atomic action.</li>
 * <li><b>Liveness and Safety:</b> Synchronizers like {@code CountDownLatch} and {@code Semaphore}
 * simplify thread coordination that would be error-prone using {@code wait/notify}.</li>
 * <li><b>Precision Timing:</b> Utilities use {@code System.nanoTime()}, which is more
 * accurate for interval timing than the system-clock-dependent {@code System.currentTimeMillis()}.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Legacy Maintenance:</b> While new code should avoid {@code wait/notify}, you must
 * understand the "wait-loop idiom" to maintain legacy systems safely.</li>
 * <li><b>Composition Complexity:</b> You cannot exclude concurrent activity from a concurrent
 * collection; locking the collection externally is ineffective and counter-productive.</li>
 * </ul>
 *
 * <h3>Understanding the Wait-Loop Idiom</h3>
 * <p>If you are forced to use the legacy {@code wait} method, the text emphasizes
 * three critical rules to avoid safety and liveness failures:</p>
 * <ol>
 * <li><b>Always use a {@code while} loop:</b> Never use an {@code if} statement.
 * The loop protects against <b>spurious wakeups</b> (where a thread wakes without being notified) and
 * ensures the condition is still true after the lock is reacquired.</li>
 * <li><b>Synchronize:</b> {@code wait} must be called inside a synchronized region that
 * locks the object being waited on.</li>
 * <li><b>Prefer {@code notifyAll}:</b> Even if you think only one thread can proceed,
 * {@code notifyAll} is safer. It prevents unrelated threads from "swallowing" a notification that
 * was meant for someone else.</li>
 * </ol>
 *
 * <h3>Why {@code System.nanoTime()}?</h3>
 * <p>For interval timing (like in the {@code time} method above), always use {@code System.nanoTime}.
 * Unlike {@code System.currentTimeMillis}, it is unaffected by adjustments to the system’s
 * real-time clock (like NTP updates) and offers much higher precision.</p>
 *
 * @see puzzlers.effectivejava.ch11.item78 SharedMutableData
 * @see puzzlers.effectivejava.ch11.item79 ExcessiveSynchronization
 * @see puzzlers.effectivejava.ch11.item80 ExecutorsTasksStreams
 */
public class ConcurrencyUtilities {

    // --- 1. Concurrent Collections: Optimized Canonicalizing Map ---
    private static final ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

    /**
     * Concurrent version of String.intern using ConcurrentHashMap.
     * Uses the "get-then-putIfAbsent" idiom for maximum performance.
     */
    public static String intern(String s) {
        String result = map.get(s);
        if (result == null) {
            result = map.putIfAbsent(s, s);
            if (result == null) {
                result = s;
            }
        }
        return result;
    }

    // --- 2. Synchronizers: CountDownLatch for Timing ---

    /**
     * Simple framework for timing concurrent execution using CountDownLatches.
     */
    public static long time(Executor executor, int concurrency, Runnable action)
            throws InterruptedException {

        CountDownLatch ready = new CountDownLatch(concurrency);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done  = new CountDownLatch(concurrency);

        for (int i = 0; i < concurrency; i++) {
            executor.execute(() -> {
                ready.countDown(); // Tell timer we're ready
                try {
                    start.await(); // Wait till "starting gun" fires
                    action.run();
                } catch (InterruptedException _) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown(); // Tell timer we're done
                }
            });
        }

        ready.await(); // Wait for all workers to reach the starting line
        long startNanos = System.nanoTime();
        start.countDown(); // And they're off!
        done.await(); // Wait for all workers to cross the finish line
        return System.nanoTime() - startNanos;
    }

    // --- 3. Legacy wait/notify Idiom (For Maintenance Reference) ---

    /**
     * Standard idiom for using the wait method.
     * Always invoke wait inside a while loop!
     */
    public void legacyWaitExample(Object obj) throws InterruptedException {
        synchronized (obj) {
            // Loop condition prevents spurious wakeups and ensures liveness
            while (!isConditionMet()) {
                obj.wait(); // Releases lock, reacquires on wakeup
            }
            // Perform action appropriate to condition
        }
    }

    private boolean isConditionMet() { return true; }

    // --- Client Usage ---

    public static void main(String[] args) throws InterruptedException {
        // Test Canonicalizing Map
        String s1 = "Effective Java";
        String s2 = intern(s1);
        System.out.println("Interned string matches: " + (s1 == s2));

        // Test timing framework
        Executor executor = Executors.newFixedThreadPool(5);
        long duration = time(executor, 5, () -> {
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        });

        System.out.println("Concurrent execution took: " + duration + " ns");
        ((ExecutorService) executor).shutdown();
    }
}