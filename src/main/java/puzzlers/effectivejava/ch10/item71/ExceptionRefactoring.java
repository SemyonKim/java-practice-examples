package puzzlers.effectivejava.ch10.item71;

import java.util.Optional;

/**
 * <h2>Avoid unnecessary use of checked exceptions</h2>
 *
 * <p>
 * <b>Core Principle:</b> Checked exceptions should be used only when the exceptional
 * condition cannot be prevented by proper API use AND the programmer can take useful
 * action to recover. If a caller can do nothing but wrap the exception or terminate
 * the program, use an unchecked exception.
 * </p>
 *
 * <h3>Advantages of Reducing Checked Exceptions</h3>
 * <ul>
 * <li><b>API Ergonomics:</b> Eliminates the need for boilerplate {@code try-catch} blocks or
 * {@code throws} declarations, making the code cleaner.</li>
 * <li><b>Stream Compatibility:</b> Methods that do not throw checked exceptions can be
 * used directly in Java Streams without messy wrapper logic (Items 45–48).</li>
 * <li><b>Flexibility:</b> Provides the caller the choice of whether to handle the
 * condition or let it propagate.</li>
 * </ul>
 *
 * <h3>Limitations of Alternatives</h3>
 * <ul>
 * <li><b>Optional:</b> Unlike exceptions, an {@code Optional} cannot carry additional
 * data (like an error code or message) explaining *why* the operation failed.</li>
 * <li><b>State-Testing:</b> Not suitable for concurrent environments where the state
 * might change between the "permission check" and the "action" (TOCTOU race condition).</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch7.item45 Streams
 * @see puzzlers.effectivejava.ch8.item55 Optionals
 * @see puzzlers.effectivejava.ch10.item69 ExceptionUsage
 * @see puzzlers.effectivejava.ch10.item70 CheckedVsUnchecked
 */
public class ExceptionRefactoring {

    // --- Technique 1: The "Optional" Approach (Item 55) ---

    /**
     * Instead of: public Result action(Args args) throws TheCheckedException
     * Use Optional to indicate failure without the burden of checked exceptions.
     */
    public Optional<String> fetchData(String key) {
        if (key == null || key.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of("Data for " + key);
    }

    // --- Technique 2: State-Testing Method Refactoring (Item 69) ---

    private boolean initialized = false;

    /**
     * State-testing method.
     * Allows the client to check if the action is permitted.
     */
    public boolean isReady() {
        return initialized;
    }

    /**
     * State-dependent method.
     * Throws an Unchecked exception if preconditions aren't met.
     */
    public void execute() {
        if (!initialized) {
            throw new IllegalStateException("Component not initialized");
        }
        System.out.println("Executing action...");
    }

    public void setInitialized(boolean state) {
        this.initialized = state;
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        ExceptionRefactoring api = new ExceptionRefactoring();

        // 1. Using Optional: Clean handling without try-catch
        api.fetchData("user_123")
                .ifPresent(data -> System.out.println("Found: " + data));

        // 2. Using State-testing: Flexible calling sequence
        if (api.isReady()) {
            api.execute();
        } else {
            System.out.println("System not ready; skipping execution.");
        }

        // 3. The "Trivial" sequence (if the programmer is sure it's safe)
        api.setInitialized(true);
        api.execute(); // No try-catch needed here!
    }
}