package basics.shift;

/**
 * Demonstrates the behavior of the unsigned right shift operator (>>>)
 * in Java for different primitive types: int, long, short, and byte.
 * <p>
 * Key points:
 * - The >>> operator shifts bits to the right, filling with zeros.
 * - For int and long, >>> works directly.
 * - For short and byte, >>> does not exist directly. These types are
 *   promoted to int before the operation, and then cast back.
 * - This promotion can lead to results that appear unchanged when
 *   cast back to short/byte, because truncation discards higher bits.
 */
public class UnsignedRightShift {
    public static void main(String[] args) {
        System.out.println("---/ int /---");
        int i = -1; // binary: 32 ones
        System.out.println("-1 : " + Integer.toBinaryString(i));
        i >>>= 10; // shift right by 10, fill with zeros
        System.out.println(">>>= 10 : " + Integer.toBinaryString(i));

        System.out.println("\n---/ long /---");
        long l = -1; // binary: 64 ones
        System.out.println("-1 : " + Long.toBinaryString(l));
        l >>>= 10; // shift right by 10, fill with zeros
        System.out.println(">>>= 10 : " + Long.toBinaryString(l));

        System.out.println("\n---/ short (same binary string) /---");
        /*
         * short is a 16‑bit signed type.
         * -1 in binary: 1111111111111111
         *
         * >>> does not exist for short directly.
         * Steps when using s >>>= 10:
         * 1. s is promoted to int (32‑bit).
         * 2. Shift performed on int.
         * 3. Result cast back to short.
         *
         * Casting truncates higher bits, often restoring -1.
         */
        short s = -1;
        System.out.println("-1 : " + Integer.toBinaryString(s));
        s >>>= 10;
        System.out.println(">>>= 10 : " + Integer.toBinaryString(s));

        System.out.println("\n---/ byte (same binary string) /---");
        /*
         * byte is an 8‑bit signed type.
         * -1 in binary: 11111111
         *
         * Same promotion rules apply: promoted to int, shifted,
         * then cast back to byte.
         */
        byte b = -1;
        System.out.println("-1 : " + Integer.toBinaryString(b));
        b >>>= 10;
        System.out.println(">>>= 10 : " + Integer.toBinaryString(b));

        System.out.println("\n---/ byte (without casting back from int to byte) /---");
        b = -1;
        System.out.println("b = -1 : " + Integer.toBinaryString(b));
        // Here we explicitly keep the result as int, so we see the actual shift
        System.out.println("b >>> 10 : " + Integer.toBinaryString(b >>> 10));
    }
}