package puzzlers.bloch.advanced.puzzle7.client;

import puzzlers.bloch.advanced.puzzle7.library.Words;

/**
 * The Brittle Constant Syndrome: Binary Incompatibility
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 93
 * <p>
 * Problem:
 * After updating a library class to change its {@code public static final}
 * values, why does the client program print a "Frankenstein" mix of the
 * old and new values?
 * <p>
 * Explanation:
 * - According to JLS §13.1, references to **constant variables** are
 * resolved at compile time and "inlined" (copied) directly into the
 * client's bytecode.
 * - A **constant variable** is defined in JLS §4.12.4 as a variable of
 * primitive type or {@code String} that is {@code final} and initialized
 * with a **compile-time constant expression**.
 * - **The Catch:** {@code null} is *not* a compile-time constant expression
 * (JLS §15.28). Therefore, {@code SECOND = null} is not a constant variable.
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Initial Compilation:** Client compiles {@code FIRST} ("the") and
 * {@code THIRD} ("set") directly into its own {@code .class} file. It
 * leaves a symbolic reference to {@code SECOND} because it isn't a constant.
 * 2. **Library Change:** The library is updated. All three are now constants.
 * 3. **Partial Recompilation:** Only the library is recompiled.
 * 4. **Runtime Result:** The client uses its *old inlined values* for
 * {@code FIRST} and {@code THIRD}, but fetches the *new value* for
 * {@code SECOND} from the library.
 * <p>
 *
 * <p>
 * The Fixes:
 * 1. **Non-Constant Initialization:** Use a method like {@code ident(String s)}
 * to initialize the fields. Since a method call is not a constant expression,
 * the compiler must generate a symbolic reference, forcing the client to
 * look up the value at runtime.
 * 2. **Use Enums:** Enum constants are *not* constant variables. You can add,
 * reorder, or change them without breaking binary compatibility or
 * requiring client recompilation.
 * 3. **Full Rebuilds:** Always perform a "clean" build when library
 * signatures or "constants" change.
 * <p>
 * Lesson:
 * - Be extremely careful when exporting {@code public static final}
 * primitives or Strings as part of an API.
 * - If the value might change in a future version, ensure it is not a
 * "constant variable" to prevent binary incompatibility.
 * <p>
 * Output:
 * the chemistry set
 */
public class PrintWords {
    public static void main(String[] args) {
        System.out.println(Words.FIRST + " " + Words.SECOND + " " + Words.THIRD);
    }
}
