package puzzlers.effectivejava.ch8.item53;

/**
 * <h2>Use varargs judiciously</h2>
 *
 * <p>
 * <b>Core Principle:</b> Use varargs (variable arity methods) when you need a method that accepts
 * a variable number of arguments. However, do not use them as a replacement for regular parameters
 * when at least one argument is required; instead, use a fixed parameter followed by a varargs parameter.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Flexibility:</b> Allows clients to pass zero or more arguments of a specified type seamlessly.</li>
 * <li><b>Clean APIs:</b> Essential for methods like {@code printf} and reflection facilities where the number of arguments is unknown at compile time.</li>
 * <li><b>Client Convenience:</b> Eliminates the need for clients to manually create arrays before calling a method.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Runtime Risks:</b> If a method requiring "one or more" arguments is defined with only a varargs parameter, it fails at runtime (with an exception) rather than compile time if no arguments are passed.</li>
 * <li><b>Performance Overhead:</b> Every invocation of a varargs method triggers an array allocation and initialization.</li>
 * <li><b>Complexity:</b> Can lead to "ugly" code (validity checks, manual index management) if forced into scenarios requiring a minimum number of arguments.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch6.item36 EnumSet
 * @see puzzlers.effectivejava.ch9.item65 Reflection
 */
public class VarargsUsage {

    /**
     * Simple use of varargs: accepts zero or more arguments.
     * The value of sum() is 0.
     */
    public static int sum(int... args) {
        int sum = 0;
        for (int arg : args) {
            sum += arg;
        }
        return sum;
    }

    /**
     * The RIGHT way to use varargs to pass one or more arguments.
     * By making the first argument explicit, we ensure compile-time safety.
     */
    public static int min(int firstArg, int... remainingArgs) {
        int min = firstArg;
        for (int arg : remainingArgs) {
            if (arg < min) {
                min = arg;
            }
        }
        return min;
    }

    /**
     * Pattern for performance-critical situations.
     * Overloading covers 95% of calls (0-3 args) without array allocation cost.
     */
    public void foo() { }
    public void foo(int a1) { }
    public void foo(int a1, int a2) { }
    public void foo(int a1, int a2, int a3) { }
    public void foo(int a1, int a2, int a3, int... rest) {
        // Array creation only happens here (for 4+ arguments)
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // Simple varargs
        System.out.println("Sum (1,2,3): " + sum(1, 2, 3));
        System.out.println("Sum (empty): " + sum());

        // Safe "one or more" varargs
        System.out.println("Min (10, 5, 20): " + min(10, 5, 20));

        // This would cause a COMPILE-TIME error, which is preferred over runtime error:
        // int fail = min();

        // Performance pattern example (using EnumSet logic)
        VarargsUsage optimizer = new VarargsUsage();
        optimizer.foo(1, 2); // No array created
        optimizer.foo(1, 2, 3, 4, 5); // Array created for [4, 5]
    }
}