package puzzlers.bloch.exceptional.puzzle3;

/**
 * The Scoping Linkage Paradox
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 44
 * <p>
 * Problem:
 * Why does declaring a variable inside a try block cause an uncaught
 * NoClassDefFoundError, while declaring it outside allows it to be caught?
 * <p>
 * Setup:
 * 1. Compile Missing.java, Strange1.java, and Strange2.java.
 * 2. Delete Missing.class.
 * <p>
 * Example:
 * // Strange1: Declaration INSIDE try
 * try {
 * Missing m = new Missing();
 * } catch (NoClassDefFoundError ex) {
 * System.out.println("Got it!");
 * }
 * * // Strange2: Declaration OUTSIDE try
 * Missing m;
 * try {
 * m = new Missing();
 * } catch (NoClassDefFoundError ex) {
 * System.out.println("Got it!");
 * }
 * <p>
 * Explanation:
 * - This behavior is driven by JVM Linkage (JLS 12.3). The JVM must
 * resolve symbolic references (like the class 'Missing') before they
 * can be used.
 * - **Strange1 (The Crash):** Because 'Missing m' is local to the try
 * block, many JVMs (like HotSpot) treat the resolution of 'Missing'
 * as a prerequisite for entering the try block's scope. If resolution
 * fails, the Error is thrown before the try block is technically
 * active, bypassing the catch.
 * - **Strange2 (The Catch):** By declaring 'm' at the method level,
 * the type is already in the Local Variable Table. The JVM defers
 * the actual resolution of the class until the 'new' bytecode
 * instruction is executed. Since 'new' is inside the try block, the
 * resulting Error is caught.
 * - **Bytecode Difference:** Strange1's bytecode couples type
 * resolution with the start of the try-range, whereas Strange2
 * separates the type definition from the instantiation opcode.
 * <p>
 * Step-by-step:
 * 1. Strange1 attempts to enter the try block.
 * 2. JVM Verifier/Linker tries to resolve 'Missing' to validate the block.
 * 3. Missing.class is gone; NoClassDefFoundError is thrown immediately.
 * 4. Strange2 starts; the try block becomes active first.
 * 5. JVM executes 'new Missing()'; resolution fails *inside* the active block.
 * 6. The Exception Table routes the Error to the catch block.
 * <p>
 * Lesson:
 * - The timing of Linkage and Resolution is implementation-dependent
 * and can be affected by variable scope.
 * - Never rely on catching Errors (like NoClassDefFoundError) within
 * the same method that references the missing type.
 * <p>
 * Safer alternative:
 * Use Reflection to check for class presence:
 * try {
 * Class.forName("Missing");
 * } catch (ClassNotFoundException e) { ... }
 * <p>
 * Output demonstration:
 * Strange1: Exception in thread "main" java.lang.NoClassDefFoundError: Missing
 * Strange2: Got it!
 */
public class Strange2 {
    public static void main(String[] args) {
        Missing m; // Declaration OUTSIDE try
        try{
            m = new Missing();
        } catch (java.lang.NoClassDefFoundError ex){
            System.out.println("Got it!");
        }
    }
}