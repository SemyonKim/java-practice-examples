package puzzlers.bloch.libraryadvanced.puzzle3;

import java.lang.reflect.Constructor;

/**
 * Reflection and Inner Class Implicit Constructor
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 80
 * <p>
 * Problem:
 * Why does {@code Inner.class.newInstance()} throw an {@code InstantiationException},
 * even though the {@code Inner} class appears to have a default no-arg constructor?
 * <p>
 * Explanation:
 * - A non-static **inner class** (like {@code Inner}) always maintains an
 * implicit reference to its enclosing instance ({@code Outer}).
 * - Because of this, the compiler automatically modifies the constructor of
 * the inner class to accept an instance of the outer class as its first
 * parameter: {@code Inner(Outer this$0)}.
 * - {@code Class.newInstance()} (now deprecated) attempts to invoke a
 * **zero-argument** constructor. Since the compiler-generated constructor
 * requires an {@code Outer} instance, no zero-argument constructor exists,
 * leading to a runtime exception.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Implicit Parameters:** Every non-static inner class constructor is
 * "secretly" prepended with a parameter for the enclosing class.
 * 2. **Reflection Failure:** Reflection methods that look for a default
 * constructor will fail because the signature is actually {@code Inner(Outer)}.
 * 3. **The Solution:** You must explicitly fetch the constructor that
 * accepts the {@code Outer} type and pass an instance of {@code Outer}
 * during invocation.
 * <p>
 * Lesson:
 * - Inner classes are rarely what you want when using reflection or
 * frameworks that require "POJO" (Plain Old Java Object) behavior.
 * - If a class does not need access to the outer class's instance members,
 * always declare it as **static** (making it a nested class).
 * <p>
 * Output:
 * // BUG Version: Exception in thread "main" java.lang.InstantiationException
 * // FIX Version: Hello world
 */
public class Outer {
    public static void main(String[] args) throws Exception {
        new Outer().greetWorld();
    }

    private void greetWorld() throws Exception {
        /* * INCORRECT VERSION:
         * //System.out.println(Inner.class.newInstance());
         * Result: InstantiationException (no-arg constructor not found)
         * newInstance() is deprecated and fails here because it looks for
         * a no-arg constructor that doesn't exist for a non-static inner class.
         */

        /* FIX 1: Access the constructor explicitly with the Outer parameter */
        Constructor<Inner> c = Inner.class.getConstructor(Outer.class);
        System.out.println(c.newInstance(Outer.this));
    }

    /* * FIX 2: Transform inner class to a nested (static) class.
     * This removes the implicit Outer parameter from the constructor.
     * public static class Inner { ... }
     */

    public class Inner {
        @Override
        public String toString() {
            return "Hello world";
        }
    }
}