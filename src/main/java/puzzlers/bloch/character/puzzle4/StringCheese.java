package puzzlers.bloch.character.puzzle4;

/**
 * String Cheese
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 18
 * <p>
 * Problem:
 * When converting a byte array containing all possible values (0-255) into
 * a String, will printing the integer value of each character yield
 * the original 0-255 sequence?
 * <p>
 * Explanation:
 * - The code attempts to treat raw bytes as characters.
 * - Because `new String(b)` uses the default charset, it doesn't just
 * copy the numbers; it "interprets" them.
 * - In common charsets like UTF-8, high-bit bytes (128-255) are often
 * invalid as standalone characters.
 * - The JVM replaces these invalid sequences with the Unicode replacement
 * character `\uFFFD`, which has a numeric value of **65533**.
 * <p>
 * Step-by-step:
 * 1. Byte array `b` is filled with values -128 to 127 (cast from 0-255).
 * 2. `new String(b)` is called. The JVM encounters bytes it cannot map.
 * 3. These "unmappable" bytes are converted to the character `\uFFFD`.
 * 4. `(int)s.charAt(i)` prints **65533** for those positions instead
 * of the original byte value.
 * <p>
 * Lesson:
 * - Do not assume a 1:1 mapping between byte values and character values.
 * - Printing `(int)charAt(i)` reveals how the charset has mangled or
 * reorganized the underlying data.
 * - Always specify an encoding like `StandardCharsets.ISO_8859_1` if
 * you need a predictable 1:1 mapping for the first 256 characters.
 * <p>
 * Output demonstration:
 * // On a UTF-8 system, the output will start correctly (0, 1, 2...)
 * // but will eventually start printing "65533" repeatedly for
 * // the invalid byte sequences.
 */
public class StringCheese {
    public static void main(String[] args) {
        byte[] b = new byte[256];
        for (int i = 0; i < 256; i++) {
            b[i] = (byte)i;
        }
        String s = new String(b);
        for (int i = 0, n = s.length(); i < n; i++) {
            System.out.print((int)s.charAt(i) + " ");
        }
    }
}