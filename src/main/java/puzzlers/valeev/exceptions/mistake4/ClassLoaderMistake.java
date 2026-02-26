package puzzlers.valeev.exceptions.mistake4;

import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;

/**
 * Different class loaders
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 43, Section 2
 * <p>
 * Problem:
 * The Java Virtual Machine (JVM) identifies a class not just by its fully qualified name,
 * but by the combination of its name AND the ClassLoader that defined it. This can lead
 * to a ClassCastException where a class cannot be cast to itself.
 * <p>
 * Expected behavior:
 * Developers expect that if two objects have the same class name (e.g., {@code MyClass}),
 * they are compatible and can be cast to one another.
 * <p>
 * Actual behavior:
 * - Runtime Identity: {@code MyClass} loaded by {@code L1} classloader is a completely different type
 * than {@code MyClass} loaded by {@code L2} classloader.
 * - Confusing Errors: The JVM throws a {@code ClassCastException} stating:
 * "class MyClass cannot be cast to class MyClass".
 * - Instanceof Failure: The {@code instanceof} operator returns {@code false} even if
 * the names match perfectly.
 * <p>
 * Explanation:
 * - This issue typically arises in complex systems with modular architectures (OSGi,
 * plugins, or web servers).
 * - Each module may use its own {@code ClassLoader} to isolate dependencies.
 * - If a class is loaded by a custom loader that doesn't delegate correctly to the
 * parent loader, the JVM creates a new runtime type, causing a "split" identity.
 * <p>
 * Ways to Avoid:
 * - Correct Parent Delegation: When creating a {@code URLClassLoader}, ensure the
 * parent is set to {@code MyClass.class.getClassLoader()} rather than the System
 * loader. This ensures existing application classes are reused rather than reloaded.
 * - Mind Module Boundaries: Be cautious when passing objects between different
 * plugins. If both plugins load the same library independently, the types will be
 * incompatible.
 * - Use Reflection as a Last Resort: If you must interact with an object from
 * another loader, store it as an {@code Object} and call methods via
 * {@code getMethod().invoke()}.
 * <p>
 * Lesson:
 * - A class's identity is {@code (ClassLoader, PackageName, ClassName)}.
 * - Always check the "loader" suffix in modern JVM error messages to debug
 * casting issues.
 */
public class ClassLoaderMistake {

    // --- Example 1: Triggering the Split Identity ---
    public void demonstrateDifferentLoaders() throws Exception {
        // URL pointing to the folder where this compiled class is located
        URL url = ClassLoaderMistake.class.getResource(".");

        // Using the System/Bootstrap loader as parent means this loader
        // won't "see" our current application classes.
        ClassLoader parent = System.class.getClassLoader();

        try (URLClassLoader newLoader = new URLClassLoader(new URL[]{url}, parent)) {
            // Load the same class name again via the new loader
            Class<?> anotherClass = newLoader.loadClass("ClassLoaderMistake");
            Object o = anotherClass.getConstructor().newInstance();

            System.out.println("\n##====Different classloaders====##");
            System.out.println("Class Name: " + o.getClass().getName());
            System.out.println("Loader: " + o.getClass().getClassLoader());
            System.out.println("Current Loader: " + this.getClass().getClassLoader());

            // This returns false because the Class objects are different
            System.out.println("Instance of check: " + (o instanceof ClassLoaderMistake));

            try {
                // This will throw ClassCastException
                ClassLoaderMistake casted = (ClassLoaderMistake) o;
            } catch (ClassCastException e) {
                System.out.println("Caught Expected Error: " + e.getMessage());
            }
            System.out.println("\n##====Reflection====##");
            reflectionWorkaround(o);
        }
    }

    // --- Example 2: The Reflection Workaround ---
    public void reflectionWorkaround(Object foreignObject) {
        try {
            // Even if we can't cast it, we can still talk to it
            Method method = foreignObject.getClass().getMethod("helperMethod");
            method.invoke(foreignObject);
        } catch (Exception e) {
            System.out.println("Reflection failed: " + e.getMessage());
        }
    }

    public void helperMethod() {
        System.out.println("Method invoked successfully!");
    }

    // --- Example 3: Correct Delegation ---
    public void correctLoaderUsage() throws Exception {
        URL url = ClassLoaderMistake.class.getResource(".");

        // FIX: Use the current class's loader as the parent
        ClassLoader correctParent = ClassLoaderMistake.class.getClassLoader();

        try (URLClassLoader fixedLoader = new URLClassLoader(new URL[]{url}, correctParent)) {
            Class<?> loadedClass = fixedLoader.loadClass("ClassLoaderMistake");
            Object o = loadedClass.getConstructor().newInstance();

            // Now this returns true because the loader delegates to the parent
            System.out.println("\n##====Fixed====##");
            System.out.println("Instance of with correct parent: " + (o instanceof ClassLoaderMistake));
        }
    }

    public static void main(String[] args) throws Exception {
        ClassLoaderMistake mistake = new ClassLoaderMistake();
        mistake.demonstrateDifferentLoaders();
        mistake.correctLoaderUsage();
    }
}