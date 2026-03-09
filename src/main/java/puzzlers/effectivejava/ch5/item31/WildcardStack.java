package puzzlers.effectivejava.ch5.item31;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * <h2>Use bounded wildcards to increase API flexibility</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use the <b>PECS</b> mnemonic: <b>P</b>roducer-<b>E</b>xtends,
 * <b>C</b>onsumer-<b>S</b>uper. If a parameterized type represents a {@code T} producer,
 * use {@code <? extends T>}; if it represents a {@code T} consumer, use {@code <? super T>}.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Maximum Flexibility:</b> Allows APIs to accept subtypes (for producers) or
 * supertypes (for consumers), moving beyond the rigid constraints of invariant generics.</li>
 * <li><b>User Transparency:</b> Properly used wildcards are nearly invisible to users,
 * allowing methods to "just work" with logical type hierarchies.</li>
 * <li><b>Interoperability:</b> Supports types that implement {@code Comparable} or
 * {@code Comparator} in a superclass rather than directly.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Return Types:</b> Do not use bounded wildcard types as return types; it
 * forces clients to use wildcards in their own code.</li>
 * <li><b>Dual Roles:</b> If a parameter is both a producer and a consumer, wildcards
 * cannot be used; an exact type match is required.</li>
 * <li><b>Complexity:</b> Declarations (like the {@code max} method) can become
 * quite verbose and difficult to read.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch5.item28 Invariance
 * @see puzzlers.effectivejava.ch5.item29 GenericTypes
 * @see puzzlers.effectivejava.ch5.item30 GenericMethods
 */
public class WildcardStack<E> {
    private List<E> elements = new ArrayList<>();

    public void push(E e) { elements.add(e); }
    public E pop() { return elements.remove(elements.size() - 1); }
    public boolean isEmpty() { return elements.isEmpty(); }

    /**
     * Wildcard type for a parameter that serves as an E producer.
     * Allows Stack<Number> to accept Iterable<Integer>.
     */
    public void pushAll(Iterable<? extends E> src) {
        for (E e : src) {
            push(e);
        }
    }

    /**
     * Wildcard type for a parameter that serves as an E consumer.
     * Allows Stack<Number> to pop elements into Collection<Object>.
     */
    public void popAll(Collection<? super E> dst) {
        while (!isEmpty()) {
            dst.add(pop());
        }
    }

    /**
     * Revised union method: Both parameters are producers.
     */
    public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }

    /**
     * Recursive type bound with wildcards.
     * T is a producer (List), and Comparable is a consumer of T.
     */
    public static <T extends Comparable<? super T>> T max(List<? extends T> list) {
        if (list.isEmpty()) throw new IllegalArgumentException("Empty list");
        T result = null;
        for (T t : list) {
            if (result == null || t.compareTo(result) > 0)
                result = Objects.requireNonNull(t);
        }
        return result;
    }

    /**
     * Wildcard capture: Public API uses the simpler wildcard.
     */
    public static void swap(List<?> list, int i, int j) {
        swapHelper(list, i, j);
    }

    // Private helper to capture the wildcard type E
    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // 1. Stack Flexibility
        WildcardStack<Number> numberStack = new WildcardStack<>();
        Iterable<Integer> integers = List.of(1, 2, 3);
        numberStack.pushAll(integers); // Producer: Integer extends Number

        Collection<Object> objects = new ArrayList<>();
        numberStack.popAll(objects);   // Consumer: Object is super of Number

        // 2. Union Flexibility
        Set<Integer> intSet = Set.of(1, 3, 5);
        Set<Double> doubleSet = Set.of(2.0, 4.0);
        Set<Number> combined = union(intSet, doubleSet);
        System.out.println("Combined: " + combined);

        // 3. Max with complex hierarchy
        // ScheduledFuture extends Delayed; Delayed extends Comparable<Delayed>.
        // This works because of Comparable<? super T>.
        // List<ScheduledFuture<?>> list = ...;
        // max(list);
    }
}