package puzzlers.effectivejava.ch5.item28;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <h2>Prefer lists to arrays</h2>
 *
 * <p>
 * <b>Core Principle:</b> Arrays and generics have incompatible type rules. Arrays are
 * <b>covariant</b> (Sub[] is a subtype of Super[]) and <b>reified</b> (enforced at runtime).
 * Generics are <b>invariant</b> (List&lt;Type1&gt; is unrelated to List&lt;Type2&gt;) and
 * <b>erased</b> (enforced only at compile time). When mixing them, prefer {@code List<E>}
 * to {@code E[]} to ensure compile-time type safety.
 * </p>
 *
 * <h3>Advantages of Lists over Arrays</h3>
 * <ul>
 * <li><b>Compile-Time Safety:</b> Generic lists catch type mismatches during compilation,
 * whereas arrays may throw an {@code ArrayStoreException} at runtime.</li>
 * <li><b>Prevention of Runtime Failures:</b> Lists avoid the {@code ClassCastException}
 * risks associated with the "generic array creation" loophole.</li>
 * <li><b>Interoperability:</b> Lists work naturally with other generic types and methods
 * without requiring unchecked casts or {@code @SuppressWarnings} annotations.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Performance:</b> Arrays can be slightly more performant and memory-efficient
 * in low-level, high-frequency operations.</li>
 * <li><b>Conciseness:</b> Array syntax is occasionally more compact than list-based
 * equivalents.</li>
 * <li><b>Non-reifiable Types:</b> You cannot create arrays of non-reifiable types
 * (e.g., {@code new List<E>[]}), which can be annoying when returning arrays from collections.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch5.item27 EliminateUncheckedWarnings
 * @see puzzlers.effectivejava.ch5.item29 GenericTypes
 * @see puzzlers.effectivejava.ch5.item32 SafeVarargs
 * @see puzzlers.effectivejava.ch5.item33 HeterogeneousContainers
 */
public class Chooser<T> {

    private final List<T> choiceList;

    /**
     * List-based constructor: This is the preferred approach.
     * It is typesafe and avoids any "unchecked cast" warnings.
     */
    public Chooser(Collection<T> choices) {
        this.choiceList = new ArrayList<>(choices);
    }

    /**
     * Returns a random element from the collection.
     * Guaranteed to be of type T at compile time.
     */
    public T choose() {
        Random rnd = ThreadLocalRandom.current();
        return choiceList.get(rnd.nextInt(choiceList.size()));
    }

    /**
     * Demonstrates the fundamental difference between Array Covariance
     * and List Invariance.
     */
    public static void demonstration() {
        // 1. Arrays are Covariant (Legal at compile time, fails at runtime)
        Object[] objectArray = new Long[1];
        try {
            objectArray[0] = "I don't fit in"; // Throws ArrayStoreException
        } catch (ArrayStoreException e) {
            System.out.println("Array caught error at runtime: " + e);
        }

        // 2. Generics are Invariant (Fails at compile time - The Safer Choice)
        // List<Object> ol = new ArrayList<Long>(); // Compile Error: Incompatible types
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        List<Integer> numberList = List.of(1, 2, 3, 4, 5, 6);

        // Using the typesafe List-based chooser
        Chooser<Integer> die = new Chooser<>(numberList);

        System.out.println("You rolled a: " + die.choose());

        // Running the covariance demo
        demonstration();
    }
}

/**
 * Note: An array-based implementation would look like this, but is NOT recommended:
 */
class ArrayChooser<T> {
    private final T[] choiceArray;

    @SuppressWarnings("unchecked")
    public ArrayChooser(Collection<T> choices) {
        // This cast is technically unsafe, though it works in this specific case.
        // The compiler cannot guarantee that choiceArray won't cause a CCE elsewhere.
        this.choiceArray = (T[]) choices.toArray();
    }
}