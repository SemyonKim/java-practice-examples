package puzzlers.bloch.loopy.puzzle1;

/**
 * The Bitwise Masking Requirement
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 24
 * <p>
 * Problem:
 * How can we correctly compare a signed `byte` to a hexadecimal
 * literal that represents an unsigned value?
 * <p>
 * Example:
 * byte b = (byte)0x90;
 * if ((b & 0xFF) == 0x90) { ... }
 * <p>
 * Explanation:
 * - In Java, bytes are signed (-128 to 127). The hex literal `0x90` (144)
 * exceeds the range of a signed byte, so `(byte)0x90` becomes -112.
 * - When you perform `b & 0xFF`, two things happen:
 * 1. `b` is promoted to an `int` via sign extension (becoming 0xFFFFFF90).
 * 2. The bitwise AND (`&`) with `0xFF` (0x000000FF) "masks" out the
 * leading 24 bits.
 * - This operation clears the sign-extension bits, turning the negative
 * `int` back into a positive value (144) that matches the literal `0x90`.
 * <p>
 * Step-by-step:
 * 1. b = 10010000 (as a byte, this is -112)
 * 2. b promoted to int: 11111111 11111111 11111111 10010000
 * 3. Mask (0xFF):      00000000 00000000 00000000 11111111
 * 4. Result of &:      00000000 00000000 00000000 10010000 (which is 144)
 * 5. Comparison: 144 == 144 is true.
 * <p>
 * Lesson:
 * - Always use `& 0xFF` when converting a `byte` to a larger
 * integer type if you want to treat the byte as an unsigned value.
 * - This is essential for bitwise processing, cryptography, and
 * network protocols.
 * <p>
 * Output demonstration:
 * byte b = (byte)0x90;
 * System.out.println(b == 0x90);        // Prints false
 * System.out.println((b & 0xFF) == 0x90); // Prints true
 */
public class BitwiseMasking {
    public static void main(String[] args) {
        byte b = (byte)0x90;

        // Correct way to handle 'unsigned' byte comparison
        if ((b & 0xFF) == 0x90) {
            System.out.println("The byte matches 0x90 (144)!");
        }
    }
}