package puzzlers.effectivejava.ch5.item26;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <h2>Don’t use raw types</h2>
 *
 * <p>
 * <b>Core Principle:</b> Never use raw types in new code. Generic types without their type parameters
 * (raw types) exist only for backward compatibility with pre-generics code. Using them forfeits
 * the type safety and expressiveness that generics provide.
 * </p>
 *
 * <h3>Advantages of Parameterized Types</h3>
 * <ul>
 * <li><b>Compile-Time Safety:</b> Errors are caught during compilation rather than at runtime via {@code ClassCastException}.</li>
 * <li><b>Self-Documenting:</b> The type declaration explicitly states what the collection contains, removing the need for explanatory comments.</li>
 * <li><b>Automatic Casting:</b> The compiler inserts invisible, guaranteed-to-succeed casts when retrieving elements.</li>
 * <li><b>Expressiveness:</b> Parameterized types like {@code List<Object>} or {@code List<?>} clearly communicate intent (holding any object vs. holding an unknown type).</li>
 * </ul>
 *
 * <h3>Limitations & Exceptions</h3>
 * <ul>
 * <li><b>Class Literals:</b> You must use raw types in class literals. {@code List.class} is legal, but {@code List<String>.class} is not.</li>
 * <li><b>instanceof Operator:</b> Because generic type information is erased at runtime, {@code instanceof} is only legal on raw types or unbounded wildcard types.</li>
 * <li><b>Migration Compatibility:</b> Raw types must remain legal to allow legacy code to interoperate with newer generic code.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item27 EliminateUncheckedWarnings
 * @see puzzlers.effectivejava.ch5.item28 ErasureAndArrays
 * @see puzzlers.effectivejava.ch5.item30 GenericMethods
 * @see puzzlers.effectivejava.ch5.item31 Wildcards
 */
public class RawTypeDemo {

    /**
     * Use of raw types (Don't do this!).
     * A "Coin" can be accidentally added to a "Stamp" collection, causing a
     * Runtime Exception later.
     */
    private static void rawTypeExample() {
        Collection stamps = new ArrayList(); // Raw type
        stamps.add(new Stamp());
        stamps.add(new Coin()); // Compiles with a warning, but it's a bug!

        for (Object obj : stamps) {
            Stamp stamp = (Stamp) obj; // Throws ClassCastException at runtime
            stamp.cancel();
        }
    }

    /**
     * Use of parameterized types (Do this).
     * The compiler prevents the insertion of "Coin" at compile-time.
     */
    private static void parameterizedTypeExample() {
        Collection<Stamp> stamps = new ArrayList<>();
        stamps.add(new Stamp());
        // stamps.add(new Coin()); // Compile-time error!
    }

    /**
     * Unsafe: uses raw List. Can corrupt the list's type invariant.
     */
    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }

    /**
     * Safe: Unbounded wildcard type.
     * Useful when the actual type parameter doesn't matter.
     * You cannot put any element (except null) into a Collection<?>.
     */
    static int numElementsInCommon(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }

    /**
     * Legitimate use of raw types: instanceof and class literals.
     */
    private static void legalRawTypeUsage(Object o) {
        // Exception 1: Class literals
        Class<?> clazz = List.class;

        // Exception 2: instanceof operator
        if (o instanceof Set) {       // Raw type check
            Set<?> s = (Set<?>) o;    // Cast to wildcard type
            System.out.println("Object is a set of size: " + s.size());
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Demonstrating the danger of raw types in unsafeAdd
        List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));

        try {
            String s = strings.get(0); // Compiler-generated cast fails here
        } catch (ClassCastException e) {
            System.err.println("Runtime failure due to raw type usage: " + e.getMessage());
        }

        // Demonstrating wildcard usage
        Set<String> s1 = Set.of("A", "B");
        Set<Integer> s2 = Set.of(1, 2);
        System.out.println("Common elements: " + numElementsInCommon(s1, s2));

        legalRawTypeUsage(new HashSet<String>());
    }

    // --- Helper classes ---

    static class Stamp { void cancel() {} }
    static class Coin { }
}