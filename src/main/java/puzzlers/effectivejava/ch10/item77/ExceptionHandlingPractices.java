package puzzlers.effectivejava.ch10.item77;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * <h2>Don’t ignore exceptions</h2>
 *
 * <p>
 * <b>Core Principle:</b> An empty catch block defeats the purpose of exceptions.
 * If an API declares a thrown exception, it is a signal that must be addressed.
 * Ignoring it is like silencing a fire alarm—the program may continue silently
 * in an inconsistent state, only to fail later at a point unrelated to the
 * actual source of the problem.
 * </p>
 *
 * <h3>Advantages of Proper Exception Handling</h3>
 * <ul>
 * <li><b>Reliability:</b> Forces the developer to handle exceptional conditions
 * or provide a fallback.</li>
 * <li><b>Debuggability:</b> Even if an exception is just propagated, it preserves
 * the stack trace, allowing for a "fail-fast" approach that aids troubleshooting.</li>
 * <li><b>System Integrity:</b> Prevents the application from proceeding with
 * corrupted data or invalid assumptions.</li>
 * </ul>
 *
 * <h3>Guidelines and Limitations</h3>
 * <ul>
 * <li><b>The "Ignored" Variable:</b> If it is truly appropriate to ignore an
 * exception, name the exception variable {@code ignored} and include a comment
 * explaining why.</li>
 * <li><b>Logging as a Minimum:</b> At the very least, log the exception so
 * repeated occurrences can be investigated.</li>
 * <li><b>Resource Closing:</b> Occasionally acceptable when closing streams
 * (like {@code FileInputStream}) after data has already been successfully read,
 * as the state of the file is unchanged.</li>
 * <li><b>Non-Critical Fallbacks:</b> Acceptable when a result is "desirable
 * but not required," and a safe default value exists.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch10.item70 Checked vs Unchecked
 * @see java.util.concurrent.Future#get(long, TimeUnit)
 * @see java.io.Closeable
 */
public class ExceptionHandlingPractices {

    /**
     * BAD PRACTICE: Empty catch block hides the error.
     * The program might crash later with a mysterious NullPointerException
     * because 'data' was never initialized.
     */
    public void suspiciousMethod(String path) {
        try {
            FileInputStream fis = new FileInputStream(path);
            // ... process file
        } catch (IOException e) {
            // Empty catch block - Extremely dangerous!
        }
    }

    /**
     * GOOD PRACTICE: Handling by fallback.
     * When the exception is ignored by design, it is documented.
     */
    public int getMapColoring(ExecutorService exec) {
        int numColors = 4; // Default: guaranteed sufficient by Four Color Theorem
        Future<Integer> f = exec.submit(() -> {
            // Complex calculation that might time out
            return 4;
        });

        try {
            numColors = f.get(1L, TimeUnit.SECONDS);
        } catch (TimeoutException | ExecutionException | InterruptedException ignored) {
            // Use default: minimal coloring is desirable, not required.
            // Variable is named 'ignored' to signal intent.
        }
        return numColors;
    }

    /**
     * ACCEPTABLE IGNORANCE: Closing resources.
     * If closing fails, there is often no recovery action to take.
     */
    public void closeResource(FileInputStream fis) {
        try {
            if (fis != null) fis.close();
        } catch (IOException ignored) {
            // No action needed; state of the file is unchanged.
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ExceptionHandlingPractices demo = new ExceptionHandlingPractices();
        ExecutorService exec = Executors.newSingleThreadExecutor();

        // Demonstrating the fallback pattern
        int result = demo.getMapColoring(exec);
        System.out.println("Resulting colors: " + result);

        exec.shutdown();

        System.out.println("\nRemember: An empty catch block is a hidden debt that " +
                "the next developer will have to pay.");
    }
}