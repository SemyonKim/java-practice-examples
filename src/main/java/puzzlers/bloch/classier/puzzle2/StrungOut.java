package puzzlers.bloch.classier.puzzle2;

/**
 * The Closest Class Definition Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 67
 * <p>
 * Problem:
 * Why does this program fail to run with a {@code NoSuchMethodError: main}
 * or "Main method not found", even though the {@code main} method is
 * clearly defined?
 * <p>
 * Explanation:
 * - The JVM looks for a very specific signature to start a program:
 * {@code public static void main(java.lang.String[] args)}.
 * - In this file, a custom class named {@code String} is defined in the
 * same package.
 * - When the compiler sees {@code main(String[] args)}, it uses the
 * "closest" definition of {@code String}, which is the custom class
 * defined below, not {@code java.lang.String}.
 * - Consequently, the signature of the {@code main} method becomes
 * {@code main(com.puzzlers.String[] args)}.
 * - To the JVM, this is just a random method; it is not the official
 * entry point of the application.
 * <p>
 * Step-by-step (The Confusion):
 * 1. **Shadowing/Hiding:** By naming a class {@code String}, you shadow
 * the standard {@code java.lang.String} within that package.
 * 2. **Binding:** The compiler binds the {@code args} parameter to your
 * custom {@code String} class.
 * 3. **Execution Failure:** When you try to run the class, the Launcher
 * looks for a method taking an array of {@code java.lang.String}. It
 * finds nothing and throws an error.
 * <p>
 * The Fix:
 * - The most obvious fix is to rename the custom class to something
 * like {@code MyString}.
 * - If you absolutely must use the name {@code String}, you must
 * explicitly qualify the parameter in the main method:
 * {@code public static void main(java.lang.String[] args)}.
 * <p>
 * Lesson:
 * - Avoid reusing the names of types in {@code java.lang}. It leads to
 * "Type Name Hiding" which is confusing and brittle.
 * - Be especially careful with {@code String}, {@code Object},
 * {@code System}, and {@code Exception}.
 * <p>
 * Output:
 * // Error: Main method not found in class StrungOut
 */
public class StrungOut {
    // The compiler thinks 'String' here refers to the class defined below
    public static void main(String[] args) {
        System.out.println("Hello World");
    }

    //The proper main method; Explicitly qualified class name
//    public static void main(java.lang.String[] args) {
//        System.out.println("Hello World");
//    }
}

class String {
    private final java.lang.String s;

    public String(java.lang.String s) {
        this.s = s;
    }

    public java.lang.String toString() {
        return s;
    }
}