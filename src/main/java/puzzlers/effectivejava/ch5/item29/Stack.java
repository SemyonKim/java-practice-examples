package puzzlers.effectivejava.ch5.item29;

import java.util.Arrays;
import java.util.EmptyStackException;

/**
 * <h2>Favor generic types</h2>
 *
 * <p>
 * <b>Core Principle:</b> New types should be designed as generic, and existing types
 * that use {@code Object} for element types should be generified. This eliminates
 * the need for explicit casts in client code and provides compile-time type safety.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>User Safety:</b> Clients of the class do not need to perform manual casts,
 * which avoids potential {@code ClassCastException} at runtime.</li>
 * <li><b>Clean API:</b> The code is more readable and expressive as the intent
 * (what type the collection holds) is part of the declaration.</li>
 * <li><b>Internal Reusability:</b> A single implementation can work with any
 * object reference type without modification.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Primitive Types:</b> Generic type parameters cannot be primitive types
 * (e.g., {@code Stack<int>} is illegal). Boxed primitives must be used instead.</li>
 * <li><b>Array Creation:</b> You cannot create arrays of non-reifiable type parameters
 * ({@code new E[]}). This requires workarounds like casting an {@code Object[]}
 * or using {@code Object[]} internally with casts on retrieval.</li>
 * <li><b>Heap Pollution:</b> The preferred workaround (casting {@code Object[]} to
 * {@code E[]}) causes the runtime type of the array to differ from its compile-time
 * type, though it is often harmless.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item7 StackMemoryLeeks
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch5.item27 EliminateUncheckedWarnings
 * @see puzzlers.effectivejava.ch5.item28 PreferListsToArrays
 * @see puzzlers.effectivejava.ch5.item32 HeapPollution
 * @see puzzlers.effectivejava.ch9.item61 BoxedPrimitives
 * @see puzzlers.effectivejava.ch9.item68 NamingConventions
 */
public class Stack<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The elements array will contain only E instances from push(E).
     * This is sufficient to ensure type safety, but the runtime
     * type of the array won't be E[]; it will always be Object[]!
     */
    @SuppressWarnings("unchecked")
    public Stack() {
        // Option 1: The more readable/concise approach.
        // We cast Object[] to E[] once here.
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = elements[--size];
        elements[size] = null; // Eliminate obsolete reference
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }

    /* * Note: Option 2 (Alternative implementation)
     * Field: private Object[] elements;
     * In pop():
     * @SuppressWarnings("unchecked") E result = (E) elements[--size];
     */

    // --- Client Usage ---

    public static void main(String[] args) {
        // Command line arguments printed in reverse order, upper-cased
        String[] mockArgs = {"effective", "java", "generics"};

        Stack<String> stack = new Stack<>();
        for (String arg : mockArgs) {
            stack.push(arg);
        }

        while (!stack.isEmpty()) {
            // No explicit cast required: pop() returns String
            System.out.println(stack.pop().toUpperCase());
        }
    }
}