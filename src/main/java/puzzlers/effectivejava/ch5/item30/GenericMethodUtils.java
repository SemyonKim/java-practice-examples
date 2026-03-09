package puzzlers.effectivejava.ch5.item30;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * <h2>Favor generic methods</h2>
 *
 * <p>
 * <b>Core Principle:</b> Just as classes can be generic, so can methods. Static utility
 * methods that operate on parameterized types should be generic to ensure type safety
 * and eliminate the need for explicit casts in client code.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Type Safety:</b> Eliminates the risk of {@code ClassCastException} by catching
 * type mismatches at compile time.</li>
 * <li><b>Ease of Use:</b> Clients can call the method without manual casting of
 * arguments or return values.</li>
 * <li><b>Generic Singleton Factory:</b> Allows a single, stateless, immutable object
 * to be shared across all type parameterizations via erasure.</li>
 * <li><b>Expressive Constraints:</b> Supports <i>recursive type bounds</i> to express
 * complex relationships, such as mutual comparability.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Rigid Typing:</b> Basic generic methods require input types and return types
 * to match exactly. (This is addressed by wildcards in Item 31).</li>
 * <li><b>Primitive Restrictions:</b> Type parameters cannot be primitives; boxed types
 * must be used.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item2 SimulatedSelfTypes
 * @see puzzlers.effectivejava.ch3.item14 Comparable
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch5.item28 Erasure
 * @see puzzlers.effectivejava.ch5.item29 GenericTypes
 * @see puzzlers.effectivejava.ch5.item31 Wildcards
 * @see puzzlers.effectivejava.ch8.item55 Optionals
 */
public class GenericMethodUtils {

    /**
     * Generic method returning the union of two sets.
     * The type parameter list <E> is placed between modifiers and return type.
     */
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    // --- Generic Singleton Factory Pattern ---

    private static final UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    /**
     * Returns an identity function. Using a singleton factory is efficient
     * because the function is stateless.
     */
    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        // Safe because the identity function returns the argument unchanged
        return (UnaryOperator<T>) IDENTITY_FN;
    }

    // --- Recursive Type Bound ---

    /**
     * Returns the maximum value in a collection.
     * <E extends Comparable<E>> ensures elements are mutually comparable.
     */
    public static <E extends Comparable<E>> E max(Collection<E> c) {
        if (c.isEmpty()) {
            throw new IllegalArgumentException("Empty collection");
        }

        E result = null;
        for (E e : c) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // 1. Union Demo
        Set<String> guys = Set.of("Tom", "Dick", "Harry");
        Set<String> stooges = Set.of("Larry", "Moe", "Curly");
        Set<String> unionResult = union(guys, stooges);
        System.out.println("Union: " + unionResult);

        // 2. Identity Function Demo
        String[] strings = { "jute", "hemp", "nylon" };
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strings) {
            System.out.println("Identity String: " + sameString.apply(s));
        }

        // 3. Max with Recursive Type Bound Demo
        Collection<Integer> nums = Set.of(1, 15, 3);
        System.out.println("Max number: " + max(nums));
    }
}