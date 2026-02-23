package puzzlers.bloch.classy.puzzle7;

/**
 * The Static Methods on Null References
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 54
 * <p>
 * Problem:
 * What happens when you call a static method on an expression that
 * evaluates to {@code null}? Will the JVM throw a
 * {@link NullPointerException}, or will the "Ghost" of the class
 * execute the method?
 * <p>
 * Explanation:
 * - Static methods in Java are associated with the **class**, not with
 * a specific **instance**.
 * - When the compiler sees {@code ((Null) null).greet()}, it ignores
 * the actual value of the expression at runtime.
 * - Instead, it looks only at the **declared type** of the expression,
 * which is {@code Null}.
 * - The compiler translates the call directly to {@code Null.greet()}
 * at the bytecode level.
 * - Because the JVM doesn't actually need an instance to call a
 * {@code static} method, the fact that the object is {@code null}
 * is completely irrelevant.
 * <p>
 * The "Spooky" Result:
 * - Many developers expect a {@code NullPointerException} because of the
 * explicit {@code null} cast.
 * - Instead, the program runs perfectly and prints: **"Hello world!"**
 * <p>
 * Step-by-step:
 * 1. The expression {@code ((Null) null)} is evaluated.
 * 2. The compiler identifies the static method {@code greet()} in
 * class {@code Null}.
 * 3. The bytecode generated is {@code invokestatic}, which does
 * not check for nullity of the receiver.
 * 4. At runtime, the "receiver" expression is evaluated (and ignored),
 * and the static method is executed.
 * <p>
 * Lesson:
 * - **Clarity is Key:** Never invoke a static method using an instance
 * expression (e.g., {@code myObject.staticMethod()}). Always use the
 * class name ({@code MyClass.staticMethod()}).
 * - This behavior is a "feature" of the language (JLS 15.12.4.1), but
 * it is widely considered a confusing design choice.
 * - Static methods do not participate in polymorphism; they are
 * bound at compile-time based on the reference type.
 * <p>
 * Output:
 * // Hello world!
 */
public class Null {
    public static void greet() {
        System.out.println("Hello world!");
    }

    public static void main(String[] args) {
        // This looks like it should crash, but it won't.
        ((Null) null).greet();
    }
}