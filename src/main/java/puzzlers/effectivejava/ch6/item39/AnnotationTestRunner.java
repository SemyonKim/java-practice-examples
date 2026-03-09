package puzzlers.effectivejava.ch6.item39;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <h2>Prefer annotations to naming patterns</h2>
 *
 * <p>
 * <b>Core Principle:</b> Avoid using naming patterns (e.g., prefixing methods with "test")
 * to indicate special treatment by tools. Instead, define and use proper annotation types
 * to provide metadata that the compiler can validate and tools can process via reflection.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Compile-time Safety:</b> Typographical errors in annotations result in compilation errors,
 * whereas typos in naming patterns (e.g., "tset" instead of "test") lead to silent failures.</li>
 * <li><b>Element Targeting:</b> Meta-annotations like {@code @Target} ensure that metadata is only
 * applied to appropriate program elements (e.g., methods only, not classes).</li>
 * <li><b>Rich Parameterization:</b> Annotations allow for the association of complex parameter
 * values (like class literals or arrays) with program elements in a type-safe manner.</li>
 * <li><b>Repeatability:</b> Since Java 8, {@code @Repeatable} annotations allow multiple instances
 * of the same metadata to be applied cleanly to a single element.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Runtime Processing Overhead:</b> Annotations generally require reflection to be processed
 * at runtime, which can have a slight performance impact.</li>
 * <li><b>Complexity in Repeatables:</b> Processing repeatable annotations is error-prone because
 * {@code isAnnotationPresent} may return {@code false} if a repeated annotation is wrapped in its container.</li>
 * <li><b>No Native Logic Enforcement:</b> The compiler cannot natively enforce complex constraints
 * (e.g., "must be a static method") without a custom annotation processor.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item27 EliminateUncheckedWarnings
 * @see puzzlers.effectivejava.ch5.item33 BoundedTypeTokens
 * @see puzzlers.effectivejava.ch6.item40 UseOverrideConsistently
 * @see puzzlers.effectivejava.ch9.item62 AvoidStringsWhereOtherTypesAreBetter
 */
public class AnnotationTestRunner {

    // 1. Marker Annotation Type
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test {}

    // 2. Annotation with Parameter (Bounded Type Token)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(ExceptionTestContainer.class)
    public @interface ExceptionTest {
        Class<? extends Throwable> value();
    }

    // 3. Container for Repeatable Annotation
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTestContainer {
        ExceptionTest[] value();
    }

    // --- Implementation Details (Sample Class to Test) ---

    public static class Sample {
        @Test public static void m1() {} // Should pass

        @Test public static void m2() { // Should fail
            throw new RuntimeException("Boom");
        }

        @Test public void m3() {} // INVALID USE: non-static

        @ExceptionTest(ArithmeticException.class)
        public static void m4() { // Should pass
            int i = 1 / 0;
        }

        @ExceptionTest(IndexOutOfBoundsException.class)
        @ExceptionTest(NullPointerException.class)
        public static void doublyBad() { // Repeatable test
            List<String> list = new ArrayList<>();
            list.addAll(5, null);
        }
    }

    // --- Client Usage (The Tool/Framework) ---

    public static void main(String[] args) {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Sample.class;

        for (Method m : testClass.getDeclaredMethods()) {
            // Processing Marker @Test
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    System.out.println(m.getName() + " failed: " + wrappedExc.getCause());
                } catch (Exception exc) {
                    System.out.println("Invalid @Test (e.g. non-static): " + m.getName());
                }
            }

            // Processing Repeatable @ExceptionTest
            if (m.isAnnotationPresent(ExceptionTest.class) || m.isAnnotationPresent(ExceptionTestContainer.class)) {
                tests++;
                try {
                    m.invoke(null);
                    System.out.println(m.getName() + " failed: no exception");
                } catch (Throwable wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    int oldPassed = passed;
                    ExceptionTest[] excTests = m.getAnnotationsByType(ExceptionTest.class);
                    for (ExceptionTest excTest : excTests) {
                        if (excTest.value().isInstance(exc)) {
                            passed++;
                            break;
                        }
                    }
                    if (passed == oldPassed) {
                        System.out.println(m.getName() + " failed: unexpected exception " + exc);
                    }
                }
            }
        }
        System.out.printf("Passed: %d, Failed: %d%n", passed, tests - passed);
    }
}