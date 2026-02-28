package puzzlers.valeev.library.mistake4;

import java.lang.annotation.*;

/**
 * Using getClass() on enums, annotations or Class<?>
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 88
 * <p>
 * Problem:
 * Why does {@code getClass()} return a different value than expected for enums,
 * annotations, or when called on a {@code Class} object itself?
 * <p>
 * Expected behavior:
 * Developers expect {@code getClass()} to return the literal class they defined
 * (e.g., {@code MyEnum.class} or {@code MyAnno.class}).
 * <p>
 * Actual behavior:
 * - Enums with bodies: If an enum constant overrides a method, it becomes an
 * anonymous subclass. {@code getClass()} returns {@code MyEnum$1}, not {@code MyEnum}.
 * - Annotation objects: At runtime, annotations are implemented as dynamic proxies.
 * {@code getClass()} returns a proxy class like {@code $Proxy1}.
 * - Class objects: Calling {@code getClass()} on a {@code Class} variable returns
 * {@code java.lang.Class.class}, not the type the variable represents.
 * <p>
 * Explanation:
 * - The Anonymous Enum Trap: Adding {@code { ... }} to an enum constant creates a
 * subclass. Since {@code getClass()} checks for the **exact** class, comparison
 * to the base Enum class fails.
 * - The Proxy Trap: Reflection uses proxies to implement annotation interfaces.
 * These proxies are generated at runtime and don't match the interface class literal.
 * - The Metadata Trap: Since {@code java.lang.Class} is a Java object, it has its
 * own {@code getClass()} method. Calling it on a class reference is a common
 * logical error in logging/error-reporting.
 * <p>
 *
 *
 * <p>
 * Step-by-step (The Enum Body Failure):
 * - Define {@code enum Color { RED { public String toString() { return "r"; } }, BLUE }}.
 * - {@code Color.BLUE.getClass()} is {@code Color.class}.
 * - {@code Color.RED.getClass()} is an anonymous subclass of {@code Color}.
 * - Result: {@code RED.getClass() == BLUE.getClass()} is {@code false}.
 * <p>
 * Fixes:
 * - Enums: Use {@code getDeclaringClass()} instead of {@code getClass()} to
 * consistently get the Enum type.
 * - Annotations: Use {@code annotationType()} to get the actual annotation interface.
 * - Class Objects: Avoid calling {@code .getClass()} on variables that are
 * already of type {@code Class<?>}. Use the variable directly.
 * - Testing: Assert message content in unit tests to ensure error-reporting
 * logic isn't printing "class java.lang.Class" mistakenly.
 * <p>
 * Lesson:
 * - {@code getClass()} is hyper-specific. For Enums and Annotations, use
 * the specialized methods provided by the JDK.
 * <p>
 * Output:
 * - Enum A Class: class Mistake88$MyEnum$1
 * - Enum A Declaring Class: class Mistake88$MyEnum
 * - Annotation Type: interface Mistake88$MyAnno
 * - Error Message: Wrong type for 'example': class java.lang.Class
 */
@Mistake88.MyAnno
public class Mistake88 {

    // Enum with a constant-specific body
    enum MyEnum {
        A {
            @Override
            public String toString() { return "Customized"; }
        },
        B
    }

    // Runtime Annotation
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyAnno {}

    public static void main(String[] args) {
        // --- Enum Analysis ---
        System.out.println("Enum A Class: " + MyEnum.A.getClass());
        System.out.println("Enum A Declaring Class: " + MyEnum.A.getDeclaringClass());

        // --- Annotation Analysis ---
        MyAnno annotation = Mistake88.class.getAnnotation(MyAnno.class);
        System.out.println("Annotation Class (Proxy): " + annotation.getClass());
        System.out.println("Annotation Type: " + annotation.annotationType());

        // --- The "Class.getClass()" Error reporting Analysis ---
        System.out.println("\n--- Error Reporting Test ---");
        checkType("example", String.class, Integer.class);
    }

    static void checkType(String key, Class<?> actual, Class<?> expectedClass) {
        if (actual != expectedClass) {
            // MISTAKE: actual is already a Class. actual.getClass() returns java.lang.Class.
            String message = "Wrong type for key='" + key + "': " + actual.getClass();
            System.out.println("Error Message: " + message);

            // CORRECT:
            message = "Wrong type for key='" + key + "': " + actual.getName();
            System.out.println("CORRECT Message: " + message);
        }
    }
}