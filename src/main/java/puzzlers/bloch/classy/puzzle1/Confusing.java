package puzzlers.bloch.classy.puzzle1;

/**
 * Case of the Confusing Constructor
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 46
 * <p>
 * Problem:
 * When passing {@code null} to an overloaded constructor where one
 * parameter is a supertype of the other, which one does Java pick?
 * Is it ambiguous, or is there a tie-breaking rule?
 * <p>
 * Example:
 * public class Confusing {
 * private Confusing(Object o) {
 * System.out.println("Object");
 * }
 * private Confusing(double[] dArray) {
 * System.out.println("double array");
 * }
 * public static void main(String[] args) {
 * new Confusing(null);
 * }
 * }
 * <p>
 * Explanation:
 * - The key rule here is the "Most Specific" rule (JLS 15.12.2.5).
 * - A constructor is "applicable" if the argument can be assigned to
 * the parameter type. Since {@code null} can be assigned to any
 * reference type, both constructors are applicable.
 * - If multiple constructors are applicable, the compiler picks the
 * most specific one.
 * - Constructor A is more specific than Constructor B if every
 * argument acceptable to A is also acceptable to B.
 * - Since every {@code double[]} is an {@code Object}, but not every
 * {@code Object} is a {@code double[]}, the {@code double[]}
 * version is more specific.
 * <p>
 * The "Confusing" Result:
 * - The compiler doesn't complain about ambiguity.
 * - It silently chooses the most specialized type available.
 * <p>
 * Step-by-step:
 * 1. The compiler sees {@code new Confusing(null)}.
 * 2. It identifies {@code Confusing(Object)} as a candidate.
 * 3. It identifies {@code Confusing(double[])} as a candidate.
 * 4. It compares the two: {@code double[]} is a subtype of {@code Object}.
 * 5. It selects {@code Confusing(double[])} as the most specific match.
 * <p>
 * Lesson:
 * - Avoid overloading methods or constructors where one parameter
 * type is a subtype of another.
 * - If you must do this, ensure both overloads behave identically
 * so the compiler's choice doesn't change the program's logic.
 * - To force a specific choice, use an explicit cast:
 * {@code new Confusing((Object) null)}.
 * <p>
 * Output:
 * // double array
 */
public class Confusing {

    private Confusing(Object o){
        System.out.println("Object");
    }

    private Confusing(double[] dArray){
        System.out.println("double array");
    }

    public static void main(String[] args) {
        new Confusing(null); // This will print "double array"
    }
}