package puzzlers.valeev.expressions.mistake3;

/**
 * Conditional Operators and Variable Arity Calls
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 10
 * <p>
 * Problem:
 * Why does collapsing an if-else statement into a conditional (ternary) expression change
 * the semantics of a variable arity method call, leading to unexpected array wrapping
 * and hashcode outputs?
 * <p>
 * Expected behavior:
 * The developer expects the refactored ternary expression {@code params.length == 0 ? "user" : params}
 * to behave exactly like the if-else block, where {@code "user"} is wrapped into an array
 * and {@code params} is passed as an existing array.
 * <p>
 * Actual behavior:
 * The method prints the expected string for the empty case but prints a default string
 * representation (hashcode) of an array (e.g., [Ljava.lang.Object;@...) when {@code params} is provided.
 * <p>
 * Explanation:
 * - Difference in if-else branches: In the original method, the {@code then} branch uses
 * a variable arity call style (String), which the compiler automatically wraps into an array.
 * The {@code else} branch passes an {@code Object[]} array explicitly, so no additional wrapping occurs.
 * - Single Expression Type: The compiler cannot wrap only one branch of a ternary expression.
 * It treats the expression as a whole.
 * - Common Supertype: Since the {@code then} branch is {@code String} and the {@code else}
 * branch is {@code Object[]}, the compiler finds the common supertype, which is {@code Object}.
 * - Automatic Re-wrapping: Because the entire expression is now typed as {@code Object}
 * (not an array), the compiler perceives it as a single argument and wraps the already
 * existing {@code params} array into a new one-element array.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: printFormatted("Hello, %s%n", "administrator")
 * - 1. Method receives {@code params} as an {@code Object[]} containing {@code "administrator"}.
 * - 2. Compiler evaluates the ternary: {@code params.length == 0} is false.
 * - 3. The compiler determines the ternary type: {@code String} (then) vs {@code Object[]} (else)
 * results in a result of type {@code Object}.
 * - 4. Because the argument type of {@code printf} is {@code Object...} and the ternary
 * result is a single {@code Object}, the compiler wraps it: {@code new Object[] { params }}.
 * - 5. {@code printf} receives a nested array and prints the hashcode of the inner array.
 * <p>
 * Fixes:
 * - Be careful when using a conditional expression as a variable arity argument; use
 * an if statement instead whenever possible.
 * - If keeping the ternary, manually wrap the alternative branch:
 * {@code params.length == 0 ? new Object[]{"user"} : params}.
 * - Extract the ternary to a variable (e.g., {@code Object adjustedParams}) using
 * IDE refactoring. This makes it visible that the expression type is {@code Object},
 * clarifying that the compiler will wrap it one more time.
 * <p>
 * Lesson:
 * - A conditional expression is perceived as a whole by the compiler, and it must
 * resolve to a single common type.
 * - The type of a ternary expression is not influenced by the varargs wrapping
 * logic of the surrounding method call.
 * - If a ternary expression resolves to a non-array type, the compiler will
 * always wrap the result when passing it to a varargs parameter.
 * <p>
 * Output:
 * - printFormatted("Hello, %s%n") -> Hello, user
 * - printFormatted("Hello, %s%n", "administrator") -> Hello, [Ljava.lang.Object;@...
 * - printFormattedFixed("Hello, %s%n", "administrator") -> Hello, administrator
 */
public class VarargsTernaryMistake {

    static void printFormatted(String formatString, Object... params) {
        // MISTAKE: The ternary result is typed as Object, causing 'params' to be re-wrapped
        // Explicitly: System.out.printf(formatString, new Object[] { params.length == 0 ? "user" : params });
        System.out.printf(formatString,
                params.length == 0 ? "user" : params);
    }

    static void printFormattedFixed(String formatString, Object... params) {
        // FIX: Use explicit if-else to ensure correct wrapping/passing semantics
        if (params.length == 0) {
            System.out.printf(formatString, "user"); // "user" wrapped to Object[1]
        } else {
            System.out.printf(formatString, params); // params passed as-is
        }
    }

    public static void main(String[] args) {
        printFormatted("Hello %s%n");
        printFormatted("Hello, %s%n", "administrator");
        printFormattedFixed("Hello, %s%n", "administrator");
    }
}