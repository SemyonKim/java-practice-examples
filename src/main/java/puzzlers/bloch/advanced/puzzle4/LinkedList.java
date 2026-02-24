package puzzlers.bloch.advanced.puzzle4;

/**
 * Generic Shadowing: The Identity Crisis
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 89
 * <p>
 * Problem:
 * Why does the code fail to compile with "incompatible types" when the inner
 * class {@code Node<E>} clearly uses the same type parameter name {@code E}
 * as its outer class {@code LinkedList<E>}?
 * <p>
 * Explanation:
 * - This is an example of **Type Parameter Shadowing**. By declaring
 * {@code private class Node<E>}, the inner class introduces a *new* type
 * parameter named {@code E} that hides the outer class's {@code E}.
 * - Even though they share the same name, the compiler treats them as distinct,
 * unrelated types. It is as if you named them {@code E1} and {@code E2}.
 * - Because the inner {@code Node} is non-static, it has an implicit reference
 * to the outer class. However, the {@code head} field belongs to
 * {@code LinkedList<E_outer>}, while {@code this} inside the constructor
 * refers to {@code Node<E_inner>}.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Shadowing:** The declaration {@code Node<E>} creates a scope where
 * {@code E} refers to the inner type.
 * 2. **Type Conflict:** The line {@code this.next = head} attempts to assign
 * a {@code Node<E_outer>} to a variable of type {@code Node<E_inner>}.
 * 3. **Failure:** The compiler cannot verify that the two {@code E} types
 * are the same, leading to a compilation error.
 * <p>
 * The Fixes:
 * 1. **Eliminate Inner Parameter:** Remove the {@code <E>} from the inner
 * class. As a non-static inner class, {@code Node} can see the outer
 * {@code E} directly.
 * 2. **Static Nesting:** Make the inner class {@code static}. This decouples
 * the node from the list instance, requiring you to pass the {@code head}
 * explicitly and manage the links within the outer class methods.
 * <p>
 * Lesson:
 * - Never reuse type parameter names in nested or inner classes. It leads
 * to confusion for both the compiler and the human reader.
 * - If a class doesn't need access to the outer instance's fields, prefer
 * {@code static} nested classes for better performance and clarity.
 * <p>
 * Output:
 * // Compilation error: incompatible types: Node<E> cannot be converted to Node<E>
 * // (where the two Es are different type parameters)
 */
public class LinkedList<E> {
    private Node<E> head = null;

    // BUG: Shadowing of the type parameter E
    private class Node<E> {
        E value;
        Node<E> next;

        Node(E value) {
            this.value = value;
            // this.next = head; // ERROR: Incompatible types
            // head = this;      // ERROR: Incompatible types
        }
    }

    public void add(E e) {
        // new Node<E>(e); // Link node as new head
    }

    /*
    // FIX 1: Non-static inner class using outer type parameter
    private class Node {
        E value;
        Node next;

        Node(E value) {
            this.value = value;
            this.next = head;
            head = this;
        }
    }
    */

    /*
    // FIX 2: Static nested class with explicit linking (Best Practice)
    private static class Node<T> {
        T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    public void add(E e) {
        head = new Node<E>(e, head);
    }
    */

    public void dump() {
        for (Node<E> n = head; n != null; n = n.next) {
            System.out.print(n.value + " ");
        }
    }

    public static void main(String[] args) {
        LinkedList<String> list = new LinkedList<>();
        list.add("world");
        list.add("Hello");
        list.dump();
    }
}