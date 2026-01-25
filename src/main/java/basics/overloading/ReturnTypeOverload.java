package basics.overloading;

/**
 * Demonstrates that Java does NOT allow method overloading
 * based only on return type.
 * <p>
 * Both methods below have the same name and parameter list,
 * but different return types. The compiler rejects this with:
 * <p>
 *   error: method f() is already defined in class ReturnTypeOverload
 * <p>
 * Reason:
 * - Overloading in Java must differ by parameter list, not return type.
 * - If you call f() and ignore the return value, the compiler cannot
 *   decide which version to use.
 * - Therefore, this code will not compile unless one of the methods
 *   is commented out.
 */
public class ReturnTypeOverload {

    // ❌ Invalid: two methods with same name and parameters but different return types
    // void f() {
    //     System.out.println("Called f() with void return");
    // }

    // int f() {
    //     return 1;
    // }

    // ✅ Correct approach: give them different names or parameter lists
    void fVoid() {
        System.out.println("Called fVoid()");
    }

    int fInt() {
        return 1;
    }

    public static void main(String[] args) {
        ReturnTypeOverload obj = new ReturnTypeOverload();

        // Demonstrating the valid methods
        obj.fVoid();              // prints message
        int result = obj.fInt();  // returns 1
        System.out.println("fInt() returned: " + result);

        // ❌ Uncommenting the invalid methods above will cause a compiler error
        // obj.f(); // ambiguous, compiler cannot decide
    }
}

