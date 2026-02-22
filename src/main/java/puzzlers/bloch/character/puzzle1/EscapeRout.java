package puzzlers.bloch.character.puzzle1;

/**
 * EscapeRout
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 14
 * <p>
 * Problem:
 * What does the expression `"a\u0022.length() + \u0022b".length()` evaluate to?
 * <p>
 * Example:
 * System.out.println("a\u0022.length() + \u0022b".length());
 * <p>
 * Explanation:
 * - Java processes Unicode escapes (\ uXXXX) very early in the compilation
 * process, even before the code is parsed into tokens or strings.
 * - The Unicode escape `\ u0022` represents the double-quote character (`"`).
 * - The compiler replaces every occurrence of `\ u0022` with an actual
 * double-quote before evaluating the logic of the string.
 * <p>
 * Step-by-step:
 * Original code:
 * "a\u0022.length() + \u0022b".length()
 * <p>
 * After Unicode translation (Step 1):
 * "a".length() + "b".length()
 * <p>
 * Evaluation (Step 2):
 * 1 + 1
 * <p>
 * Result:
 * 2
 * <p>
 * Lesson:
 * - Unicode escapes are not the same as standard escape sequences (like `\"`).
 * - Standard escapes are processed after the program is parsed into tokens,
 * keeping them safely inside a string literal.
 * - Unicode escapes are processed so early they can change the structure of
 * the code, effectively "escaping" the string and inserting actual syntax.
 * - Avoid using Unicode escapes for characters that have special meaning
 * in Java (quotes, line feeds, etc.) unless absolutely necessary.
 * <p>
 * Safer alternative:
 * Use standard escape sequences to include quotes inside a string:
 * "a\".length() + \"b".length() // Evaluates to 16
 * <p>
 * Output demonstration:
 * System.out.println("a\u0022.length() + \u0022b".length());
 * // Prints: 2
 */
public class EscapeRout {
    public static void main(String[] args) {
        // This looks like one string, but it is actually two strings being added.
        System.out.println("a\u0022.length() + \u0022b".length());
    }
}