package puzzlers.effectivejava.ch8.item52;

import java.util.Arrays;
import java.util.concurrent.*;

/**
 * <h2>Part 2: Use overloading judiciously</h2>
 * {@link CollectionClassifier Part 1}
 *
 * <p>
 * <b>Core Principle:</b> Overloading resolution is a compile-time process governed by
 * complex JLS rules. To ensure API safety, one must understand how "inexact method
 * references" and "target typing" interact, and recognize when types are "radically
 * different" (unrelated classes or unconvertible pairs).
 * </p>
 *
 * <h3>Advantages of Understanding JLS Rules</h3>
 * <ul>
 * <li><b>Resolution Predictability:</b> Knowing why {@code System.out::println} fails
 * in overloaded contexts prevents "voodoo debugging."</li>
 * <li><b>Type Safety:</b> Utilizing "radically different" types (JLS 5.5) ensures
 * that no single object can satisfy multiple overloadings, removing ambiguity.</li>
 * <li><b>Binary Compatibility:</b> Careful overloading allows APIs to evolve (like
 * adding {@code CharSequence} support) without breaking existing client code.</li>
 * </ul>

 * <h3>Limitations & Pitfalls (The JLS Perspective)</h3>
 * <ul>
 * <li><b>Inexact Method References (JLS 15.13.1):</b> Overloaded methods (like {@code println})
 * create "inexact" references that the compiler cannot resolve until a
 * <b>Target Type</b> is fixed.</li>
 * <li><b>Applicability Tests (JLS 15.12.2):</b> If a method reference is inexact,
 * it is <i>ignored</i> during the initial phases of overload resolution,
 * leading to compilation failures even if one choice seems "obvious."</li>
 * <li><b>Boxed Complexity:</b> Autoboxing bridges the gap between primitives
 * and references, making them no longer "radically different."</li>
 * </ul>

 * @see puzzlers.effectivejava.ch8.item53 Varargs
 * @see puzzlers.effectivejava.ch11.item78 Synchronization
 */
public class OverloadingNuances {

    // --- The "Inexact" Reference Problem ---

    public static void submit(Runnable r) { r.run(); }
    public static void submit(Callable<?> c) throws Exception { c.call(); }

    /**
     * Demonstrates why overloading with different functional interfaces is dangerous.
     * <p>System.out::println is "inexact" (JLS 15.13.1) because 'println' is
     * itself overloaded (println(int), println(String), etc.).</p>
     */
    public static void demonstrateInexactReference() {
        // This compiles because Thread only has a Runnable constructor.
        new Thread(System.out::println).start();

        // This fails to compile:
        //submit(System.out::println);

        // WHY? Per JLS 15.12.2, inexact references are ignored during
        // applicability tests. The compiler sees two valid candidates
        // (Runnable/Callable) but can't use the 'println' reference to
        // disambiguate them because its own meaning depends on the target type.
    }

    // --- API Evolution & Forwarding ---

    private String content = "Effective Java";

    /**
     * If you must overload for a more specific type, forward to the general one.
     * This ensures identical behavior regardless of which overloading is chosen.
     */
    public boolean contentEquals(StringBuffer sb) {
        return contentEquals((CharSequence) sb);
    }

    public boolean contentEquals(CharSequence cs) {
        return content.equals(cs.toString());
    }

    // --- Radically Different / Unrelated Types ---

    /**
     * Unrelated Classes: String and Throwable are unrelated.
     * Safe overloading: Neither is a descendant of the other.
     * It is impossible for an object to be both, making this overloading "safe."
     */
    public void handle(String s) { System.out.println("String: " + s); }
    public void handle(Throwable t) { System.out.println("Error: " + t.getMessage()); }

    /**
     * Unconvertible Pairs:
     * Array types and Interface types (except Serializable/Cloneable)
     * cannot be converted in either direction.
     */
    public void process(int[] numbers) {
        System.out.println(Arrays.toString(numbers));
    }
    public void process(Iterable<?> iterable) {
        iterable.forEach(System.out::println);
    }

    // --- Client Usage ---

    public static void main(String[] args) throws Exception {
        OverloadingNuances demo = new OverloadingNuances();

        // 1. Forwarding behavior
        System.out.println("Equal: " + demo.contentEquals(new StringBuffer("Effective Java")));

        // 2. Safe: String and Exception are unrelated.
        demo.handle("Hello");
        demo.handle(new Exception("Fail"));

        // Safe: int[] is not an Iterable, and Iterable is not an int[].
        // They are radically different.
        demo.process(new int[]{1, 2, 3});

        /*
         * Note on Serializable:
         * Arrays DO implement Serializable. Therefore, overloading
         * handle(int[] arr) and handle(Serializable s) is NOT radically
         * different and can cause confusion.
         */

        // 3. The anomaly to avoid: String.valueOf(char[]) vs String.valueOf(Object)
        Object x = new char[]{'a', 'b'};
        System.out.println(String.valueOf(x));          // Prints memory address/type (e.g. "[C@8efb846")
        System.out.println(String.valueOf((char[]) x)); // Prints "ab"
        // This is a prime example of "confusing" overloading in the JDK.
    }
}

/*
Detailed JLS Technical Breakdown

1. Inexact Method References (JLS 15.13.1)
A method reference is exact only if the underlying method is not overloaded and not generic.
System.out::println is inexact because PrintStream has many println overloads. When you pass
an inexact reference to an overloaded method, the compiler faces a "chicken and egg" problem:
it can't pick the method until it knows the target type, but it can't pick the target type until
it resolves the method.

2. Unrelated Classes (JLS 5.5)
The JLS defines "unrelated" classes as those where neither class is a descendant of the other.
Because Java has single inheritance for classes, if Class A and Class B are both direct subclasses
of Object, they are unrelated. An object cannot "be" both, so the compiler will never be confused
about which overload to pick based on the runtime instance.

3. Unconvertible Class Pairs (JLS 5.1.12)
This section defines identity, widening, and narrowing conversions. If two types have no conversion path
between them (e.g., a double[] and a List), they are considered radically different.

The Exception: Object, Serializable, and Cloneable. Since arrays implement the latter two and
extend the first, they are "related" to those specific interfaces, making overloading with them dangerous.

Summary Advice: If the JLS rules for a specific overload are so complex that you need to open
the specification to understand them, your API is too complex. Rename the methods.
*/