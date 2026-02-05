package puzzlers.bloch.expressive.puzzle5;

/**
 * Multicast
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   What does the following expression print?
 *     System.out.println((int)(char)(byte)-1);
 * <p>
 * Expected behavior:
 *   Developers might expect the value to remain -1 throughout the casts.
 * <p>
 * Actual behavior:
 *   The output is 65535.
 * <p>
 * Explanation with binary detail:
 * <p>
 *   1. Literal -1
 *      - Type: int (default for integer literals)
 *      - Hex: 0xFFFFFFFF
 *      - Decimal: -1
 *      - Binary (32 bits): 11111111 11111111 11111111 11111111
 * <p>
 *   2. Cast to byte
 *      - Narrow to 8 bits → 0xFF
 *      - Decimal: -1
 *      - Binary (8 bits): 11111111
 * <p>
 *   3. Cast to char
 *      - Rule: byte is promoted to int (sign-extended) before narrowing.
 *      - So -1 as int = 0xFFFFFFFF.
 *      - Narrow to char (16 bits): lower 16 bits = 0xFFFF.
 *      - Char is unsigned → interpreted as 65535.
 *      - Binary (16 bits): 11111111 11111111
 * <p>
 *   4. Cast to int
 *      - Char to int uses zero extension.
 *      - 0xFFFF → 0x0000FFFF
 *      - Decimal: 65535
 *      - Binary (32 bits): 00000000 00000000 11111111 11111111
 * <p>
 *   Final result:
 *      Prints 65535.
 * <p>
 * Extra notes:
 *   - Sign extension is performed if the original type is signed.
 *   - Zero extension is performed if the original type is char.
 * <p>
 * Examples of controlling sign vs. zero extension:
 * <p>
 *   1. char -> int without sign extension:
 *      Masking with 0xFFFF (binary: 11111111 11111111) ensures that only
 *      the lower 16 bits of the char are kept, and the upper 16 bits of the
 *      resulting int are filled with zeros. This guarantees a non-negative
 *      result in the range [0, 65535].
 *          int i = c & 0xFFFF;   // zero extension
 * <p>
 *   2. char -> int with sign extension:
        Casting to short first interprets the 16-bit char as a signed value.
 *      Then widening to int sign-extends that signed short, filling the
 *      upper 16 bits with the sign bit. This can produce negative values
 *      in the range [-32768, 32767].
 *          int i = (short) c;    // sign extension
 * <p>
 *   3. byte -> char without sign extension:
 *      Masking with 0xFF (binary: 11111111) ensures that the byte’s lower
 *      8 bits are treated as an unsigned value. When widened to char (16-bit),
 *      the upper 8 bits are filled with zeros. Result is always in [0, 255].
 *          char c = (char) (b & 0xFF); // zero extension
 * <p>
 *   4. byte -> char with sign extension:
 *      Casting directly from byte to char first promotes the byte to int
 *      with sign extension (so -1 becomes 0xFFFFFFFF), then narrows to char,
 *      keeping the lower 16 bits. That yields 0xFFFF = 65535 for -1.
 *      In general, negative bytes become large positive chars.
 *          char c = (char) b; // sign extension
 * <p>
 * Key takeaway:
 *   - Be explicit about whether you want sign extension or zero extension.
 *   - Use bitmask (& 0xFF or & 0xFFFF) when you want explicit zero extension.
 *   - Use casts through signed types (short, int) when you want sign extension.
 *   - Zero extension keeps values non-negative; sign extension preserves the
 *     “negative” interpretation of the original signed type.
 */
public class Multicast {

    public static void main(String[] args) {
        // Puzzle expression
        System.out.println("Puzzle result: " + (int)(char)(byte)-1);

        // Step-by-step demonstration
        int i = -1; // 0xFFFFFFFF → 11111111 11111111 11111111 11111111
        byte b = (byte) i;   // 0xFF → 11111111 (8 bits) = -1
        char c = (char) b;   // 0xFFFF → 11111111 11111111 (16 bits) = 65535
        int j = (int) c;     // 0x0000FFFF → 00000000 00000000 11111111 11111111 = 65535
        System.out.println("Step-by-step result: " + j);

        // Demonstrating explicit control
        char cZeroExt = (char) (b & 0xFF); // 0x00FF → 255
        char cSignExt = (char) b;          // 0xFFFF → 65535
        System.out.println("byte to char (zero extension): " + (int)cZeroExt);
        System.out.println("byte to char (sign extension): " + (int)cSignExt);

        // Char to int examples
        char cExample = '\uFFFF'; // 0xFFFF → 11111111 11111111 = 65535
        int intZeroExt = cExample & 0xFFFF; // 0x0000FFFF → 65535
        int intSignExt = (short) cExample;  // 0xFFFF → -1
        System.out.println("char to int (zero extension): " + intZeroExt);
        System.out.println("char to int (sign extension): " + intSignExt);
    }
}