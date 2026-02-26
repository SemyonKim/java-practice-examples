package puzzlers.valeev.expressions.mistake2;

import java.util.List;

/**
 * Incorrectly Using Variable Arity Calls (Varargs)
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 9
 * <p>
 * Problem:
 * Why do variable arity (varargs) calls lead to unexpected results—ranging from
 * NullPointerExceptions to logical failures—when passing objects, nulls, collections,
 * or primitive arrays?
 * <p>
 * Expected behavior:
 * Developers expect the compiler to consistently wrap any single argument into
 * a new array or, in the case of collections and primitive arrays, to treat
 * them as a sequence of individual elements.
 * <p>
 * Actual behavior:
 * The compiler makes decisions based on the **declared type** at compile time, not
 * the runtime type. This leads to:
 * - Arrays declared as Object being re-wrapped into a new array.
 * - null being passed as a null array reference instead of an array containing null.
 * - Collections being wrapped as a single element of an array.
 * - Primitive arrays being wrapped as a single Object because they cannot satisfy
 * generic type requirements (T...).
 * <p>
 * Explanation:
 * - Expression Classification: Varargs methods allow either a flat list of arguments
 * or an explicit array. If exactly one argument is passed, the compiler must
 * decide whether to "wrap" it or "pass as is."
 * - Ambiguous Variable Arity: If the declared type of a single argument is assignable
 * to the array type (e.g., passing null or an Object[] to Object...), the compiler
 * assumes you are passing the array itself, not an element.
 * - Mixing Array and Collection: Collections (List) do not match the array
 * type (T[]) semantically. The compiler wraps the entire List as the first
 * element of a new array.
 * - Primitive Array Limitation: Since primitives cannot be used in generics,
 * an int[] is treated as a single Object and wrapped into an Object[1]
 * when passed to a method expecting T... or Object....
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: printAll(obj) where obj is declared as Object
 * - 1. The compiler sees printAll(Object... data).
 * - 2. It checks the declared type (Object). Since Object is not Object[], it wraps it.
 * - 3. Explicitly: printAll(new Object[] { obj });
 * - 4. If obj was an array at runtime, we now have a nested array, printing the array's hashcode.
 * - Case: printAll(null)
 * - 1. null is assignable to Object[]. Per JLS 15.12.4.2, it is not wrapped.
 * - 2. Explicitly: printAll((Object[]) null);
 * - 3. data becomes null, causing NPE during iteration.
 * - Case: contains("English", allLanguages) where allLanguages is a List
 * - 1. List is not an array. T is inferred as Object.
 * - 2. Explicitly: contains("English", new Object[] { allLanguages });
 * - 3. The search fails because a String is never equal to a List.
 * - Case: Arrays.asList(primitiveArray)
 * - 1. asList expects T... a. For example, int[] cannot be T[].
 * - 2. The compiler wraps it into a one element array.
 *      Explicitly: Arrays.asList(new Object[] { primitiveArray });
 * - 3. Result is a List<int[]> containing one element (the array).
 * <p>
 * Fixes:
 * - Ambiguous Calls: Use explicit casts: (Object)null to wrap, or (Object[])null to pass null.
 * - Collection Transition: When refactoring arrays to Collections, verify all varargs
 * call sites as they will no longer "unroll" the elements.
 * - Primitive Arrays: Avoid Arrays.asList() for primitives; use IntStream.of() or
 * convert to a boxed array first.
 * - Defensive Coding: Use intermediate variables with explicit types to verify
 * what the compiler is actually producing (e.g., List<int[]> vs List<Integer>).
 * <p>
 * Lesson:
 * - Varargs decisions are binding at compile time based on declared types.
 * - Passing null to varargs is not wrapped if it is assignable to the array type.
 * - Collections are never automatically expanded into varargs.
 * - Primitive arrays are treated as a single Object in generic varargs contexts.
 * <p>
 * Output:
 * - printAll(obj) -> [Ljava.lang.Object;@... (hashcode)
 * - printAll(null) -> NullPointerException
 * - isLanguage("English") -> false (searching String in List-inside-Array)
 * - containsZero(new int[]{0}) -> false (searching Integer in int[]-inside-List)
 */
public class VarargsMistakes {

    static final List<String> allLanguages = List.of("English", "French", "Italian", "Korean");

    static void printAll(Object... data) {
        // Explicitly: if data is null, the loop below throws NPE
        for (Object d : data) {
            System.out.println(d);
        }
    }

    @SafeVarargs
    static <T> boolean contains(T needle, T... haystack) {
        // Explicitly: If a List is passed, haystack becomes Object[1] { List }
        for (T t : haystack) {
            if (java.util.Objects.equals(t, needle)) {
                return true;
            }
        }
        return false;
    }

    static boolean isLanguage(String language){
        return contains(language, allLanguages);
    }

    static boolean containsZero(int[] arr) {
        // Explicitly: returns List<int[]> containing the array itself
        return java.util.Arrays.asList(arr).contains(0);
    }

    public static void main(String[] args) {
        // 1
        Object obj = new Object[]{"Hello", "world"};
        printAll(obj); // Output: [Ljava.lang.Object;@...

        // 2
        try{
            printAll(null);
        } catch (NullPointerException e){
            System.out.println("printAll(null) -> NullPointerException");
        }
        printAll((Object)null); // Output: null

        // 3
        System.out.println("isLanguage(\"English\") -> " + isLanguage("English"));

        // 4
        int[] arr = {0, 1, 2};
        System.out.println("containsZero(new int[]{0,1,2}) -> " + containsZero(arr));
    }
}