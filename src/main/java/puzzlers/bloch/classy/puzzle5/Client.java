package puzzlers.bloch.classy.puzzle5;

/**
 * The Re-Initialization Reset
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 52
 * <p>
 * Problem:
 * If a static initializer block calls a method that sets the value of
 * a static field, what happens if that field is explicitly initialized
 * later in the class body?
 * <p>
 * Explanation:
 * - When {@code Cache} is first accessed, the JVM starts class initialization.
 * - **Step 1:** The static block runs and calls {@code initializeIfNecessary()}.
 * - **Step 2:** {@code initializeIfNecessary()} checks {@code initialized}
 * (which is currently the default {@code false}). It calculates the sum (4950)
 * and sets {@code initialized = true}.
 * - **Step 3:** The JVM continues down the class. It reaches the line
 * {@code private static boolean initialized = false;}.
 * - **The Crash:** This explicit assignment **overwrites** the {@code true}
 * value set by the method call, resetting it to {@code false}.
 * - When {@code getSum()} is called, it sees {@code initialized} is
 * {@code false} again and potentially runs the logic a second time,
 * or worse, returns an inconsistent state.
 * <p>
 * The "Reset" Result:
 * - You expect the class to be "initialized" after the static block runs.
 * - Instead, the explicit field initialization at the bottom of the class
 * acts as a "reset" button, wiping out the previous state.
 * <p>
 * Step-by-step:
 * 1. {@code Cache} class loading starts.
 * 2. Static block runs -> {@code initializeIfNecessary()} sets
 * {@code sum = 4950} and {@code initialized = true}.
 * 3. JVM reaches {@code private static int sum;} (No change, remains 4950).
 * 4. JVM reaches {@code initialized = false;} -> Value is
 * overwritten from {@code true} back to {@code false}.
 * 5. {@code Client.main} calls {@code getSum()}, which finds
 * {@code initialized} is {@code false}.
 * <p>
 * Lesson:
 * - **Textual Order is King:** In Java, static initializers and field
 * initializers execute in the order they appear in the source code.
 * - **Avoid Early Calls:** Don't call methods that set static fields from
 * static blocks if those fields are also initialized via literal assignments
 * later in the class.
 * - **Placement:** Always place field declarations with literal initializers
 * at the very top of your class.
 * <p>
 * Output:
 * // 9900 (It runs the calculation twice!)
 */
class Cache {
    static {
        initializeIfNecessary();
    }

    private static int sum;

    public static int getSum() {
        initializeIfNecessary();
        return sum;
    }

    private static boolean initialized = false;

    private static void initializeIfNecessary() {
        if (!initialized) {
            for (int i = 1; i < 100; i++) {
                sum += i;
            }
            initialized = true;
        }
    }
}

public class Client {
    public static void main(String[] args) {
        System.out.println(Cache.getSum());
    }
}