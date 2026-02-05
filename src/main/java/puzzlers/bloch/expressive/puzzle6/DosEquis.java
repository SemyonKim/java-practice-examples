package puzzlers.bloch.expressive.puzzle6;

/**
 * Dos Equis
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   What does this program print?
 * <p>
 *   public class DosEquis {
 *       public static void main(String[] args) {
 *           char x = 'X';
 *           int i = 0;
 *           System.out.print(true ? x : 0);
 *           System.out.print(false ? i : x);
 *       }
 *   }
 * <p>
 * Expected output:
 *   "XX"
 * <p>
 * Actual output:
 *   "X88"
 * <p>
 * Explanation:
 *   The puzzle demonstrates how the conditional (ternary) operator `?:`
 *   chooses the type of its result based on operand types.
 * <p>
 *   Rules (simplified from JLS §15.25):
 *   1. If the second and third operands have the same type,
 *      that type is the type of the conditional expression.
 * <p>
 *   2. If one operand is of type T (byte, short, or char)
 *      and the other is an int constant expression whose value
 *      fits in type T, then the type is T.
 * <p>
 *   3. Otherwise, binary numeric promotion is applied,
 *      and the result type is the promoted type of the operands.
 * <p>
 * Step-by-step:
 * <p>
 *   Line 1: System.out.print(true ? x : 0);
 *   - Operands: `x` (char 'X' = 88) and `0` (int constant).
 *   - Rule 2 applies: one operand is char, the other is int constant 0,
 *     which fits in char range [0..65535].
 *   - So the type of the conditional expression is char.
 *   - Result: 'X' is printed → "X".
 * <p>
 *   Line 2: System.out.print(false ? i : x);
 *   - Operands: `i` (int 0) and `x` (char 'X').
 *   - Rule 2 does NOT apply: `i` is not a constant expression.
 *   - Rule 3 applies: binary numeric promotion.
 *   - char is promoted to int, so the whole expression is int.
 *   - The false branch is chosen → `x` promoted to int = 88.
 *   - Printing an int prints its decimal value, not the character.
 *   - Result: "88".
 * <p>
 * Final output:
 *   "X88"
 * <p>
 * Lesson:
 *   - The ternary operator can silently promote types.
 *   - Printing a char vs. printing its int value produces very different results.
 *   - Avoid mixed-type operands in conditional expressions to prevent surprises.
 * <p>
 * Safer alternative:
 *   System.out.print(true ? x : (char)0);
 *   System.out.print(false ? i : (int)x);
 * <p>
 * Or simply use explicit casts or temporary variables to clarify intent.
 */
public class DosEquis {
    public static void main(String[] args) {
        char x = 'X'; // 'X' has Unicode code point 88
        int i = 0;

        // First print: Rule 2 applies, result type is char
        System.out.print(true ? x : 0); // prints 'X'

        // Second print: Rule 3 applies, result type is int
        System.out.print(true ? x : i); // prints 88
    }
}