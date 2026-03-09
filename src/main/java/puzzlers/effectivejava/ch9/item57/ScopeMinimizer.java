package puzzlers.effectivejava.ch9.item57;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <h2>Minimize the scope of local variables</h2>
 *
 * <p>
 * <b>Core Principle:</b> Declare local variables at the point where they are first used and
 * almost always include an initializer. Prefer {@code for} loops (traditional or for-each)
 * over {@code while} loops to automatically restrict the scope of loop variables.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Readability:</b> Reduces mental clutter by keeping variables out of sight until they are relevant.</li>
 * <li><b>Error Prevention:</b> Prevents "copy-and-paste" bugs where a stale variable from a previous block is accidentally reused.</li>
 * <li><b>Maintainability:</b> Makes code easier to refactor; smaller scopes mean fewer side effects to track when moving logic.</li>
 * <li><b>Performance:</b> Loops can cache expensive computations (e.g., list size) within the loop declaration scope.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Try-Catch Constraints:</b> If a variable is initialized by a method that throws a checked exception, it must be declared outside the {@code try} block if it's needed in the {@code finally} or later logic.</li>
 * <li><b>Method Size:</b> If a method is too large and performs multiple tasks, variable scopes naturally expand.
 * <i>Note: The solution is to decompose the method into smaller, focused ones.</i></li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 Accessibility
 * @see puzzlers.effectivejava.ch9.item58 ForEach
 * @see puzzlers.effectivejava.ch9.item67 Optimization
 */
public class ScopeMinimizer {

    /**
     * Preferred idiom for iterating over a collection.
     * The scope of 'e' is limited strictly to the loop body.
     */
    public <E> void iterate(Collection<E> container) {
        for (E e : container) {
            process(e);
        }
    }

    /**
     * Demonstrates how 'for' loops prevent copy-paste errors compared to 'while' loops.
     * In a 'while' loop, the iterator remains in scope, inviting accidental reuse.
     */
    public <E> void preventCopyPasteErrors(List<E> c1, List<E> c2) {
        // Traditional for-loop restricts 'i' to the loop header and body
        for (Iterator<E> i = c1.iterator(); i.hasNext(); ) {
            process(i.next());
        }

        // Reusing 'i' here is a COMPILE-TIME error, which is good!
        // for (Iterator<E> i2 = c2.iterator(); i.hasNext(); ) { ... }

        for (Iterator<E> i2 = c2.iterator(); i2.hasNext(); ) {
            process(i2.next());
        }
    }

    /**
     * Idiom for minimizing scope while optimizing performance.
     * Limits the scope of both the index 'i' and the boundary 'n'.
     */
    public void optimizedLoop() {
        // 'n' is calculated once and limited to the loop scope
        for (int i = 0, n = expensiveComputation(); i < n; i++) {
            System.out.println("Processing index: " + i);
        }
    }

    private int expensiveComputation() {
        return 10;
    }

    private void process(Object o) {
        // Process logic
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ScopeMinimizer minimizer = new ScopeMinimizer();
        List<String> fruits = List.of("Apple", "Banana", "Cherry");

        minimizer.iterate(fruits);
        minimizer.optimizedLoop();

        // Example of declaring where first used:
        // Don't declare 'result' at the top of main!
        String message = "Hello Scope"; // Initialized exactly where needed
        System.out.println(message);
    }
}