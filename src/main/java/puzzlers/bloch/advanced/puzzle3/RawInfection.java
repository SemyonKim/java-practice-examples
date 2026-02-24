package puzzlers.bloch.advanced.puzzle3;

import java.util.Arrays;
import java.util.List;

/**
 * Raw Type Infection: The Erasure Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 88
 * <p>
 * Problem:
 * Why does the method {@code stringList()}, which explicitly returns a
 * {@code List<String>}, cause a compilation error when accessed via a raw
 * type reference, claiming that it returns a raw {@code List} instead?
 * <p>
 * Explanation:
 * - This is a consequence of how Java handles **Raw Types** for backward
 * compatibility. When you use a generic class without type arguments (a raw type),
 * the compiler applies a "blanket erasure" to the entire class.
 * - According to JLS §4.8, the type of *every* instance method and field in a
 * raw type is the erasure of its type in the generic declaration.
 * - Even though {@code stringList()} does not use the type parameter {@code T},
 * its return type {@code List<String>} is erased to the raw {@code List}
 * because it is being accessed through the raw reference {@code ri}.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Raw Declaration:** {@code RawInfection ri} is declared as a raw type.
 * 2. **Member Erasure:** The compiler looks at all members of {@code RawInfection}.
 * Because {@code ri} is raw, {@code List<String> stringList()} is treated
 * as {@code List stringList()}.
 * 3. **Type Mismatch:** Inside the {@code for} loop, the compiler expects
 * the elements of a raw {@code List} to be of type {@code Object}.
 * 4. **Failure:** Attempting to assign these {@code Object} elements to
 * the loop variable {@code String s} results in a compilation error.
 * <p>
 * The Fixes:
 * 1. **Define Type Argument:** Use {@code RawInfection<Object> ri}. This
 * stops the class from being raw and preserves other generic signatures.
 * 2. **Use Wildcards:** Use {@code RawInfection<?> ri} if the specific
 * type of {@code T} is irrelevant.
 * 3. **Manual Casting:** If forced to use raw types, treat the returned
 * elements as {@code Object} and cast manually (not recommended).
 * <p>
 * Lesson:
 * - Raw types are "infectious." Using one doesn't just disable generics for
 * the type parameters; it wipes the generic metadata for the entire instance.
 * - Always prefer {@code <Object>} or {@code <?>} over raw types to maintain
 * type safety for unrelated methods.
 * <p>
 * Output:
 * // Compilation error: incompatible types: Object cannot be converted to String
 * // for (String s : ri.stringList())
 */
public class RawInfection<T> {
    private final T first;
    private final T second;

    public RawInfection(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    public List<String> stringList() {
        return Arrays.asList(String.valueOf(first), String.valueOf(second));
    }

    public static void main(String[] args) {
        // BUG: Use of raw type triggers erasure of all generic signatures in the class
        RawInfection ri = new RawInfection<Object>(23, "skidoo");

        // FIX 1: RawInfection<Object> ri = new RawInfection<Object>(23, "skidoo");
        // FIX 2: RawInfection<?> ri = new RawInfection<Object>(23, "skidoo");

        System.out.println(ri.getFirst() + " " + ri.getSecond());

        // Compilation fails here because ri.stringList() returns a raw List
//        for (String s : ri.stringList()) {
//            System.out.print(s + " ");
//        }

        // FIX 3: for (Object s : ri.stringList()) { System.out.print(s + " "); }
    }
}