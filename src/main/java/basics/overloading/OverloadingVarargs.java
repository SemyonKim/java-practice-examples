package basics.overloading;

/**
 * Demonstrates method overloading with varargs in Java.
 * <p>
 * Key points:
 * - Overloading can be based on parameter types, including varargs.
 * - The compiler chooses the most specific applicable overload.
 * - Ambiguity arises when multiple overloads are equally valid.
 * <p>
 * ⚠️ Rule of thumb:
 *   Only use a variable argument list on one version of an overloaded method.
 *   Or consider not doing it at all.
 */
public class OverloadingVarargs {

    /** Accepts Character varargs */
    static void f(Character... args) {
        System.out.print("first");
        for(Character c : args)
            System.out.print(" " + c);
        System.out.println();
    }

    /** Accepts Integer varargs */
    static void f(Integer... args) {
        System.out.print("second");
        for(Integer i : args)
            System.out.print(" " + i);
        System.out.println();
    }

    /** Accepts Long varargs */
    static void f(Long... args) {
        System.out.println("third");
    }

    public static void main(String[] args) {
        // 'a', 'b', 'c' are chars → boxed to Character
        f('a', 'b', 'c'); // calls f(Character...)

        // 1 is int → boxed to Integer
        f(1); // calls f(Integer...)

        // 2, 1 are ints → boxed to Integer
        f(2, 1); // calls f(Integer...)

        // 0 is int → boxed to Integer
        f(0); // calls f(Integer...)

        // 0L is long → boxed to Long
        f(0L); // calls f(Long...)

        // f(); // ❌ Won't compile: ambiguous
        // Both f(Character...) and f(Integer...) are valid for an empty call.
    }
}

/**
 * Example that does NOT compile.
 * <p>
 * Shows ambiguity when overloading with varargs and mixed primitive/boxed types.
 * Both methods below can match certain calls, and neither is more specific.
 */
class OverloadingVarargs2 {

    /** Accepts a float plus Character varargs */
    static void f(float i, Character... args) {
        System.out.println("first");
    }

    /** Accepts only Character varargs */
    static void f(Character... args) {
        System.out.print("second");
    }

    public static void main(String[] args) {
        // 1 (int) → widened to float, 'a' → boxed to Character
        f(1, 'a'); // ✅ unambiguous, calls f(float, Character...)

        // 'a', 'b' (chars) → can be:
        // - widened 'a' → float, 'b' → Character (matches first)
        // - both boxed to Character (matches second)
        // ❌ Compiler error: ambiguous
        f('a', 'b');
    }
}

/**
 * Corrected version of OverloadingVarargs2.
 * <p>
 * Solution: change one overload to use a different parameter list
 * (here, `char` instead of `Character...`), making overload resolution unambiguous.
 */
class OverloadingVarargs3 {

    /** Accepts a float plus Character varargs */
    static void f(float i, Character... args) {
        System.out.println("first");
    }

    /** Accepts a char plus Character varargs */
    static void f(char c, Character... args) {
        System.out.println("second");
    }

    public static void main(String[] args) {
        // 1 (int) → widened to float, 'a' → Character
        f(1, 'a'); // calls f(float, Character...)

        // 'a', 'b' → first 'a' matches char, 'b' → Character
        f('a', 'b'); // calls f(char, Character...)
    }
}