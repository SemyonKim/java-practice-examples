package generics.patterns;

// Example: Type Inference in Generic Record Patterns is Limited
// Inspired by Cay Horstmann, Core Java Volume 1
//
// This example demonstrates how Java's type inference struggles
// with generic record patterns when checking exhaustiveness in switch expressions.
//
// Key idea: Even though we can define sealed hierarchies of JSON primitives,
// once generics are involved (like Pair<T>), the compiler cannot always prove
// that all cases are covered. This leads to either runtime checks or the need
// for a default clause, even when logically unnecessary.
public class GenericRecordPatterns {

    // Step 1: Define a sealed hierarchy of JSON primitives.
    // Each record implements JSONPrimitive<T> with a specific type parameter.
    sealed interface JSONPrimitive<T> {
    }

    record JSONNumber(double value) implements JSONPrimitive<Double> {
    }

    record JSONBoolean(boolean value) implements JSONPrimitive<Boolean> {
    }

    record JSONString(String value) implements JSONPrimitive<String> {
    }


    // Step 2: A method converting any JSONPrimitive to a number.
    // The switch is exhaustive because the compiler knows there are only three implementations.
    public static <T> double toNumber(JSONPrimitive<T> v) {
        return switch (v) {
            case JSONNumber(var n) -> n;
            case JSONBoolean(var b) -> b ? 1 : 0;
            case JSONString(var s) -> {
                try {
                    yield Double.parseDouble(s);
                } catch (NumberFormatException ex) {
                    yield Double.NaN;
                }
            }
        };
    }


    // Step 3: Define a generic Pair record.
    // This allows us to group two JSONPrimitive values together.
    record Pair<T>(T first, T second) {
        public static <U> Pair<U> of(U first, U second) {
            return new Pair<U>(first, second);
        }
    }


    // Step 4: Attempt to sum two JSON primitives inside a Pair.
    // Problem: The switch is NOT exhaustive because mixed pairs are possible.
    public static Object sum(Pair<? extends JSONPrimitive<?>> pair) {
        return switch (pair) {
            case Pair(JSONNumber(var left), JSONNumber(var right)) -> left + right;
            case Pair(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
            case Pair(JSONString(var left), JSONString(var right)) -> left.concat(right);

            // ERROR: Not exhaustive. Mixed pairs like (JSONNumber, JSONString) are possible.
            default -> throw new AssertionError(); // Try to comment this line!
        };
    }

    // Example of a problematic call:
    // sum(Pair.of(new JSONNumber(42), new JSONString("Fred")));
    // This compiles, but the switch does not handle it exhaustively.


    // Step 5: Restrict Pair to homogeneous JSONPrimitive types.
    // Now only pairs of the same JSONPrimitive type are allowed.
    public static <T extends JSONPrimitive<U>, U> Object sumHomogeneous(Pair<T> pair) {
        return switch (pair) {
            case Pair(JSONNumber(var left), JSONNumber(var right)) -> left + right;
            case Pair(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
            case Pair(JSONString(var left), JSONString(var right)) -> left.concat(right);
            default -> throw new AssertionError();
            // Sadly required: Java cannot prove exhaustiveness even though logically it is.
        };
    }

    // Now, the call below no longer compiles:
    // sumHomogeneous(Pair.of(new JSONNumber(42), new JSONString("Fred")));
    // Because T must be the same JSONPrimitive type for both elements.


    // Step 6: Why explicit type arguments fail.
    // Attempting to match Pair<JSONNumber> directly in the switch causes unsafe casts.
    // The compiler cannot prove that Pair<T> can safely be cast to Pair<JSONNumber>.
//    public static <T extends JSONPrimitive<U>, U> Object sumExplicit(Pair<T> pair) {
//        return switch (pair) {
//            // ERROR: Unsafe casts, compiler rejects these cases.
//            case Pair<JSONNumber>(JSONNumber(var left), JSONNumber(var right)) -> left + right;
//            case Pair<JSONBoolean>(JSONBoolean(var left), JSONBoolean(var right)) -> left | right;
//            case Pair<JSONString>(JSONString(var left), JSONString(var right)) -> left.concat(right);
//        };
//    }
}
/*
 * ------------------------------------------------------------
 * Why Pair<? extends JSONPrimitive<?>> is too broad:
 * ------------------------------------------------------------
 * - The first "?" stands for any subtype of JSONPrimitive (JSONNumber, JSONBoolean, JSONString).
 * - The second "?" is the type parameter of JSONPrimitive<T> (Double, Boolean, String).
 * - This allows mixed pairs, e.g. Pair.of(new JSONNumber(42), new JSONString("Fred")).
 * - Therefore, a switch on Pair<? extends JSONPrimitive<?>> is NOT exhaustive.
 *
 * ------------------------------------------------------------
 * Restriction with <T extends JSONPrimitive<U>, U>:
 * ------------------------------------------------------------
 * - Both elements of the pair must be of the SAME subtype of JSONPrimitive.
 * - Example instantiations:
 *   Pair<JSONNumber> → T = JSONNumber, U = Double
 *   Pair<JSONBoolean> → T = JSONBoolean, U = Boolean
 *   Pair<JSONString> → T = JSONString, U = String
 * - Mixed pairs no longer compile.
 * - However, Java still requires a default case because the compiler cannot prove exhaustiveness
 *   with generics (due to type erasure).
 *
 * ------------------------------------------------------------
 * All Possible Pair Combinations
 * ------------------------------------------------------------
 * | Left Element   | Right Element   | Combination Type | Allowed by Pair<? extends JSONPrimitive<?>> | Allowed by <T extends JSONPrimitive<U>, U> |
 * |----------------|-----------------|------------------|---------------------------------------------|---------------------------------------------|
 * | JSONNumber     | JSONNumber      | Homogeneous      | ✅ Yes                                       | ✅ Yes                                       |
 * | JSONBoolean    | JSONBoolean     | Homogeneous      | ✅ Yes                                       | ✅ Yes                                       |
 * | JSONString     | JSONString      | Homogeneous      | ✅ Yes                                       | ✅ Yes                                       |
 * | JSONNumber     | JSONBoolean     | Mixed            | ✅ Yes                                       | ❌ No                                        |
 * | JSONNumber     | JSONString      | Mixed            | ✅ Yes                                       | ❌ No                                        |
 * | JSONBoolean    | JSONNumber      | Mixed            | ✅ Yes                                       | ❌ No                                        |
 * | JSONBoolean    | JSONString      | Mixed            | ✅ Yes                                       | ❌ No                                        |
 * | JSONString     | JSONNumber      | Mixed            | ✅ Yes                                       | ❌ No                                        |
 * | JSONString     | JSONBoolean     | Mixed            | ✅ Yes                                       | ❌ No                                        |
 * <p>
 * ------------------------------------------------------------
 * Conclusion:
 * ------------------------------------------------------------
 * - Wildcard version allows all 9 combinations → switch not exhaustive.
 * - Homogeneous restriction allows only 3 combinations → mixed pairs rejected at compile time.
 * - Default branch still required because Java cannot prove exhaustiveness with generics.
 */