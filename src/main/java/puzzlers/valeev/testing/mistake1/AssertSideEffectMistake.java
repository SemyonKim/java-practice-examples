package puzzlers.valeev.testing.mistake1;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Side effect in assert statement
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 94
 * <p>
 * Problem:
 * Why does code that passes all tests fail in production with "IllegalStateException"
 * or missing data?
 * <p>
 * Expected behavior:
 * Developers expect {@code assert} statements to be "invisible" checks that
 * validate logic without altering the program's state.
 * <p>
 * Actual behavior:
 * - Java assertions are **disabled by default** in production (unless {@code -ea}
 * is passed to the JVM).
 * - If the expression inside the {@code assert} has a side effect (e.g., modifying
 * a collection or advancing a cursor), that side effect **never happens** in
 * production.
 * <p>
 *
 * <p>
 * Explanation:
 * - The assertion {@code assert condition : message;} is logically equivalent
 * to: $if (assertionsEnabled) \{ if (!condition) throw new AssertionError(message); \}$.
 * - If {@code assertionsEnabled} is false, the code inside the parenthesis is
 * never even evaluated.
 * - This creates a "Heisenbug": the bug only appears when you stop looking for it
 * (by turning off assertions).
 * <p>
 * Step-by-step (The Matcher Failure):
 * - A method uses {@code assert matcher.find();} to ensure a pattern exists.
 * - In testing (with {@code -ea}), {@code find()} returns true and advances
 * the matcher to the first group.
 * - In production (without {@code -ea}), {@code find()} is **not called**.
 * - The subsequent call to {@code matcher.group(1)} fails because the matcher
 * hasn't been started.
 * <p>
 * Fixes:
 * - Extract the side effect: Perform the action in a standard statement and
 * assign the result to a local variable. Then assert that variable.
 * - Use IF for Preconditions: If a condition **must** be true for the program
 * to continue (like a file existing or a regex matching), use a standard
 * {@code if} check and throw an {@code IllegalArgumentException}.
 * - CI Configuration: Occasionally run your CI pipeline with assertions
 * disabled to ensure the logic remains sound.
 * <p>
 * Lesson:
 * - Assertions are for **invariants** (things that should technically be
 * impossible), not for **preconditions** or **state changes**.
 * - Never put a method call that changes state inside an {@code assert}.
 * <p>
 * Output:
 * - With -ea: [Set size: 1, Matcher group: 123]
 * - Without -ea: [Set size: 0, IllegalStateException: No match found]
 */
public class AssertSideEffectMistake {

    public static void main(String[] args) {
        testSetAdd();
        testMatcher();
    }

    /**
     * Demonstrates how data can "disappear" in production.
     */
    static void testSetAdd() {
        Set<String> set = new HashSet<>();
        String value = "important_data";

        // BUG: In production, the add() call is skipped!
        assert set.add(value) : "Duplicate value: " + value;

        System.out.println("Set size: " + set.size());
    }

    /**
     * Demonstrates a crash that only happens when assertions are off.
     */
    static void testMatcher() {
        String message = "ID (123)";
        Pattern pattern = Pattern.compile("\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(message);

        // BUG: find() advances the matcher state. If skipped, group() fails.
        assert matcher.find();

        try {
            System.out.println("Extracted: " + matcher.group(1));
        } catch (IllegalStateException e) {
            System.out.println("CRASH: Matcher was never started (assertions are off)!");
        }
    }

    /**
     * The robust way to handle these scenarios.
     */
    static void robustApproach(String message) {
        Pattern pattern = Pattern.compile("\\((\\d+)\\)");
        Matcher matcher = pattern.matcher(message);

        // Fix: Use an explicit check for mandatory state changes
        if (!matcher.find()) {
            throw new IllegalArgumentException("Message must contain a number in parentheses");
        }

        System.out.println("Safe extraction: " + matcher.group(1));
    }
}