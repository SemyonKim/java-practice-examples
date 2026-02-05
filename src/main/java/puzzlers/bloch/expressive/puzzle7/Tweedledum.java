package puzzlers.bloch.expressive.puzzle7;

/**
 * Tweedledum
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Problem:
 *   Why does `x += i;` compile but `x = x + i;` fail when x is a short and i is an int?
 * <p>
 * Example:
 *   short x = 0;
 *   int i = 123456;
 * <p>
 *   x += i;       // compiles
 *   x = x + i;    // does not compile
 * <p>
 * Explanation:
 *   - In Java, arithmetic operators (`+`, `-`, `*`, `/`) always promote operands
 *     smaller than int (byte, short, char) to int before performing the operation.
 *   - So `x + i` is evaluated as `(int)x + i`, producing an int result.
 *   - Assigning that int back to a short requires an explicit cast:
 *       x = (short)(x + i);
 *     Without the cast, the compiler rejects it.
 * <p>
 *   - Compound assignment operators (`+=`, `-=`, `*=`, etc.) are special:
 *     They implicitly cast the result back to the left-hand type.
 *     So `x += i;` is equivalent to:
 *       x = (short)(x + i);
 *     but with the cast inserted automatically by the compiler.
 * <p>
 * Step-by-step:
 *   short x = 0;
 *   int i = 123456;
 * <p>
 *   x = x + i;
 *   expands to:
 *   x = (int)x + i;   // result is int
 *   compiler error: cannot assign int to short without cast
 * <p>
 *   x += i;
 *   expands to:
 *   x = (short)(x + i); // implicit narrowing conversion
 *   compiles successfully
 * <p>
 * Lesson:
 *   - Compound assignment operators perform an implicit cast back to the
 *     left-hand type, which can hide narrowing conversions.
 *   - This can lead to silent truncation of values (e.g., overflow when
 *     assigning large int to short).
 *   - Always be cautious: `x += i;` may compile but can lose data.
 * <p>
 * Safer alternative:
 *   Explicitly cast and document intent:
 *     x = (short)(x + i);
 * <p>
 * Output demonstration:
 *   short x = 0;
 *   int i = 123456;
 *   x += i;
 *   System.out.println(x); // prints -7616 (overflowed short value)
 */
public class Tweedledum {
    public static void main(String[] args) {
        short x = 0;
        int i = 123456;

        // Compiles: implicit cast
        x += i;
        System.out.println("x after x += i: " + x);

        // Uncommenting the next line causes a compile-time error:
        // x = x + i;
    }
}