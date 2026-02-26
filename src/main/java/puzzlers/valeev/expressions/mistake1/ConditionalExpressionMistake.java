package puzzlers.valeev.expressions.mistake1;

/**
 * Ternary Type Conversion Pitfalls
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 6
 * <p>
 * Problem:
 * Why are ternary expressions not semantically identical to if-statements, particularly
 * when mixed boxed/primitive types trigger unexpected NullPointerExceptions?
 * <p>
 * Expected behavior:
 * The expression should behave like an if-else block where null references are
 * preserved and returned without side effects.
 * <p>
 * Actual behavior:
 * The JVM performs implicit unboxing (e.g., .doubleValue()), causing a
 * NullPointerException if a branch evaluates to null.
 * <p>
 * Explanation:
 * - Expression Classification: Numeric ternaries are "standalone expressions." Unlike
 * "poly expressions" (e.g., Collections.emptyList()), their type is determined
 * solely by their operands, ignoring the assignment context.
 * - Primitive Promotion: In mixed-type ternaries (e.g., Double vs. double), the
 * primitive type "wins," forcing the boxed operand to unbox.
 * - Nesting Logic: The result of an inner ternary becomes an operand for the outer
 * one. If the inner result is a boxed type that meets an outer primitive,
 * unboxing is triggered.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: valueOrZero()
 * - 1. Compiler evaluates Double (value) and double (0.0).
 * - 2. Per JLS 15.25.2, the Double is unboxed to double.
 * - 3. The result is then cast back to the return type (Double).
 * - Case: mapValueBroken()
 * - 1. Inner ternary (input > 10 ? 1 : null) resolves to Integer (1 meets null).
 * - 2. Outer ternary compares this Integer result with primitive 2.
 * - 3. Per JLS, the Integer is unboxed to int. If the inner result was null, .intValue() fails.
 * - Case: mapValueSafe()
 * - 1. Inner ternary (input <= 20 ? 1 : 2) involves only primitives, resolving to int.
 * - 2. Outer ternary compares null with that int, resulting in a boxed Integer (Safe).
 * <p>
 * Fixes:
 * - Avoid mixing primitives and boxed wrappers within a single ternary.
 * - Use if-else or switch expressions when nullability is a factor.
 * - Eagerly convert boxed types to primitives to clarify null-safety.
 * <p>
 * Lesson:
 * - Ternary operators are NOT simple if-else shorthands; they follow strict type-unification.
 * - Mixed-type numeric ternaries are standalone; the surrounding context is ignored.
 * - Avoid branches with differing types; at least one primitive branch can trigger unboxing.
 * <p>
 * Output:
 * - valueOrZero(true, null) -> NullPointerException
 * - mapValueBroken(5) -> NullPointerException
 * - mapValueSafe(5) -> null
 */
public class ConditionalExpressionMistake {

    public Double valueOrZero(boolean condition, Double value) {
        // Explicitly: return Double.valueOf(condition ? value.doubleValue() : 0.0);
        return condition ? value : 0.0;
    }

    public static Integer mapValueBroken(int input) {
        // Explicitly: return Integer.valueOf(input > 20 ? 2 :
        //                                   (input > 10 ? Integer.valueOf(1) : null).intValue());
        return input > 20 ? 2 :
                input > 10 ? 1 :
                        null;
    }

    public static Integer mapValueSafe(int input) {
        // Explicitly: return input <= 10 ? null : Integer.valueOf(input <= 20 ? 1 : 2);
        return input <= 10 ? null :
                input <= 20 ? 1 :
                        2;
    }
}