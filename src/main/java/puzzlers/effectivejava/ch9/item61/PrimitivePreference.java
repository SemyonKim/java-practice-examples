package puzzlers.effectivejava.ch9.item61;

import java.util.Comparator;

/**
 * <h2>Prefer primitive types to boxed primitives</h2>
 *
 * <p>
 * <b>Core Principle:</b> Java has a two-part type system: primitives (e.g., {@code int}) and
 * reference types (e.g., {@code Integer}). While autoboxing blurs the line, primitives are
 * safer and more efficient. Use primitives by default and boxed primitives only when necessary.
 * </p>
 *
 * <h3>Major Differences</h3>
 * <ul>
 * <li><b>Identity vs. Value:</b> Primitives have only values. Boxed primitives have identities
 * distinct from their values. Two {@code Integer} instances can represent the same number
 * but be different objects (leading to {@code ==} failure).</li>
 * <li><b>Functionality:</b> Primitives are always functional. Boxed primitives can be {@code null},
 * which leads to {@code NullPointerException} during auto-unboxing.</li>
 * <li><b>Efficiency:</b> Primitives are significantly faster and consume less memory.</li>
 * </ul>
 *
 * <h3>Advantages of Primitives</h3>
 * <ul>
 * <li><b>Performance:</b> Avoids the overhead of object creation and constant boxing/unboxing
 * in loops.</li>
 * <li><b>Safety:</b> Eliminates the risk of identity comparison errors and {@code NullPointerException}.</li>
 * </ul>
 *
 * <h3>Limitations (When to use Boxed Primitives)</h3>
 * <ul>
 * <li><b>Collections:</b> Primitives cannot be used as elements in {@code Collection} or as keys/values in {@code Map}.</li>
 * <li><b>Type Parameters:</b> You must use {@code List<Integer>}, not {@code List<int>}.</li>
 * <li><b>Reflection:</b> Reflective method invocations require boxed types.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item6 AvoidNeedlessObjects
 * @see java.util.Comparator#naturalOrder()
 */
public class PrimitivePreference {

    /**
     * BROKEN: Demonstrates identity comparison flaw.
     * Comparing two new Integer(42) objects with '==' returns false because
     * they are different object identities, even though they hold the same value.
     */
    public static final Comparator<Integer> brokenNaturalOrder =
            (i, j) -> (i < j) ? -1 : (i == j ? 0 : 1);

    /**
     * FIXED: Explicitly unbox to primitives before comparison to avoid
     * identity comparison bugs.
     */
    public static final Comparator<Integer> fixedNaturalOrder = (iBoxed, jBoxed) -> {
        int i = iBoxed, j = jBoxed; // Auto-unboxing
        return i < j ? -1 : (i == j ? 0 : 1);
    };

    /**
     * DANGEROUS: Demonstrates unboxing a null reference.
     * Throws NullPointerException because 'i' is unboxed to check equality with 42.
     */
    public void unbelievable() {
        Integer i = null;
        try {
            if (i == 42) { // Auto-unboxing 'null'
                System.out.println("Unbelievable");
            }
        } catch (NullPointerException e) {
            System.err.println("Caught expected NPE due to auto-unboxing null");
        }
    }

    /**
     * HIDEOUSLY SLOW: Demonstrates the performance cost of accidental boxing.
     * Every addition creates a new Long object.
     */
    public void slowSum() {
        Long sum = 0L; // Should be primitive 'long'
        for (long i = 0; i < Integer.MAX_VALUE / 100; i++) { // Scale reduced for demo
            sum += i; // Unbox, add, Re-box!
        }
        System.out.println("Sum: " + sum);
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        PrimitivePreference demo = new PrimitivePreference();

        // 1. Identity Comparison Trap
        Integer n1 = new Integer(42);
        Integer n2 = new Integer(42);
        System.out.println("Broken Compare (42, 42): " + brokenNaturalOrder.compare(n1, n2)); // Returns 1
        System.out.println("Fixed Compare (42, 42): " + fixedNaturalOrder.compare(n1, n2));  // Returns 0

        // 2. The Null Trap
        demo.unbelievable();

        // 3. Performance Trap
        long start = System.currentTimeMillis();
        demo.slowSum();
        System.out.println("Time taken (boxed): " + (System.currentTimeMillis() - start) + "ms");
    }
}