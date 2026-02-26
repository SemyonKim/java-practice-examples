package puzzlers.valeev.exceptions.mistake2;

import java.util.Optional;

/**
 * Using Optional instead of Null
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - Mistake 41, Section 2
 * <p>
 * Problem:
 * Using {@code null} as a return value is an implicit contract that callers often
 * overlook, leading to {@code NullPointerException}. Conversely, overusing
 * {@code Optional} (e.g., in parameters or fields) can make code verbose and
 * hide legitimate bugs.
 * <p>
 * Expected behavior:
 * Developers expect a clear signal when a search or computation might result
 * in "nothing." They expect the API to guide them toward safe handling.
 * <p>
 * Actual behavior:
 * - Silent Failures: Using {@code Optional.ofNullable()} when a value *should*
 * be present hides bugs that would be caught immediately by {@code Optional.of()}.
 * - Parameter Clutter: Passing {@code Optional} as an argument forces callers
 * to wrap values unnecessarily, complicating the API.
 * - Double-Null: Returning {@code null} from a method that is typed to return
 * {@code Optional} is a "betrayal" of the type system.
 * <p>
 *
 * <p>
 * Explanation:
 * - Signaling: {@code Optional} is a container (a "box") that is either empty or full.
 * Its main job is to prevent the caller from "forgetting" that the result could be missing.
 * - Fluent Transformation: Methods like {@code map()}, {@code filter()}, and
 * {@code orElse()} allow developers to handle missing values without {@code if (x != null)} blocks.
 * - Fail-Fast Principle: {@code Optional.of(value)} throws an NPE immediately if
 * the value is null. This is desirable if the presence of the value is a
 * requirement of the logic.
 * <p>
 * Ways to Avoid:
 * - Return {@code Optional} for search results or optional computations.
 * - Never return {@code null} if the return type is {@code Optional}.
 * - Avoid {@code Optional} as method parameters; use nullable primitives or
 * overloaded methods instead.
 * - Use {@code Optional.of()} when the value is mandatory; use {@code ofNullable()}
 * only when converting a potentially null source into a safe container.
 * <p>
 * Lesson:
 * - {@code Optional} is a tool for API design, not a complete replacement for null.
 * - Prefer an explicit crash (NPE) via {@code Optional.of()} over a silent
 * logic error via {@code ofNullable()}.
 */
public class OptionalUsageMistake {

    static class User {
        String fullName() { return "John Doe"; }
    }

    // --- Example 1: Signaling Absence via Return Value ---
    public Optional<User> findUser(String name) {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }
        // Use .of() if getExistingUser is guaranteed to return a user
        // Use .ofNullable() if getExistingUser might return null
        return Optional.ofNullable(getExistingUser(name));
    }

    private User getExistingUser(String name) {
        return "admin".equals(name) ? new User() : null;
    }

    // --- Example 2: Fluent Call Chains ---
    public void printUserName(String name) {
        String fullName = findUser(name)
                .map(User::fullName)
                .orElse("(no such user)");

        System.out.println("Result: " + fullName);
    }

    // --- Example 3: The ofNullable vs of Trap ---
    public void processMandatoryValue(String input) {
        // BAD: If 'input' is null due to a bug, this silently continues with an empty box
        Optional<String> bad = Optional.ofNullable(input);

        // GOOD: If 'input' is null, it crashes here, pinpointing the bug immediately
        Optional<String> good = Optional.of(input);
    }

    public static void main(String[] args) {
        OptionalUsageMistake example = new OptionalUsageMistake();
        example.printUserName("admin");
        example.printUserName("guest");
    }
}