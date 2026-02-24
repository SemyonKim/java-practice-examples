package puzzlers.bloch.advanced.puzzle1;

/**
 * The Literal Exception: Special Case for MIN int and long
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 86
 * <p>
 * Problem:
 * Why does the expression {@code int i = -2147483648;} compile perfectly, while
 * the seemingly identical {@code int j = -(2147483648);} triggers a compilation
 * error stating the "integer number is too large"?
 * <p>
 * Explanation:
 * - In Java, there are technically no negative decimal literals. Negative values
 * are formed by applying the unary minus operator ({@code -}) to a positive literal.
 * - The range of a 32-bit signed {@code int} is $-2^{31}$ to $2^{31} - 1$, or
 * **-2147483648** to **2147483647**.
 * - The positive value $2147483648$ is actually too large to fit in an {@code int}.
 * - To allow the minimum value to be written, JLS §3.10.1 grants a **special exception**:
 * The decimal literal {@code 2147483648} is only allowed as the operand of the
 * unary minus operator {@code -}.
 * - By adding parentheses, you create a subexpression. The compiler must evaluate
 * {@code (2147483648)} independently. Because the literal is no longer the *direct*
 * operand of the unary minus, the special exception no longer applies, and the
 * literal is rejected for being out of range.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Tokenization:** The compiler identifies {@code 2147483648} as a decimal literal.
 * 2. **Context Check:** It checks if the literal is preceded immediately by {@code -}.
 * 3. **The Parenthesis Trap:** In {@code -(2147483648)}, the literal is preceded by
 * an open parenthesis, not a minus sign.
 * 4. **Overflow:** The compiler attempts to parse the value as a positive {@code int}.
 * Since $2147483648 > 2147483647$, it fails before the minus sign is ever considered.
 * <p>
 * The Fix:
 * - If you must use literals for minimum values, never parenthesize the subexpression.
 * - The robust approach is to use {@code Integer.MIN_VALUE} and {@code Long.MIN_VALUE}.
 * <p>
 * Lesson:
 * - Syntactic "sugar" or special compiler exceptions are often fragile.
 * - The smallest negative value in two's complement notation is a "lonely" number
 * because its absolute value has no positive counterpart in the same bit-width.
 */
public class LiteralEdge {
    public static void main(String[] args) {
        // This works: Special case in JLS §3.10.1
        int i = -2147483648;
        long longi = -9223372036854775808L;

        System.out.println("int i: " + i);
        System.out.println("long longi: " + longi);

        // Compilation fails: Integer number too large
        // int j = -(2147483648);

        // Compilation fails: Long number too large
        // long longj = -(9223372036854775808L);
    }
}