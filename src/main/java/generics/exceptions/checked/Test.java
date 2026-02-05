package generics.exceptions.checked;

// Example: Defeating Checked Exception Checking in Java
// Inspired by Cay Horstmann, Core Java Volume 1
//
// This example demonstrates how generics, type erasure,
// and @SuppressWarnings can be used to "defeat" Java's
// checked exception mechanism. Normally, Java enforces
// that all checked exceptions must be caught or declared.
// Here, we trick the compiler into believing checked
// exceptions are unchecked.

interface Task {
    // A functional interface similar to Runnable,
    // but its run() method is allowed to throw checked exceptions.
    void run() throws Exception;

    /**
     * Generic method that rethrows any Throwable as type T.
     * <p>
     * - The compiler believes the thrown exception is of type T.
     * - If T is chosen as RuntimeException, the compiler assumes
     *   the exception is unchecked.
     * - In reality, we can pass in any checked exception.
     * <p>
     * '''@SuppressWarnings("unchecked")''' is required because
     *   the cast (T) t is unchecked at runtime.
     */
    @SuppressWarnings("unchecked")
    static <T extends Throwable> void throwAs(Throwable t) throws T {
        throw (T) t; // The cast tricks the compiler
    }

    /**
     * Adapts a Task into a Runnable.
     * <p>
     * Runnable.run() cannot declare checked exceptions,
     * but Task.run() can. We catch any Exception from Task.run()
     * and rethrow it using throwAs(), which convinces the compiler
     * it is unchecked.
     */
    static Runnable asRunnable(Task task) {
        return () -> {
            try {
                task.run(); // May throw checked exceptions
            } catch (Exception e) {
                // Rethrow as "unchecked" using our trick
                Task.<RuntimeException>throwAs(e);
            }
        };
    }
}

public class Test {
    public static void main(String[] args) {
        // Create a thread from a Task that throws checked exceptions.
        var thread = new Thread(Task.asRunnable(() -> {
            // Thread.sleep normally requires handling InterruptedException.
            // Here, we don't need to catch it.
            Thread.sleep(1000);

            System.out.println("Hello, World!");

            // Throw a checked exception directly.
            // Normally forbidden in Runnable.run(), but allowed here.
            throw new Exception("Check this out!");
        }));

        // Start the thread. After 1 second, it prints and then throws.
        thread.start();
    }
}