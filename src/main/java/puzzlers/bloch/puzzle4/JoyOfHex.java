package puzzlers.bloch.puzzle4;

/**
 * Joy of Hex
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   Consider the expression:
 *     System.out.println(Long.toHexString(0x100000000L + 0xcafebabe));
 * <p>
 *   What hexadecimal string will it print?
 * <p>
 * Expected behavior:
 *   Developers might expect the result to be:
 *     0x1cafebabe
 *   because 0x100000000L + 0xcafebabe = 0x1cafebabe.
 * <p>
 * Actual behavior:
 *   The output is:
 *     cafebabe
 * <p>
 *   Why?
 *   - Hexadecimal literals are base‑16. Each digit represents 4 bits.
 *   - 0xCAFEBABE expands as:
 *       C × 16^7 + A × 16^6 + F × 16^5 + E × 16^4 + B × 16^3 + A × 16^2 + B × 16^1 + E × 16^0
 *     = 3,405,691,582 in decimal.
 * <p>
 *   - By default, hex literals without a suffix are `int` (32‑bit signed).
 *   - The maximum positive int is 2,147,483,647. Since 3,405,691,582 > MAX_VALUE,
 *     the bit pattern is interpreted as a negative number using two’s complement:
 *       3,405,691,582 − 2^32 = −889,275,714
 * <p>
 *   - The literal `0xcafebabe` is therefore an `int` constant with value −889,275,714.
 * <p>
 *   - When added to 0x100000000L (a long literal equal to 4,294,967,296 decimal):
 *       4,294,967,296 + (−889,275,714) = 3,405,691,582
 * <p>
 *   - In hex, 3,405,691,582 = 0xCAFEBABE.
 *     But because of the signed interpretation, the arithmetic path is different
 *     from the naive expectation of 0x1CAFEBABE.
 * <p>
 *   - `Long.toHexString` prints the lower 32 bits of the result, which appear as "cafebabe".
 * <p>
 * Lesson:
 *   - Hex literals without a suffix default to `int`.
 *   - If the value exceeds `Integer.MAX_VALUE`, it is treated as a negative int.
 *   - Adding to a `long` promotes the int, but the sign is preserved.
 * <p>
 * Key takeaway:
 *   Always use the `L` suffix for large hex literals intended as `long`.
 *   Example:
 *     Long.toHexString(0x100000000L + 0xcafebabeL) // prints "1cafebabe"
 */
public class JoyOfHex {

    public static void main(String[] args) {
        // Naive expression (int literal for cafebabe)
        System.out.println("Naive: " + Long.toHexString(0x100000000L + 0xcafebabe));

        // Correct expression (long literal for cafebabe)
        System.out.println("Correct: " + Long.toHexString(0x100000000L + 0xcafebabeL));

        // Show values explicitly
        int cafebabeInt = 0xcafebabe;
        long cafebabeLong = 0xcafebabeL;
        System.out.println("cafebabe as int: " + cafebabeInt);
        System.out.println("cafebabe as long: " + cafebabeLong);

        long naiveResult = 0x100000000L + cafebabeInt;
        long correctResult = 0x100000000L + cafebabeLong;
        System.out.println("Naive result (decimal): " + naiveResult);
        System.out.println("Correct result (decimal): " + correctResult);
    }
}