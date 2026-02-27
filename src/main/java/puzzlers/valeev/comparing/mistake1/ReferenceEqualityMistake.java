package puzzlers.valeev.comparing.mistake1;

import java.lang.reflect.Method;

/**
 * Use of Reference Equality instead of the equals Method
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 55
 * <p>
 * Problem:
 * Why does using the == operator for comparing objects like Strings or primitive
 * wrappers (Boolean, Integer) produce inconsistent results, even when their
 * values are identical?
 * <p>
 * Expected behavior:
 * Developers often expect == to compare the contents or values of objects,
 * especially when the JVM caches certain values (like small Integers or String
 * constants).
 * <p>
 * Actual behavior:
 * The == operator checks reference equality (memory addresses). Two objects
 * with the same value can reside at different addresses. For Boolean wrappers
 * created via reflection (pre-Java 18) or Strings created at runtime, ==
 * frequently returns false despite equal values.
 * <p>
 * Explanation:
 * - Reference vs. Value: == compares if two references point to the exact same
 * object in memory. .equals() is designed to compare the actual data.
 * - String Interning: String constants are pooled. However, runtime operations
 * like "a" + "b" (where variables are involved) create new objects on the heap
 * that are not automatically interned.
 * - Reflection "Inflation": In Java versions before 18, reflection (Method.invoke)
 * initially creates new Boolean objects. After a threshold (usually 15-16 calls),
 * it optimizes and starts using cached constants, causing == results to change
 * during execution.
 * - Java 18+ Method Handles: Modern Java reimplements reflection using method
 * handles, which avoid creating new wrapper instances and typically return
 * cached constants.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: String Concatenation
 * - "Hello!" is stored in the String Pool.
 * - greeting + "!" is evaluated at runtime, bypassing the pool and creating a
 * new String object on the heap.
 * - == compares the Pool address vs. the Heap address -> false.
 * - Case: Reflection (Pre-Java 18)
 * - Initial calls to method.invoke() return a new Boolean(true) instance.
 * - After 16 calls, the JVM "inflates" the accessor to a faster bytecode version.
 * - The optimized version uses Boolean.TRUE (cached constant).
 * - == suddenly switches from false to true.
 * <p>
 * Fixes:
 * - Always use .equals() for object comparison (Strings, Wrappers, Enums if necessary).
 * - Avoid using primitive wrappers where primitives (boolean, int) suffice.
 * - If you must use wrappers, unbox them or use Objects.equals(a, b) for null-safety.
 * <p>
 * Lesson:
 * - == is for primitives; .equals() is for objects.
 * - Relying on JVM caching or interning is dangerous because behavior changes
 * based on JVM version, flags, and runtime optimization thresholds.
 * - Reflection is a common source of "hidden" object creation.
 * <p>
 * Output:
 * - "Hello" == "Hello" -> true
 * - greeting2 == "Hello!" -> false
 * - test.invoke() == Boolean.TRUE -> false (Java 17, first call)
 */
public class ReferenceEqualityMistake {

    public static void main(String[] args) throws Exception {
        // String Example
        String greeting = "Hello";
        String greeting2 = greeting + "!";

        System.out.println("--- String Comparison ---");
        System.out.println("Literal vs Literal (Interned): " + (greeting == "Hello"));
        System.out.println("Runtime Concatenation vs Literal: " + (greeting2 == "Hello!"));
        System.out.println("Using .equals(): " + greeting2.equals("Hello!"));

        // Reflection Example
        System.out.println("\n--- Reflection Boolean Comparison ---");
        Method testMethod = ReferenceEqualityMistake.class.getMethod("testValue");

        // In Java 17 and below, this behavior might change after several iterations
        for (int i = 1; i <= 20; i++) {
            Object result = testMethod.invoke(null);
            boolean isSameReference = (result == Boolean.TRUE);

            if (i == 1 || i == 17) {
                System.out.println("Iteration " + i + ": (result == Boolean.TRUE) is " + isSameReference);
            }
        }
    }

    public static boolean testValue() {
        return true;
    }
}