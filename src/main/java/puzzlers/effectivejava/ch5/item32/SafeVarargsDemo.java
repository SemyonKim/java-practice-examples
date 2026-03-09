package puzzlers.effectivejava.ch5.item32;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <h2>Combine generics and varargs judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> Generic varargs are a leaky abstraction because they are
 * implemented using arrays, which are reified and covariant, unlike erased and
 * invariant generics. This can lead to <b>heap pollution</b>. Use {@code @SafeVarargs}
 * only if the method is truly safe (it doesn't store anything in the array and doesn't
 * let the array escape).
 * </p>
 *
 * <h3>Advantages of @SafeVarargs</h3>
 * <ul>
 * <li><b>Client Convenience:</b> Suppresses confusing and "noisy" warnings at the
 * call site for methods known to be typesafe.</li>
 * <li><b>API Cleanliness:</b> Allows for natural-looking utility methods (like
 * {@code List.of} or {@code Arrays.asList}) without forcing clients to use
 * {@code @SuppressWarnings}.</li>
 * </ul>
 *
 * <h3>Limitations & Dangers</h3>
 * <ul>
 * <li><b>Heap Pollution:</b> Storing a value of an incompatible type in the varargs
 * array can cause a {@code ClassCastException} at a distance from the actual error.</li>
 * <li><b>Escaping References:</b> Returning or exposing the varargs array is
 * dangerous because the compiler often allocates it as {@code Object[]}, leading
 * to runtime failures when cast back to a specific type.</li>
 * <li><b>Scope:</b> Only allowed on methods that cannot be overridden (static, final,
 * or private instance methods in Java 9+).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item27 EliminateUncheckedWarnings
 * @see puzzlers.effectivejava.ch5.item28 PreferListsToArrays
 * @see puzzlers.effectivejava.ch8.item53 Varargs
 */
public class SafeVarargsDemo {

    /**
     * DANGEROUS: Mixing generics and varargs can violate type safety.
     * This method causes heap pollution and throws ClassCastException.
     */
    static void dangerous(List<String>... stringLists) {
        List<Integer> intList = List.of(42);
        Object[] objects = stringLists; // Covariance of arrays
        objects[0] = intList;           // Heap pollution!
        String s = stringLists[0].get(0); // Invisible cast fails: ClassCastException
    }

    /**
     * UNSAFE: Exposes a reference to its generic parameter array.
     * Propagates heap pollution up the call stack.
     */
    static <T> T[] toArray(T... args) {
        return args;
    }

    /**
     * SAFE: Follows the two rules for @SafeVarargs:
     * 1. Doesn't store anything in the array.
     * 2. Doesn't make the array visible to untrusted code.
     */
    @SafeVarargs
    static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    /**
     * PREFERRED ALTERNATIVE: Use a List instead of varargs.
     * This is typesafe by design and requires no @SafeVarargs.
     */
    static <T> List<T> flattenList(List<List<? extends T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // 1. Illustrating the danger of toArray (escaped array)
        try {
            // pickTwo internally calls toArray, which returns Object[]
            String[] attributes = pickTwo("Good", "Fast", "Cheap");
        } catch (ClassCastException e) {
            System.err.println("Failure: Escaped generic varargs array (Object[] is not String[])");
        }

        // 2. Illustrating the safe usage
        List<String> friends = List.of("Joey", "Ross");
        List<String> romans = List.of("Brutus", "Caesar");
        List<String> combined = flatten(friends, romans);
        System.out.println("Flattened: " + combined);

        // 3. Illustrating the preferred typesafe alternative
        List<String> saferCombined = flattenList(List.of(friends, romans));
        System.out.println("Safer Flattened: " + saferCombined);
    }

    /**
     * Helper that propagates an Object[] array back to a context expecting T[].
     */
    static <T> T[] pickTwo(T a, T b, T c) {
        switch(ThreadLocalRandom.current().nextInt(3)) {
            case 0: return toArray(a, b);
            case 1: return toArray(a, c);
            case 2: return toArray(b, c);
            default: throw new AssertionError();
        }
    }
}