package puzzlers.bloch.character.puzzle2;

/**
 * Line Printer
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 16
 * <p>
 * Problem:
 * Why does a seemingly harmless comment containing a Unicode escape
 * cause a compile-time error or unexpected behavior?
 * <p>
 * Example:
 * // Note: \ u000A is a line feed
 * <p>
 * Explanation:
 * - As seen in the "Unicode Preprocessing Trap," Java translates all
 * Unicode escapes (`\ uXXXX`) into their actual characters as the very
 * first step of compilation.
 * - The Unicode escape `\ u000A` represents the Line Feed (LF) character.
 * - When the compiler sees `\ u000A`, it replaces it with a literal
 * new line, regardless of whether it is inside a comment or a string.
 * <p>
 * Step-by-step:
 * Original code:
 * // Note: \u000A is a line feed
 * <p>
 * After Unicode translation (Step 1):
 * // Note:
 * is a line feed
 * <p>
 * Result:
 * The words "is a line feed" are now on a new line and are no longer
 * part of the comment. Since "is a line feed" is not valid Java
 * syntax, the compiler throws an error.
 * <p>
 * Lesson:
 * - Unicode escapes are processed BEFORE comments are discarded.
 * - You can accidentally "execute" a comment or break your code by
 * including Unicode for line terminators (`\ u000A` or `\ u000D`).
 * - This can be used maliciously to hide code in comments or
 * accidentally via automated tools that generate Unicode.
 * <p>
 * Safer alternative:
 * Never use Unicode escapes for formatting characters. Simply write
 * the comment in plain text:
 * // Note: The following is a line feed character
 * <p>
 * Output demonstration:
 * // \u000A System.out.println("Executed from a comment!");
 * // The above line actually prints the message because the
 * // println is moved to a new line by the compiler.
 */
public class LineFeedInjection {
    public static void main(String[] args) {
        // The following line will actually execute!
        // \u000A System.out.println("This is not a comment anymore.");
    }
}