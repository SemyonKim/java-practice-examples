package puzzlers.effectivejava.ch9.item65;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Set;

/**
 * <h2>Prefer interfaces to reflection</h2>
 *
 * <p>
 * <b>Core Principle:</b> Reflection is a powerful tool for access to arbitrary classes at runtime,
 * but it comes with heavy costs. Whenever possible, use reflection only to <b>instantiate</b>
 * objects and then access them via a known interface or superclass.
 * </p>
 *
 * <h3>Disadvantages of Reflection</h3>
 * <ul>
 * <li><b>Loss of Compile-time Type Checking:</b> Errors that would be caught by the compiler
 * (like non-existent methods or type mismatches) become runtime exceptions.</li>
 * <li><b>Verbosity:</b> Reflective code is clumsy, tedious to write, and difficult to read
 * compared to standard method or constructor calls.</li>
 * <li><b>Performance Penalty:</b> Reflective hits are significantly slower than normal
 * invocations (often 10x slower or more).</li>
 * </ul>
 *
 * <h3>Advantages of the "Hybrid" Approach</h3>
 * <ul>
 * <li><b>Flexibility:</b> Allows a program to use classes that didn't exist when the
 * program was compiled.</li>
 * <li><b>Type Safety:</b> Once the object is instantiated and cast to an interface, the
 * rest of the code enjoys full compile-time type checking.</li>
 * <li><b>Maintainability:</b> Confines the "ugly" reflective code to a small
 * initialization block.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Unchecked Casts:</b> Instantiating via {@code Class.forName} often requires
 * suppressing warnings or handling {@code ClassCastException} at runtime.</li>
 * <li><b>Constructor Requirements:</b> Requires the target class to have a
 * parameterless constructor (or requires complex logic to find specific constructors).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch2.item1 ServiceProviderFrameworks
 * @see puzzlers.effectivejava.ch9.item64 ReferToObjectsByInterfaces
 */
public class ReflectionToInterface {

    /**
     * Demonstrates the power of reflective instantiation combined with interface access.
     * Use case: A generic set tester that can work with any Set implementation
     * provided as a class name string.
     */
    public void executeSetAnalysis(String className, String[] elements) {
        // 1. Translate class name into a Class object
        Class<? extends Set<String>> cl = null;
        try {
            // Legitimate unchecked cast - handled by ClassCastException later
            cl = (Class<? extends Set<String>>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            fatalError("Class not found: " + className);
        }

        // 2. Get the parameterless constructor
        Constructor<? extends Set<String>> cons = null;
        try {
            cons = cl.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            fatalError("No parameterless constructor for " + className);
        }

        // 3. Instantiate the set
        Set<String> s = null;
        try {
            s = cons.newInstance();
        } catch (IllegalAccessException e) {
            fatalError("Constructor not accessible");
        } catch (InstantiationException e) {
            fatalError("Class not instantiable");
        } catch (InvocationTargetException e) {
            fatalError("Constructor threw " + e.getCause());
        } catch (ClassCastException e) {
            fatalError("Class " + className + " doesn't implement Set");
        }

        // 4. Exercise the set normally via the interface!
        // Beyond this point, no more reflection is needed.
        s.addAll(Arrays.asList(elements));
        System.out.println("Set implementation: " + className);
        System.out.println("Resulting Set: " + s);
    }

    private static void fatalError(String msg) {
        System.err.println(msg);
        // In a library, throw a custom RuntimeException; in a CLI tool, exit.
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ReflectionToInterface demo = new ReflectionToInterface();
        String[] data = {"apple", "banana", "cherry", "apple"};

        // Example 1: java.util.HashSet (Unordered)
        demo.executeSetAnalysis("java.util.HashSet", data);

        // Example 2: java.util.TreeSet (Alphabetical)
        demo.executeSetAnalysis("java.util.TreeSet", data);

        // Example 3: java.util.LinkedHashSet (Insertion order)
        demo.executeSetAnalysis("java.util.LinkedHashSet", data);
    }
}