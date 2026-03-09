package puzzlers.effectivejava.ch10.item69;

import java.util.Iterator;
import java.util.Optional;

/**
 * <h2>Use exceptions only for exceptional conditions</h2>
 *
 * <p>
 * <b>Core Principle:</b> Exceptions are designed for exceptional circumstances; they should
 * never be used for ordinary control flow. Designing APIs that force clients to use
 * exceptions for normal operation is a violation of this principle.
 * </p>
 *
 * <h3>Advantages of Standard Idioms</h3>
 * <ul>
 * <li><b>Readability:</b> Standard loops and flow control are instantly recognizable to any developer.</li>
 * <li><b>Performance:</b> JVMs optimize standard loops (e.g., bounds-check elimination), whereas
 * placing code in try-catch blocks often inhibits these optimizations.</li>
 * <li><b>Debugging:</b> Using exceptions for control flow can mask unrelated bugs. If a method inside
 * an exception-based loop throws an unexpected exception, the loop may catch it and terminate
 * silently instead of failing fast with a stack trace.</li>
 * </ul>
 *
 * <h3>Limitations of State-Testing Methods</h3>
 * <ul>
 * <li><b>Concurrency:</b> If an object is accessed concurrently without external synchronization,
 * the state might change between the "state-test" (e.g., {@code hasNext()}) and the
 * "state-dependent" call (e.g., {@code next()}). In such cases, use an {@code Optional} or
 * a distinguished return value (like {@code null}).</li>
 * <li><b>Performance Overhead:</b> If a state-testing method duplicates the work of the
 * state-dependent method, a distinguished return value or {@code Optional} may be preferred for efficiency.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch8.item55 Optionals
 * @see puzzlers.effectivejava.ch9.item67 AvoidUnnecessaryExceptions
 * @see puzzlers.effectivejava.ch10.item70 CheckedVsUnchecked
 */
public class ExceptionControlFlow {

    /**
     * Bad Practice: Using exceptions for loop control.
     * It is slow, confusing, and masks bugs.
     */
    public void exceptionBasedLoop(Mountain[] range) {
        try {
            int i = 0;
            while (true) {
                range[i++].climb();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Ignored: misinterpreted as normal loop termination
        }
    }

    /**
     * Best Practice: Standard idiom.
     * Clean, fast, and idiomatic.
     */
    public void standardLoop(Mountain[] range) {
        for (Mountain m : range) {
            m.climb();
        }
    }

    // --- API Design Patterns ---

    /**
     * Pattern 1: State-testing method (hasNext) paired with state-dependent method (next).
     * Mildly preferred for readability.
     */
    public void processWithStateTest(Iterable<String> collection) {
        Iterator<String> i = collection.iterator();
        while (i.hasNext()) {
            String s = i.next();
            System.out.println(s);
        }
    }

    /**
     * Pattern 2: Optional return value.
     * Better for concurrent access or when the test is expensive.
     */
    public void processWithOptional(Sensor sensor) {
        Optional<Double> reading = sensor.getReading();
        reading.ifPresent(val -> System.out.println("Reading: " + val));
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ExceptionControlFlow demo = new ExceptionControlFlow();
        Mountain[] mountains = { new Mountain("Everest"), new Mountain("K2") };

        System.out.println("Running standard loop:");
        demo.standardLoop(mountains);

        // Demonstrating why exception-based control is dangerous
        System.out.println("\nNote: Standard idioms allow bugs to surface immediately, " +
                "whereas exception-based loops might hide them.");
    }
}

class Mountain {
    private final String name;
    public Mountain(String name) { this.name = name; }
    public void climb() { /* Climbing logic */ }
}

interface Sensor {
    /**
     * Instead of throwing an exception if no reading is available,
     * it returns an Optional (Item 55).
     */
    Optional<Double> getReading();
}