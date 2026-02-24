package puzzlers.bloch.classier.puzzle6;

/**
 * The Inconsistency in The Final Modifier for Methods and Fields
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 72
 * <p>
 * Problem:
 * Does the {@code final} modifier prevent a field from being hidden in a
 * subclass, similar to how it prevents a method from being overridden?
 * <p>
 * Explanation:
 * - This puzzle highlights a significant inconsistency in the Java language:
 * the {@code final} modifier behaves differently for methods than it does
 * for fields.
 * - A **final instance (static) method** cannot be overridden (hidden).
 * If a subclass attempts to define a method with the same signature,
 * the compiler throws an error.
 * - A **final field**, however, **can be hidden**. The {@code final}
 * keyword on a field only means that the field's value cannot be changed
 * after initialization. It does nothing to protect the field's name from
 * being reused by a subclass.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Inheritance:** {@code DoubleJeopardy} inherits from {@code Jeopardy}.
 * 2. **Field Declaration:** {@code DoubleJeopardy} declares its own
 * {@code static final String PRIZE}. This is perfectly legal.
 * 3. **Hiding:** The {@code PRIZE} field in the subclass "hides" the
 * {@code PRIZE} field in the superclass.
 * 4. **Local Resolution:** Inside the {@code main} method of
 * {@code DoubleJeopardy}, the simple name {@code PRIZE} refers to the
 * field declared in the current class, not the inherited one.
 * <p>
 * Lesson:
 * - The {@code final} modifier is not a tool for controlling inheritance
 * of fields; it is only for controlling the mutability of values.
 * - To avoid confusion, never give a field in a subclass the same name
 * as a field in a superclass, even (and especially) if they are
 * {@code static final} constants.
 * - If you want to ensure a value is truly "locked" and shared across
 * an entire hierarchy without risk of hiding, consider using a
 * **final method** to return the value instead of a public field.
 * <p>
 * Output:
 * DoubleJeopardy.PRIZE: $128,000
 * Jeopardy.PRIZE: $64,000
 */
class Jeopardy {
    public static final String PRIZE = "$64,000";

    public static final String getPrize(){
        return PRIZE;
    }
}

public class DoubleJeopardy extends Jeopardy {
    // This is legal even though the superclass field is 'final'
    public static final String PRIZE = "$128,000";

    public static void main(String[] args) {
        System.out.println("DoubleJeopardy.PRIZE: " + PRIZE);
        System.out.println("Jeopardy.PRIZE: " + getPrize());
    }
}