package puzzlers.valeev.collections.mistake5;

import java.util.*;

/**
 * Mixing Collection.remove(Object) and List.remove(int) for List of Integer
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 77
 * <p>
 * Problem:
 * When using a {@code List<Integer>}, the {@code remove} method is overloaded:
 * - {@code remove(int index)}: Inherited from List, removes by position.
 * - {@code remove(Object o)}: Inherited from Collection, removes by value.
 * Passing a primitive {@code int} to this method often results in the unintended
 * removal of an index rather than a value.
 * <p>
 * Expected behavior:
 * A developer calling {@code list.remove(0)} on a list of integers might expect
 * to remove the value {@code 0} from the collection.
 * <p>
 * Actual behavior:
 * The Java compiler prioritizes primitive matching over autoboxing. Since the
 * argument {@code 0} is a primitive {@code int}, it matches {@code remove(int index)}
 * perfectly. Consequently, the element at the first position is removed, regardless
 * of its value.
 * <p>
 * Explanation:
 * - Overload Resolution: Java's method resolution rules look for the most specific
 * match. A primitive {@code int} is an exact match for the index-based version.
 * To reach the object-based version, the {@code int} would have to be autoboxed
 * to an {@code Integer}, which is considered a "lesser" match.
 * - Return Type Discrepancy: {@code remove(int)} returns the element being removed
 * (the generic type E), while {@code remove(Object)} returns a {@code boolean}.
 * This difference can be used to catch the error at compile time.
 * <p>
 * Step-by-step (The Mechanics):
 * - Create a {@code List<Integer>} containing.
 * - Call {@code list.remove(0)}.
 * - The compiler binds to {@code remove(int index)}.
 * - The element at index 0 (value 10) is removed.
 * - The value 0 was NOT removed.
 * <p>
 * Ways to Avoid This Mistake:
 * - Explicit Casting: Cast the argument to an {@code Integer} or {@code Object}
 * to force the correct overload: {@code list.remove((Integer) 0)}.
 * - Value Wrapping: Use {@code Integer.valueOf(value)} to ensure you are passing
 * an object.
 * - Use Return Value: Assign the result to a boolean. If you try to assign the
 * result of {@code remove(int)} to a boolean, the code will not compile,
 * alerting you to the mismatch.
 * - Primitive Collections: Use libraries like FastUtil or Trove. They often
 * provide distinct method names like {@code rem(int)} or {@code removeInt(int)}
 * to avoid this ambiguity entirely.
 * <p>
 * Lesson:
 * - Be extremely cautious when calling {@code remove()} on any {@code List} of
 * boxed primitives (Integer, Short, Character).
 * - Trust IDE parameter hints; they will show if the argument is being treated
 * as an "index" or an "object".
 * <p>
 * Output:
 * - List after list.remove(0):
 * - List after list.remove((Integer) 0):
 */
public class ListRemoveMistake {

    public static void main(String[] args) {
        // --- Demonstration of the Mistake ---
        List<Integer> list1 = new ArrayList<>(List.of(10, 20, 0));
        // The intention is to remove the value 0, but we pass a primitive 0
        list1.remove(0);
        System.out.println("List after list.remove(0) [Index 0 removed]: " + list1);

        // --- Demonstration of the Fix ---
        List<Integer> list2 = new ArrayList<>(List.of(10, 20, 0));
        // Explicitly casting to Integer forces the remove(Object) overload
        list2.remove((Integer) 0);
        System.out.println("List after list.remove((Integer) 0) [Value 0 removed]: " + list2);

        // --- Using return type to catch the error ---
        List<Integer> list3 = new ArrayList<>(List.of(10, 20, 0));

        /* The following line would cause a compilation error:
           boolean success = list3.remove(0);
           Error: incompatible types: Integer cannot be converted to boolean
        */

        boolean success = list3.remove(Integer.valueOf(0));
        System.out.println("Removal of value 0 successful: " + success);
        System.out.println("Final state: " + list3);
    }
}