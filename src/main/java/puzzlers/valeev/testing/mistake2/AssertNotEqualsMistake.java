package puzzlers.valeev.testing.mistake2;

/**
 * Using assertNotEquals() to check the equality contract
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 99
 * <p>
 * Problem:
 * Why does a broken {@code equals()} method that returns {@code true} for everything
 * still pass {@code assertNotEquals()} tests in some frameworks?
 * <p>
 * Expected behavior:
 * Developers expect {@code assertNotEquals(obj1, obj2)} to fail if {@code obj1.equals(obj2)}
 * returns {@code true}.
 * <p>
 * Actual behavior:
 * - In frameworks like TestNG, {@code assertNotEquals} is the literal inverse of
 * a strict {@code assertEquals}.
 * - If {@code assertEquals} checks both directions (obj1.equals(obj2) and obj2.equals(obj1)),
 * then {@code assertNotEquals} succeeds if **either** direction returns {@code false}.
 * - This creates a "false pass" where your buggy {@code equals} method is never
 * actually caught because the other object (like a {@code String}) correctly
 * rejects the comparison.
 * <p>
 *
 * <p>
 * Explanation:
 * - Symmetry Requirement: The Java contract requires that a.equals(b) must be
 * the same as b.equals(a).
 * - The TestNG Trap: If you compare your custom object to a {@code String}, your
 * object might say "Yes, I am equal to this String," but {@code "string".equals(yourObj)}
 * will say "No." Because one side said "No," TestNG considers them "not equal,"
 * and the test passes—masking your bug.
 * - Null Handling: Most frameworks handle {@code null} automatically, sometimes
 * skipping the {@code equals()} call entirely, which provides zero validation of
 * your logic.
 * <p>
 * Step-by-step (The Symmetry Failure):
 * - Create {@code MyCustomClass} where {@code equals()} always returns {@code true}.
 * - Call {@code assertNotEquals(new MyCustomClass(), "test")}.
 * - The framework checks {@code "test".equals(customObj)}.
 * - {@code String.equals()} returns {@code false} (correctly).
 * - The framework decides the objects are "not equal."
 * - The test passes, even though {@code customObj.equals("test")} would have returned {@code true}.
 * <p>
 * Fixes:
 * - Use assertFalse: Instead of {@code assertNotEquals(a, b)}, use
 * {@code assertFalse(a.equals(b))} to specifically test your implementation's logic.
 * - Test Symmetry Explicitly: If you are testing the contract, manually assert
 * both {@code a.equals(b)} and {@code b.equals(a)}.
 * - Avoid Mixed Types: Generally avoid {@code assertNotEquals} between different
 * classes unless you are specifically testing that your {@code equals} returns
 * false for incompatible types.
 * <p>
 * Lesson:
 * - {@code assertNotEquals()} is often too "loose" for unit testing an {@code equals()}
 * implementation.
 * - Be explicit about which side of the {@code .equals()} call you are testing.
 * <p>
 * Output:
 * - TestNG assertNotEquals: PASSED (This is the bug!)
 * - Explicit assertFalse: FAILED (Correctly caught the bug)
 */
public class AssertNotEqualsMistake {

    public static void main(String[] args) {
        MyCustomClass buggyInstance = new MyCustomClass();
        String other = "I am a string";

        System.out.println("Testing buggy equals implementation...");

        // Simulated TestNG behavior (version 7.4.0 logic)
        // It passes if either direction is false
        boolean testNGResult = !(buggyInstance.equals(other) && other.equals(buggyInstance));

        System.out.println("--- Test Results ---");
        System.out.println("Buggy Object says it equals String: " + buggyInstance.equals(other));
        System.out.println("String says it equals Buggy Object: " + other.equals(buggyInstance));

        if (testNGResult) {
            System.out.println("Result: assertNotEquals(buggy, string) PASSED");
            System.out.println("Conclusion: The test failed to catch the bug!");
        }

        // The recommended way to catch this
        try {
            assertFalse(buggyInstance.equals(other), "Object should not equal string");
        } catch (AssertionError e) {
            System.out.println("\nResult: assertFalse(buggy.equals(string)) FAILED");
            System.out.println("Conclusion: The bug was correctly caught.");
        }
    }

    static void assertFalse(boolean condition, String message) {
        if (condition) throw new AssertionError(message);
    }

    static class MyCustomClass {
        @Override
        public boolean equals(Object obj) {
            // BUG: Always returns true, violating the contract and logic
            return true;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
}