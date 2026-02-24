package puzzlers.bloch.classier.puzzle3;

/**
 * The Obscuring Redux: Variable Precedence Over Type
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 68
 * <p>
 * Problem:
 * When the expression {@code X.Y.Z} is evaluated, does it refer to the
 * static field {@code Z} inside the nested class {@code Y}, or the
 * instance field {@code Z} of the static object {@code Y}?
 * <p>
 * Explanation:
 * - This is a classic case of **Obscuring**. Obscuring occurs when a
 * name justifies multiple meanings in the same scope (e.g., a type
 * name and a variable name).
 * - According to the Java Language Specification (JLS §6.5.2), when
 * resolving a qualified name, a **variable name takes precedence
 * over a type name**.
 * - In the expression {@code X.Y}, the compiler finds two members
 * named {@code Y} in class {@code X}: a nested class and a static field.
 * - Because variables win the "priority war," {@code X.Y} is resolved
 * as the static field of type {@code C}. The nested class {@code Y}
 * is effectively obscured.
 * - Consequently, {@code X.Y.Z} accesses the field {@code Z} of the
 * object stored in the field {@code Y}.
 * <p>
 *
 * <p>
 * Step-by-step (The Resolution):
 * 1. **Identify X:** The compiler identifies the class {@code X}.
 * 2. **Evaluate Y:** Inside {@code X}, it sees both {@code class Y}
 * and {@code C Y}. Per JLS rules, it picks the variable {@code Y}.
 * 3. **Access Z:** It then looks for {@code Z} within the type of
 * the variable {@code Y} (which is class {@code C}).
 * 4. **Result:** It finds {@code "White"}.
 * <p>
 * Lesson:
 * - Never give a type (class/interface) and a variable (field/parameter)
 * the same name in the same scope.
 * - Follow standard naming conventions: variables should start with
 * lowercase letters (e.g., {@code static C y = new C();}) to
 * distinguish them from PascalCase types.
 * <p>
 * Output:
 * White
 */
public class ShadesOfGray {
    public static void main(String[] args) {
        // X.Y refers to the field, not the nested class
        System.out.println(X.Y.Z);
    }
}

class X {
    // This class is obscured by the field below
    static class Y {
        static String Z = "Black";
    }

    // Variables take precedence over types
    static C Y = new C();
}

class C {
    String Z = "White";
}