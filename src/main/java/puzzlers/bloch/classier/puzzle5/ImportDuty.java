package puzzlers.bloch.classier.puzzle5;

import java.util.Arrays;

import static java.util.Arrays.toString;

/**
 * The Inherited Members Shadow Static Import
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 71
 * <p>
 * Problem:
 * Why does the call to {@code toString(args)} result in a compilation error
 * even though {@code java.util.Arrays.toString} is statically imported?
 * <p>
 * Explanation:
 * - This puzzle demonstrates the priority of name resolution in Java.
 * Specifically, it shows that **members inherited from superclasses shadow
 * static imports**.
 * - Every class in Java inherits from {@code java.lang.Object}, which
 * contains a {@code toString()} method.
 * - When the compiler sees the name {@code toString}, it first looks in the
 * current class and its hierarchy. Because {@code Object.toString()} is
 * always present, the compiler finds it immediately.
 * - Static imports are only considered if no matching name is found in
 * the class hierarchy. Since {@code Object.toString()} "claims" the name
 * first, the compiler never looks at the static import of
 * {@code Arrays.toString(Object[])}.
 * <p>
 * The Compilation Trap:
 * - Once the compiler decides that {@code toString} refers to
 * {@code Object.toString()}, it checks the arguments.
 * - Since {@code Object.toString()} takes no arguments, and we are passing
 * an array, the compiler throws an error: "method toString in class Object
 * cannot be applied to given types."
 * <p>
 *
 * <p>
 * Step-by-step (The Failure):
 * 1. **Name Lookup:** The compiler encounters {@code toString(args)}.
 * 2. **Local/Inherited Search:** It checks {@code ImportDuty} and its
 * parent {@code Object}. It finds {@code Object.toString()}.
 * 3. **Shadowing:** Because a member with that name exists in the hierarchy,
 * the static import from {@code java.util.Arrays} is obscured/shadowed.
 * 4. **Signature Match:** It tries to pass {@code Object... args} to
 * {@code Object.toString()}. This fails.
 * <p>
 * Lesson:
 * - Static imports are a "last resort." They are only used when a name
 * cannot be found via standard inheritance or local scope.
 * - Avoid using static imports for methods that have the same name as
 * common methods in {@code Object} (e.g., {@code toString}, {@code equals},
 * {@code hashCode}).
 * - If you must use them, qualify the call: {@code Arrays.toString(args)}.
 * <p>
 * Output:
 * // Uncomment line 66 -> Compile-time Error: method toString in class Object cannot be applied to (Object[])
 */
public class ImportDuty {
    public static void main(String[] args) {
        printArgs(1, 2, 3);
    }

    static void printArgs(Object... args) {
        // ERROR: Compiler finds Object.toString() first and fails on argument mismatch
//        System.out.println(toString(args));

        System.out.println(Arrays.toString(args));
    }
}