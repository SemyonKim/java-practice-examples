package patterns.gof.creational.singleton;

/**
 * ============================================================================
 * DESIGN PATTERN: Singleton
 * CATEGORY:       Creational
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Ensure a class only has one instance, and provide a global point of access
 * to it. Solves the problem of coordinating state across an entire system
 * without relying on unsafe global variables.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Certain system components, like a file system manager or a thread pool,
 * must only have one active instance to prevent resource conflicts.
 * Analogy: A country only has one sitting President. The title gives a global
 * point of access to that one specific individual.
 * <p>
 * 3. APPLICABILITY
 * - When there must be exactly one instance of a class.
 * - When the instance must be accessible from a well-known access point.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Singleton: Contains a private constructor, a static private instance,
 * and a static public getInstance() method.
 * <p>
 * 5. COLLABORATIONS
 * Clients interact with the Singleton strictly via the getInstance() method.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - Pros: Strict control over instantiation, reduces global namespace pollution.
 * - Cons: Can become a testing nightmare due to hidden dependencies and shared
 * global state. Hard to implement safely in multithreaded environments.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * Implementing a proper Singleton in Java is notoriously tricky due to
 * Multithreading, Reflection, and Serialization. This demonstration outlines
 * the evolution of Java singletons, culminating in the "Enum Singleton",
 * which is the widely accepted modern best practice.
 * ============================================================================
 */
public class SingletonDemonstration {

    /**
     * MOCKED ENTITIES (Consistent Creational Base)
     * For consistency with the Creational theme, we assume these singletons
     * manage some global state for a Maze game.
     */
    static class MazeState {
        public int totalRoomsGenerated = 0;
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * A simple class with a global variable. Anyone can overwrite it or
     * create a new instance, leading to fragmented state.
     */
    static class NaiveMazeManager {
        public static NaiveMazeManager globalManager = new NaiveMazeManager();
        public MazeState state = new MazeState();

        // Public constructor allows unlimited instantiations!
        public NaiveMazeManager() {}
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTIONS)
     * ========================================================================
     */

    /**
     * Approach A: Classic Lazy Initialization (Thread-Safe / Double-Checked Locking)
     * Useful if the instance is very expensive to create and might not be used.
     */
    static class LazyMazeManager {
        // 'volatile' ensures changes made in one thread are visible to others
        private static volatile LazyMazeManager uniqueInstance;
        public MazeState state;

        // Private constructor prevents instantiation
        private LazyMazeManager() {
            this.state = new MazeState();
            // In reality, guard against reflection attacks here:
            if (uniqueInstance != null) {
                throw new IllegalStateException("Already initialized.");
            }
        }

        public static LazyMazeManager getInstance() {
            if (uniqueInstance == null) { // 1st check (no locking overhead)
                synchronized (LazyMazeManager.class) {
                    if (uniqueInstance == null) { // 2nd check (inside lock)
                        uniqueInstance = new LazyMazeManager();
                    }
                }
            }
            return uniqueInstance;
        }
    }

    /**
     * Approach B: Bill Pugh Singleton (Initialization-on-demand holder idiom)
     * Safe, lazy, and highly performant without requiring 'synchronized'.
     * Relies on the JVM classloader to guarantee thread safety.
     */
    static class BillPughMazeManager {
        public MazeState state;

        private BillPughMazeManager() {
            this.state = new MazeState();
        }

        // Inner class is not loaded until getInstance() is called
        private static class InstanceHolder {
            private static final BillPughMazeManager INSTANCE = new BillPughMazeManager();
        }

        public static BillPughMazeManager getInstance() {
            return InstanceHolder.INSTANCE;
        }
    }

    /**
     * Approach C: Enum Singleton (Modern Java Best Practice)
     * Recommended by Joshua Bloch (Effective Java).
     * Naturally thread-safe, guards against reflection, and provides free
     * serialization machinery to absolutely guarantee only one instance exists.
     */
    enum EnumMazeManager {
        INSTANCE; // The sole instance

        private final MazeState state;

        EnumMazeManager() {
            this.state = new MazeState();
        }

        public void incrementRooms() {
            state.totalRoomsGenerated++;
        }

        public int getRoomCount() {
            return state.totalRoomsGenerated;
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Singleton Naive Approach ---");
        NaiveMazeManager manager1 = new NaiveMazeManager();
        NaiveMazeManager manager2 = new NaiveMazeManager();

        manager1.state.totalRoomsGenerated = 5;
        System.out.println("Manager 1 state: " + manager1.state.totalRoomsGenerated);
        System.out.println("Manager 2 state: " + manager2.state.totalRoomsGenerated);
        System.out.println("Are manager 1 and 2 the exact same instance? " + (manager1 == manager2));

        System.out.println("\n--- Singleton Pattern Approach (Double-Checked Locking) ---");
        LazyMazeManager lazy1 = LazyMazeManager.getInstance();
        LazyMazeManager lazy2 = LazyMazeManager.getInstance();
        lazy1.state.totalRoomsGenerated = 10;

        System.out.println("Lazy 1 state: " + lazy1.state.totalRoomsGenerated);
        System.out.println("Lazy 2 state: " + lazy2.state.totalRoomsGenerated);
        System.out.println("Are lazy 1 and 2 the exact same instance? " + (lazy1 == lazy2));

        System.out.println("\n--- Singleton Pattern Approach (Enum - Best Practice) ---");
        EnumMazeManager enumManager1 = EnumMazeManager.INSTANCE;
        EnumMazeManager enumManager2 = EnumMazeManager.INSTANCE;

        enumManager1.incrementRooms();
        enumManager1.incrementRooms();

        System.out.println("Enum 1 state: " + enumManager1.getRoomCount());
        System.out.println("Enum 2 state: " + enumManager2.getRoomCount());
        System.out.println("Are enum 1 and 2 the exact same instance? " + (enumManager1 == enumManager2));
    }
}