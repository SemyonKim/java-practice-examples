package patterns.caveofprogramming.singleton;

/**
 * The Singleton class ensures that only one instance of the object
 * is created during the lifetime of the application.
 */
public class Singleton {

    /**
     * Eager Initialization:
     * The instance is created at the time of class loading.
     * This is thread-safe because the JVM guarantees that the static
     * initializer is executed before any thread accesses the class.
     */
    private static final Singleton instance = new Singleton();

    /**
     * Private constructor prevents instantiation from other classes.
     * This is the "secret sauce" of the Singleton pattern.
     */
    private Singleton(){}

    /**
     * Global access point to the single instance.
     * @return The unique instance of the Singleton class.
     */
    public static Singleton getInstance() {
        return instance;
    }

    /*
     * ---------------------------------------------------------
     * Lazy Initialization - OLD (Not Thread Safe):
     * ---------------------------------------------------------
     * This approach delays creation until it's actually needed.
     * * PROBLEM: In a multi-threaded environment, two threads could
     * check 'instance == null' at the same time and create
     * TWO different instances, breaking the pattern.
     * * private static Singleton instance;
     *
     * public static Singleton getInstance() {
     * if (instance == null) instance = new Singleton();
     * return instance;
     * }
     */
}

//Best Practices & Recommendations
/**
 * 1. Use an Enum (The Gold Standard)
 * According to Joshua Bloch (Effective Java), a single-element enum is the best way
 * to implement a Singleton. It provides:
 * - Absolute thread safety.
 * - Serialization protection for free.
 * - Reflection protection (Java prevents using reflection to create enum constants).
 */
enum SingletonEnum {
    INSTANCE;

    public void doSomething() { /* ... */ }
}

/**
 * 2. Double-Checked Locking (For Thread-Safe Lazy Loading)
 * If you must use lazy initialization (because the object is heavy and might not be used),
 * use this pattern:
 */
class ThreadSafeSingleton {

    // Note: The volatile keyword is critical here to ensure visibility of changes across threads.
    private static volatile ThreadSafeSingleton instance;

    public static ThreadSafeSingleton getInstance() {
        if (instance == null) { // First check
            synchronized (ThreadSafeSingleton.class) {
                if (instance == null) { // Second check
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
    }
}

/**
 * 3. Bill Pugh Singleton (Static Inner Class)
 * This is a clever way to get lazy loading and thread safety without using synchronized
 */
class BillPughSingleton {
    private BillPughSingleton() {}

    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }
}