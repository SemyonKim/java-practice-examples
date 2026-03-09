package puzzlers.effectivejava.ch11.item80;

import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

/**
 * <h2>Prefer executors, tasks, and streams to threads</h2>
 *
 * <p>
 * <b>Core Principle:</b> Decouple the unit of work (the <i>task</i>) from the
 * mechanism of execution (the <i>executor service</i>). Avoid working directly
 * with {@code Thread} objects, which serve as both the unit of work and the
 * execution mechanism.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Separation of Concerns:</b> Tasks (Runnable/Callable) define <i>what</i>
 * is done; Executors define <i>how</i> and <i>when</i> it is executed.</li>
 * <li><b>Robustness:</b> Provides built-in support for graceful shutdown,
 * scheduling, and retrieving results (Futures), which are difficult to implement
 * manually without safety or liveness failures.</li>
 * <li><b>Efficiency:</b> Advanced thread pools like {@code ForkJoinPool} use
 * "work-stealing" to ensure all CPU cores remain busy, maximizing throughput.</li>
 * <li><b>Flexibility:</b> Changing execution policy (e.g., from single-threaded
 * to a thread pool) requires minimal code changes.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>CachedThreadPool Hazard:</b> {@code Executors.newCachedThreadPool()}
 * is unsuitable for heavily loaded production servers because it may spawn
 * unlimited threads, leading to resource exhaustion.</li>
 * <li><b>Complexity of Tuning:</b> While powerful, manually writing and tuning
 * {@code ForkJoinTask} is tricky; parallel streams are often a better entry point.</li>
 * <li><b>Lifecycle Responsibility:</b> You <i>must</i> shut down executors
 * properly, or the Virtual Machine may fail to exit.</li>
 * </ul>
 *
 * <h3>Key Takeaways</h3>
 * <ul>
 * <li><b>Task vs. Thread:</b> Think of {@code Thread} as a low-level resource (like a hardware port) and
 * {@code ExecutorService} as the high-level manager.</li>
 * <li><b>The Choice of Pool:</b>
 * <ul>
 * <li><b>Small/Light Load:</b> {@code Executors.newCachedThreadPool()} is convenient and adaptive.</li>
 * <li><b>Heavy Load:</b> {@code Executors.newFixedThreadPool(n)} or a custom
 * {@code ThreadPoolExecutor} provides predictable resource usage.</li>
 * </ul></li>
 * <li><b>Fork-Join & Streams:</b> For compute-intensive tasks, Java 7’s Fork-Join framework
 * (and Java 8’s parallel streams) allows threads to "steal" work from each other, preventing
 * some threads from idling while others are buried.</li>
 * <li><b>Safety First:</b> Remember that the {@code Executor Framework} is just a tool;
 * if the tasks themselves share mutable data, you still need the synchronization principles</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item48 Parallel Streams
 * @see puzzlers.effectivejava.ch11.item78 Synchronization
 * @see puzzlers.effectivejava.ch11.item79 Open Calls
 */
public class TaskExecutionManager {

    /**
     * Demonstrates the modern way to handle asynchronous tasks using
     * an ExecutorService instead of manual Thread management.
     */
    public void runTasks() throws InterruptedException, ExecutionException {
        // 1. Create the execution mechanism (Thread Pool)
        // Use FixedThreadPool for production stability; Cached for small tasks.
        ExecutorService exec = Executors.newFixedThreadPool(4);

        try {
            // 2. Define and submit a 'Runnable' (unit of work with no result)
            exec.execute(() -> System.out.println("Running a simple background task..."));

            // 3. Define and submit a 'Callable' (unit of work that returns a value)
            Future<String> future = exec.submit(() -> {
                TimeUnit.MILLISECONDS.sleep(500);
                return "Task Result";
            });

            // Do other work here...

            // 4. Retrieve result (blocks if not ready)
            System.out.println("Retrieved: " + future.get());

            // 5. Handling a collection of tasks
            List<Callable<Integer>> tasks = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int taskId = i;
                tasks.add(() -> {
                    Thread.sleep(100);
                    return taskId * 10;
                });
            }

            List<Future<Integer>> results = exec.invokeAll(tasks);
            for (Future<Integer> res : results) {
                System.out.println("Bulk result: " + res.get());
            }

        } finally {
            // 6. Mandatory graceful shutdown
            exec.shutdown();
            if (!exec.awaitTermination(5, TimeUnit.SECONDS)) {
                exec.shutdownNow();
            }
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        TaskExecutionManager manager = new TaskExecutionManager();
        try {
            manager.runTasks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}