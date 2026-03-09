package puzzlers.effectivejava.ch2.item4;

/**
 * <h2>Enforce noninstantiability with a private constructor</h2>
 *
 * <p><strong>Core Principle:</strong> Utility classes—those designed only to group related
 * static methods or fields—should not be instantiable. Since the compiler provides a
 * default public constructor in the absence of explicit ones, you must include a
 * private constructor to ensure the class is never instantiated.</p>
 *
 * <h3>Utility Class (Noninstantiable Entity)</h3>
 * <ul>
 * <li><b>Advantage 1:</b> Explicit Intent - Prevents users from creating nonsensical instances of a class meant only for static access (e.g., <code>java.lang.Math</code>).</li>
 * <li><b>Advantage 2:</b> Simplicity - Provides a simple, foolproof idiom to block instantiation without complex machinery.</li>
 * <li><b>Advantage 3:</b> Inheritance Prevention - As a side effect, it prevents the class from being subclassed because a subclass has no accessible superclass constructor to invoke.</li>
 * </ul>
 *
 * <ul>
 * <li><b>Limitation 1:</b> Counter-intuitive - The constructor is provided specifically so that it cannot be invoked, which can be confusing without documentation.</li>
 * <li><b>Limitation 2:</b> Abstract Class Failure - Attempting to use the <code>abstract</code> keyword to prevent instantiation does not work, as it can be subclassed and misleads the user into thinking the class was designed for inheritance.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 StaticFactories
 * @see puzzlers.effectivejava.ch4.item19 DesignForInheritance
 */
public class UtilityClass {

    /**
     * <b>Noninstantiability Enforced:</b>
     * The private constructor is inaccessible outside the class.
     * The AssertionError provides insurance against accidental internal invocation.
     */
    private UtilityClass() {
        throw new AssertionError("This utility class cannot be instantiated.");
    }

    /**
     * <b>Utility Method Example:</b>
     * Typical use case: grouping methods for primitives or arrays.
     */
    public static String exampleStaticMethod(String input) {
        return input.toUpperCase();
    }

    /**
     * <b>Client Usage Example</b>
     */
    public void clientUsage() {
        // Correct usage: Accessing members statically
        String result = UtilityClass.exampleStaticMethod("hello");

        // Incorrect usage: The following would result in a compile-time error
        // UtilityClass myUtil = new UtilityClass();
    }
}