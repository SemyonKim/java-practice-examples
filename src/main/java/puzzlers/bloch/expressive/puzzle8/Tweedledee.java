package puzzlers.bloch.expressive.puzzle8;

/**
 * Tweedledee
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005)
 * <p>
 * Based on JLS §15.26.2 (Compound Assignment Operators) and JLS §5.2 (Assignment Conversion).
 * <p>
 * Demonstrates that `x = x + i;` and `x += i;` are not equivalent when reference types are involved.
 * <p>
 * Example:
 *   Object x = "Buy ";
 *   String i = "Effective Java!";
 * <p>
 *   x = x + i;   // Legal
 *   x += i;      // Illegal
 * <p>
 * Lesson:
 *   - Simple assignment (`=`) allows reference conversions (e.g., String → Object).
 *   - Compound assignment (`+=`) inserts an implicit cast back to the left-hand type,
 *     but only for primitive narrowing conversions and string concatenation.
 *   - Reference conversions are not permitted in compound assignment.
 */
public class Tweedledee {
    public static void main(String[] args) {
        Object x = "Buy ";
        String i = "Effective Java!";

        // --- Case 1: Simple assignment ---
        // Expression: x = x + i;
        //
        // Step 1: Evaluate x + i
        //   - x is an Object reference pointing to a String ("Buy ").
        //   - i is a String ("Effective Java!").
        //   - The + operator is defined for String concatenation if either operand is a String.
        //   - So x is converted to String via String.valueOf(x).
        //   - Result: "Buy " + "Effective Java!" = "Buy Effective Java!" (type String).
        //
        // Step 2: Assignment
        //   - Left-hand side type: Object.
        //   - Right-hand side type: String.
        //   - JLS §5.2 Assignment Conversion allows reference conversions:
        //       String is a subtype of Object, so assignment is legal.
        //
        // Therefore, this compiles and runs, and x now refers to the String "Buy Effective Java!".
        x = x + i;
        System.out.println(x); // Prints: Buy Effective Java!

        // --- Case 2: Compound assignment ---
        // Expression: x += i;
        //
        // Defined by JLS §15.26.2:
        //   E1 op= E2
        //   is equivalent to:
        //   E1 = (T)(E1 op E2)
        //   where T is the type of E1.
        //
        // Step 1: Evaluate x + i
        //   - Same as before: "Buy Effective Java!" (type String).
        //
        // Step 2: Implicit cast
        //   - The compiler inserts: (Object)(x + i).
        //   - But here’s the catch: the cast rules for compound assignment
        //     only allow *numeric narrowing conversions* (like int → short),
        //     not general reference conversions.
        //   - JLS §15.26.2 explicitly restricts this: reference conversions
        //     are not applied in compound assignment.
        //
        // Step 3: Assignment
        //   - The compiler sees that the result of (x + i) is String,
        //     and tries to cast it to Object in the compound assignment context.
        //   - This is not permitted under the compound assignment rules.
        //
        // Therefore, this line does not compile.
        //
        // Uncommenting the next line will cause a compile-time error:
        // x += i;
    }
}