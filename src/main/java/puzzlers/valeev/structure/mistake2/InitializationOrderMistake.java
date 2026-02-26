package puzzlers.valeev.structure.mistake2;

import java.util.stream.Stream;

/**
 * Incorrect Initialization Order
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 23
 * <p>
 * Problem:
 * How do circular dependencies between class initializers or enum constants lead to
 * uninitialized fields (nulls), NullPointerExceptions, or permanent thread deadlocks?
 * <p>
 * Expected behavior:
 * Developers expect static fields to be fully initialized before they are accessed.
 * In the Enum case, they expect constants to be available during the execution of
 * the constructor or switch expressions.
 * <p>
 * Actual behavior:
 * - Classes: Accessing a field in a partially initialized class can return {@code null}
 * even if the field is {@code static final} and has an initializer.
 * - Threads: Two threads initializing interdependent classes simultaneously can
 * deadlock on the class initialization monitor.
 * - Enums (Pre-Java 21): Using a {@code switch} or {@code values()} inside a constructor
 * can throw a {@code NullPointerException}.
 * <p>
 * Explanation:
 * - Recursive Initialization: If Class A triggers B, and B needs A, the JVM pauses A's
 * initialization to start B's. At this point, A's fields are still at their default
 * values (null/0).
 * - Initialization Locks: The JVM acquires a lock per class during initialization.
 * Circular dependencies across threads lead to a classic "Deadly Embrace" deadlock.
 * - Enum Switch Mechanics (The "values()" Trap):
 * In Java versions prior to 21, using a {@code switch} on 'this' inside an enum
 * constructor is unsafe. To ensure binary compatibility (in case the enum is
 * modified/reordered later without recompiling the caller), the compiler
 * "desugars" the switch into a lookup table.
 * To build this table, the JVM implicitly calls {@code values()} to map
 * constants to ordinals. However, since the enum constants are still being
 * initialized, the internal array returned by {@code values()} is not yet
 * ready, resulting in a {@code NullPointerException}.
 * <p>
 * Step-by-step (The Mechanics - Class A/B example):
 * - 1. A starts initializing.
 * - 2. A creates {@code new B()}, triggering B's initialization.
 * - 3. B assigns {@code A.WELCOME} to {@code MSG}. Since A is currently "initializing",
 * {@code WELCOME} is still {@code null}.
 * - 4. B finishes; A then continues and sets {@code WELCOME = "HELLO"}.
 * - 5. Result: {@code B.MSG} is permanently {@code null}.
 * <p>
 * Fixes:
 * - Simplify static initializers: Move complex logic out of {@code static} blocks.
 * - Lazy Initialization: Use an "Initialization-on-demand holder" or a getter method.
 * - Avoid cross-enum references in constructors.
 * - Upgrade to Java 21+: Java 21 optimizes internal enum switches to avoid the
 * implicit {@code values()} call when the switch is inside the enum's own class.
 * <p>
 * Lesson:
 * - Class initialization is not atomic across interdependent classes.
 * - Circularity in static logic is a "time bomb" for multithreaded deadlocks.
 * - Static final fields are only "final" after the initializer completes.
 */
public class InitializationOrderMistake {

    // --- Part 1: Class Initialization Loop ---
    static class A {
        private static final B unused = new B();
        static final String WELCOME = "Hello".toUpperCase();

        public static void main(String[] args) {
            // Prints "null" if A is launched first, "HELLO" if B is launched first.
            System.out.println("B.MSG: " + B.MSG);
        }
    }

    static class B {
        static final String MSG = A.WELCOME;
    }

    // --- Part 2: Enum Initialization Loop ---
    enum Pet {
        DOG(Voice.BARK);
        final Voice voice;
        Pet(Voice voice) { this.voice = voice; }
    }

    enum Voice {
        BARK(Pet.DOG);
        final Pet owner;
        Voice(Pet owner) { this.owner = owner; }
    }

    // --- Part 2.1: Enum Initialization Loop: switch and this ---
    enum DayOfWeek {
        SUN, MON, TUE, WED, THU, FRI, SAT;
        final boolean weekend;

        DayOfWeek() {
            // Pre-Java 21: This switch triggers an implicit values() call, causing NPE
            // Java 21+: This is safe because it's optimized to avoid values()
            this.weekend = switch (this) {
                case SAT, SUN -> true;
                default -> false;
            };
        }
    }

    // --- Part 3: Deadlock Demonstration ---
    public static void demonstrateDeadlock() {
        Runnable[] actions = {() -> new A(), () -> new B()};
        // This may hang forever
        Stream.of(actions).parallel().forEach(Runnable::run);
        System.out.println("Finished (if no deadlock)");
    }

    public static void main(String[] args) {
        System.out.println("Pet's voice: " + Pet.DOG.voice);       // BARK
        System.out.println("Voice's owner: " + Voice.BARK.owner); // null!
        System.out.println("Is Sunday weekend? " + DayOfWeek.SUN.weekend);

        // A.main(args);
        // demonstrateDeadlock();
    }
}