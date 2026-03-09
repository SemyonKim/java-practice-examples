package puzzlers.effectivejava.ch5.item27;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <h2>Eliminate unchecked warnings</h2>
 *
 * <p>
 * <b>Core Principle:</b> Eliminate every unchecked warning that you can. If a warning
 * cannot be eliminated but you can prove the code is typesafe, suppress it using the
 * {@code @SuppressWarnings("unchecked")} annotation in the narrowest possible scope
 * and document why it is safe.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Type Safety:</b> Eliminating warnings ensures that your code will not throw
 * a {@code ClassCastException} at runtime.</li>
 * <li><b>Code Health:</b> Keeping the build "clean" ensures that new, genuine warnings
 * are immediately visible and not lost among "false alarms."</li>
 * <li><b>Maintainability:</b> Documenting the rationale for suppression helps future
 * maintainers understand why the code is safe despite the compiler's concern.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>False Security:</b> Suppressing a warning without actual proof of type safety
 * masks potential runtime failures.</li>
 * <li><b>Scope Restrictions:</b> The {@code @SuppressWarnings} annotation cannot be
 * placed on {@code return} statements, often requiring the creation of a local variable.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch5.item28 ErasureAndArrays
 * @see puzzlers.effectivejava.ch5.item29 GenericTypes
 */
public class WarningManager<E> {

    private E[] elements;
    private int size = 0;

    /**
     * Demonstrates an easy-to-eliminate warning using the diamond operator.
     */
    public void eliminateSimpleWarning() {
        // Warning: [unchecked] unchecked conversion
        // Set<String> exaltation = new HashSet();

        // Fixed: The diamond operator allows the compiler to infer the type
        Set<String> exaltation = new HashSet<>();
    }

    /**
     * Demonstrates proper suppression in the smallest possible scope.
     * We cannot put the annotation on the return statement.
     */
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            // This cast is correct because the array we're creating is of the
            // same type as the one passed in, which is T[].
            @SuppressWarnings("unchecked")
            T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
            return result;
        }
        System.arraycopy(elements, 0, a, 0, size);
        if (a.length > size) {
            a[size] = null;
        }
        return a;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        WarningManager<String> manager = new WarningManager<>();

        // Example of a clean compilation outcome
        manager.eliminateSimpleWarning();

        String[] seedArray = new String[0];
        String[] result = manager.toArray(seedArray);

        System.out.println("Array processed successfully without unchecked exceptions.");
    }
}