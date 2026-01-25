package basics.overloading;

/**
 * Demonstrates that Java does NOT allow overloading
 * methods based only on return type.
 * <p>
 * Both methods below have the same name and parameter list,
 * but different return types. The compiler rejects this with
 * "method f() is already defined".
 * <p>
 * Reason: If you call f() and ignore the return value,
 * the compiler cannot decide which version to use.
 * Therefore, Java requires overloaded methods to differ
 * in their argument lists, not just return type.
 */
public class ReturnTypeOverload {
    // First method: returns nothing
    void f() {
        System.out.println("Called f() with void return");
    }

    // Second method: returns int
    int f() {
        return 1;
    }

    public static void main(String[] args) {
        ReturnTypeOverload obj = new ReturnTypeOverload();

        // Try calling f() without using return value
        obj.f(); // <-- Compiler cannot decide which f() to call
    }
}
