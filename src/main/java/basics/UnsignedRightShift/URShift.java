package basics.UnsignedRightShift;

public class URShift {
    public static void main(String[] args) {
        System.out.println("---/ int /---");
        int i = -1;
        System.out.println("-1 : " + Integer.toBinaryString(i));
        i >>>= 10;
        System.out.println(">>>= 10 : " + Integer.toBinaryString(i));

        System.out.println("\n---/ long /---");
        long l = -1;
        System.out.println("-1 : " + Long.toBinaryString(l));
        l >>>= 10;
        System.out.println(">>>= 10 : " + Long.toBinaryString(l));

        System.out.println("\n---/ short (same binary string) /---");
        /*
        * short (byte) is a 16‑bit (8-bit) signed type.
        * -1 in binary is 1111111111111111 (11111111).
        *
        * The operator >>> does not exist for short or byte directly.
        * In Java, all arithmetic and bitwise operations on types smaller than int are promoted to int first.
        *
        * So when you write s >>>= 10, the compiler:
        * 1. Promotes s to int (32‑bit).
        * 2. Performs the shift on that int.
        * 3. Casts the result back to short (because of the compound assignment >>>=).
        * */
        short s = -1;
        System.out.println("-1 : " + Integer.toBinaryString(s));
        s >>>= 10;
        System.out.println(">>>= 10 : " + Integer.toBinaryString(s));

        System.out.println("\n---/ byte (same binary string) /---");
        byte b = -1;
        System.out.println("-1 : " + Integer.toBinaryString(b));
        b >>>= 10;
        System.out.println(">>>= 10 : " + Integer.toBinaryString(b));

        System.out.println("\n---/ byte (without casting back from int to byte) /---");
        b = -1;
        System.out.println("b = -1 : " + Integer.toBinaryString(b));
        System.out.println("b >>> 10 : " + Integer.toBinaryString(b >>> 10));
    }
}