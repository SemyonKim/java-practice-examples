package puzzlers.bloch.classy.puzzle3;

/**
 * The Instanceof-Cast Contrast
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 50
 * <p>
 * Problem:
 * Understanding the three distinct outcomes of type checking in Java:
 * the "Safe False," the "Compile-Time Guard," and the "Runtime Crash."
 * Why does {@code instanceof} behave so differently from a cast?
 * <p>
 * Explanation:
 * - **Type1 (The Null Rule):** The {@code instanceof} operator is explicitly
 * defined to return {@code false} if the left-hand operand is {@code null}.
 * It acts as a built-in null check, making it safer than methods like
 * {@code .getClass()}.
 * <p>
 * - **Type2 (The Compile-Time Check):** The compiler knows the class
 * hierarchy. Since {@code Type2} and {@code String} are both final
 * classes (or unrelated classes with no subclass relationship possible),
 * it is mathematically impossible for an instance of {@code Type2} to
 * ever be an instance of {@code String}. The compiler catches this
 * "inconvertible types" error early.
 * <p>
 * - **Type3 (The Runtime Check):** In {@code Type3}, a cast is a
 *  directive to the compiler. Since {@code Object} *could* potentially
 *  hold a {@code Type3} instance, the compiler allows it. The JVM
 *  validates this only at runtime, leading to a failure when the
 *  assumption is wrong., and throws a {@link ClassCastException}.
 * <p>
 * The "Inconsistent" Result:
 * - {@code Type1} prints **false**.
 * - {@code Type2} produces a **compile-time error**.
 * - {@code Type3} throws a **runtime exception**.
 * <p>
 * Step-by-step:
 * 1. {@code Type1}: {@code null} is checked against a type. Result: {@code false}.
 * 2. {@code Type2}: Compiler checks if {@code Type2} can ever be a
 * {@code String}. Result: Impossible -> Error.
 * 3. {@code Type3}: Compiler checks if {@code Object} *could* be a
 * {@code Type3}. Result: Possible -> Compiled. JVM checks actual
 * instance at runtime. Result: False -> Exception.
 * <p>
 * Lesson:
 * - {@code instanceof} is a "safe" operator; it returns {@code false} for
 * {@code null} and only fails to compile if types are completely
 * incompatible.
 * - Casting is "unsafe"; it tells the compiler "trust me," which often
 * leads to {@code ClassCastException} at runtime.
 * - If you aren't sure of a type, always use {@code instanceof} before
 * performing a cast.
 * <p>
 * Output:
 * // Type1: false
 * // Type2: (None - Compilation fails)
 * // Type3: java.lang.ClassCastException
 */
public class TypeTests {
    public static void main(String[] args) {
        // Summarized in the Javadoc above.
    }
}

class Type1 {
    public static void main(String[] args) {
        String s = null;
        System.out.println(s instanceof String); // False
    }
}

class Type2 {
    public static void main(String[] args) {
        // Compilation error: Inconvertible types
        //System.out.println(new Type2() instanceof String);
    }
}

class Type3 {
    public static void main(String[] args) {
        // Runtime exception: java.lang.ClassCastException
        Type3 t3 = (Type3) new Object();
    }
}