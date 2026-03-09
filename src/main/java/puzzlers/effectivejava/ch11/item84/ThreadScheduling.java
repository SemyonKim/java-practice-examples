package puzzlers.effectivejava.ch11.item84;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h2>Don’t depend on the thread scheduler</h2>
 *
 * <p>
 * <b>Core Principle:</b> Any program that relies on the thread scheduler for correctness
 * or performance is likely to be nonportable. The best way to write a robust, responsive,
 * portable program is to ensure that the average number of runnable threads is not
 * significantly greater than the number of processors. Threads should not run if they
 * aren't doing useful work.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Proper Thread Management (Keeping runnable threads low):</b> Leaves the scheduler
 * with little choice, meaning the program’s behavior remains consistent and portable even
 * under radically different OS scheduling policies.</li>
 * <li><b>Executor Framework:</b> Sizing thread pools appropriately and keeping tasks
 * well-sized prevents dispatching overhead from harming performance.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Busy-Waiting:</b> Repeatedly checking a shared object for a state change makes
 * the program vulnerable to scheduler vagaries, severely increases processor load, and
 * prevents other threads from doing useful work.</li>
 * <li><b>{@code Thread.yield()}:</b> Highly nonportable with no testable semantics. Yield
 * invocations that improve performance on one JVM might degrade it on another or do nothing.</li>
 * <li><b>Thread Priorities:</b> Among the least portable features in Java. They are merely
 * hints to the scheduler and should never be used to "fix" a serious liveness problem or
 * a program that barely works.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch11.item80 ExecutorFramework
 */
public class ThreadScheduling {

    // --- 1. Busy-Waiting (What NOT to do) ---

    /**
     * Awful CountDownLatch implementation - busy-waits incessantly!
     * <p>This demonstrates the severe penalty of busy-waiting. It keeps the thread
     * in a runnable state, needlessly consuming CPU cycles and relying entirely on
     * the thread scheduler to eventually preempt it.</p>
     */
    public static class SlowCountDownLatch {
        private int count;

        public SlowCountDownLatch(int count) {
            if (count < 0) {
                throw new IllegalArgumentException(count + " < 0");
            }
            this.count = count;
        }

        public void await() {
            while (true) {
                // BAD PRACTICE: Busy-waiting loop!
                synchronized (this) {
                    if (count == 0) {
                        return;
                    }
                }
            }
        }

        public synchronized void countDown() {
            if (count != 0) {
                count--;
            }
        }
    }

    // --- 2. Proper Thread Management (What TO do) ---

    /**
     * Demonstrates proper thread management using Java's built-in concurrency utilities.
     * Threads are placed in a WAITING state rather than RUNNABLE when not doing useful work.
     */
    public static class GoodTaskWorker {
        private final CountDownLatch latch;

        public GoodTaskWorker(int taskCount) {
            // Uses Java's optimized latch which utilizes LockSupport to safely park threads
            this.latch = new CountDownLatch(taskCount);
        }

        public void performWorkAndSignal() {
            System.out.println(Thread.currentThread().getName() + " is doing useful work...");

            // Simulate useful work
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println(Thread.currentThread().getName() + " finished work. Counting down.");
            latch.countDown();
        }

        public void waitForCompletion() throws InterruptedException {
            // The thread efficiently waits here without consuming CPU cycles
            latch.await();
            System.out.println("All useful work completed efficiently.");
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        System.out.println("--- Anti-Pattern: Busy Waiting ---");
        SlowCountDownLatch badLatch = new SlowCountDownLatch(1);

        Thread badThread = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Bad Thread finally counting down...");
            badLatch.countDown();
        });

        badThread.start();
        System.out.println("Main thread about to intensely busy-wait (consuming CPU)...");
        badLatch.await(); // Main thread spins endlessly here until badThread finishes
        System.out.println("Busy-waiting resolved.\n");


        System.out.println("--- Best Practice: Sensible Thread Management ---");
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available Processors: " + processors);

        // Keeping the number of runnable threads close to the number of processors
        ExecutorService executor = Executors.newFixedThreadPool(processors);
        GoodTaskWorker goodWorker = new GoodTaskWorker(3);

        for (int i = 0; i < 3; i++) {
            executor.submit(goodWorker::performWorkAndSignal);
        }

        try {
            System.out.println("Main thread is safely waiting (0% CPU usage for this thread)...");
            goodWorker.waitForCompletion();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted");
        } finally {
            executor.shutdown();
        }
    }
}