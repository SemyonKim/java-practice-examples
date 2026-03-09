package puzzlers.effectivejava.ch3.item13;

import java.util.Arrays;

/**
 * <h2>Override clone judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> The {@code Cloneable} interface is a flawed mixin that modifies
 * the behavior of {@code Object.clone()}. If you implement it, you must provide a public
 * {@code clone} method that first calls {@code super.clone()} and then "fixes" any mutable
 * internal state to ensure the clone is truly independent of the original.
 * </p>
 *
 * <h3>The Cloneable Protocol</h3>
 * <ul>
 * <li><b>Mechanism:</b> Implementing {@code Cloneable} makes {@code Object.clone()} return
 * a field-by-field copy; otherwise, it throws {@code CloneNotSupportedException}.</li>
 * <li><b>Requirement:</b> By convention, the returned object should be obtained via
 * {@code super.clone()}. This ensures the correct class instance is created.</li>
 * <li><b>Deep Copy:</b> For classes with mutable state, a shallow copy is disastrous.
 * You must recursively clone internal mutable objects or use deep-copying techniques.</li>
 * </ul>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Array Performance:</b> Arrays are the sole compelling use case for {@code clone()};
 * it is the preferred way to duplicate them.</li>
 * <li><b>Polymorphic Copying:</b> If a class hierarchy correctly implements {@code clone()},
 * it allows for cloning without knowing the exact subclass.</li>
 * </ul>
 *
 * <h3>Limitations / Warnings</h3>
 * <ul>
 * <li><b>Extralinguistic:</b> It creates objects without calling a constructor, which
 * can lead to corruption if invariants aren't properly established.</li>
 * <li><b>Final Field Conflict:</b> The {@code clone} architecture is incompatible with
 * {@code final} fields referring to mutable objects, as {@code clone} cannot reassign them.</li>
 * <li><b>Checked Exceptions:</b> {@code Object.clone()} throws a checked exception,
 * forcing annoying {@code try-catch} boilerplate.</li>
 * <li><b>Better Alternatives:</b> For most classes, a <b>copy constructor</b> or
 * <b>copy factory</b> is superior—they are safer, don't conflict with {@code final} fields,
 * and don't require casts.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item20 MixinInterfaces
 * @see puzzlers.effectivejava.ch2.item7 MemoryLeaks
 * @see puzzlers.effectivejava.ch10.item71 AvoidCheckedExceptions
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 */
public class Stack implements Cloneable {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        this.elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) throw new java.util.EmptyStackException();
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }

    private void ensureCapacity() {
        if (elements.length == size)
            elements = Arrays.copyOf(elements, 2 * size + 1);
    }

    /**
     * Correct implementation for a class with mutable state.
     * We must "fix" the internal array to avoid a shallow copy.
     */
    @Override
    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            // Deep copy: Arrays are the best use case for .clone()
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen if we implement Cloneable
        }
    }

    // --- Preferred Alternative: Copy Constructor ---

    /**
     * A copy constructor is usually a better alternative to clone().
     */
    public Stack(Stack s) {
        this.elements = s.elements.clone();
        this.size = s.size;
    }

    // --- Client Usage ---

    public static void clientUsage() {
        Stack original = new Stack();
        original.push("Item 1");
        original.push("Item 2");

        // Using clone
        Stack clone = original.clone();

        // Using copy constructor (preferred)
        Stack betterCopy = new Stack(original);

        System.out.println("Original size: " + original.size);
        System.out.println("Clone size: " + clone.size);

        // Verification: Modifying clone shouldn't affect original
        clone.pop();
        System.out.println("Original size after clone pop: " + original.size);
    }
}