package puzzlers.bloch.exceptional.puzzle1;

/**
 * The Arcane Exception Trio
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 37
 * <p>
 * Case 1: The Specific Catch Error
 * try {
 * System.out.println("Hello world");
 * } catch (java.io.IOException e) { // COMPILE ERROR
 * System.out.println("I've got you now!");
 * }
 * Explanation: Per JLS 11.2.3, you cannot catch a checked exception
 * (except Exception/Throwable) if the try block isn't capable of
 * throwing it. PrintStream.println does not throw IOException.
 * <p>
 * Case 2: The Universal Catch Pass
 * try {
 * // Empty
 * } catch (Exception e) { // COMPILES
 * System.out.println("This is fine.");
 * }
 * Explanation: Catching Exception or Throwable is a special case in
 * the JLS; it is always legal, regardless of the try block's contents.
 * <p>
 * Case 3: The Intersection Rule
 * interface Type1 { void f() throws CloneNotSupportedException; }
 * interface Type2 { void f() throws InterruptedException; }
 * interface Type3 extends Type1, Type2 {}
 * * Explanation: Per JLS 15.12.2.5, when a method is declared in
 * multiple types, the set of exceptions it is allowed to throw is
 * the INTERSECTION of the sets, not the union.
 * Since the intersection of {CloneNotSupportedException} and
 * {InterruptedException} is empty, the method in Type3
 * throws NO checked exceptions at all.
 */

interface Type1 { void f() throws CloneNotSupportedException; }
interface Type2 { void f() throws InterruptedException; }
interface Type3 extends Type1, Type2 {}

public class Arcane3 implements Type3 {
    // This compiles because f() effectively throws nothing
    public void f(){
        System.out.println("Hello world");
    }

    public static void main(String[] args) {
        Type3 t3 = new Arcane3();
        t3.f();
    }
}