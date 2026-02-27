package puzzlers.valeev.strings.mistake4;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Mismatched Format Arguments
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 48
 * <p>
 * Problem:
 * Why do String.format() and printf() often fail at runtime or produce silent
 * formatting errors that the compiler cannot detect?
 * <p>
 * Expected behavior:
 * Developers expect a one-to-one mapping between format specifiers (like %s, %d)
 * and the provided arguments, with strict type checking.
 * <p>
 * Actual behavior:
 * The Java compiler treats format strings as plain text, delaying validation until
 * runtime. This leads to MissingFormatArgumentException,
 * IllegalFormatConversionException, or silent errors where extra arguments are
 * ignored or types are misinterpreted (especially with %b).
 * <p>
 * Explanation:
 * - Runtime Validation: Because the format string can be dynamic, the JVM only
 * checks it when the method is executed.
 * - The %b Trap: The boolean specifier (%b) returns "false" for null and "true"
 * for ANY other object. This means swapping a string and a boolean in the
 * argument list often won't trigger an exception, just incorrect text.
 * - Ignored Arguments: Providing more arguments than specifiers is not an error;
 * the JVM simply ignores the trailing values, which often masks logic errors.
 * - %s Versatility: Like %b, %s accepts any object by calling String.valueOf(),
 * making it easy to accidentally pass the wrong variable without a crash.
 * <p>
 * Step-by-step (The Mechanics):
 * - Case: Type Mismatch
 * - 1. Developer uses %d (integer) but passes "123" (String).
 * - 2. Runtime throws IllegalFormatConversionException because 'd' != String.
 * - Case: Argument Swapping (%s and %b)
 * - 1. Format: "Day: %s; isWeekend = %b".
 * - 2. Args: (boolean) isWeekend, (Object) dayOfWeek.
 * - 3. Result: "Day: false; isWeekend = true" (The boolean was treated as a
 * string, and the object was treated as a boolean).
 * - Case: The "Forgotten" Argument
 * - 1. Format: "Point: %d".
 * - 2. Args: x, y.
 * - 3. Result: "Point: 10" (y is silently discarded).
 * <p>
 * Fixes:
 * - Testability: Extract string construction into helper methods (e.g., getGreeting())
 * so they can be unit-tested independently of I/O.
 * - Explicit Indexing: Use the "n$" syntax (e.g., %1$s, %2$d) to map specifiers
 * to specific argument positions. This is robust against reordering and
 * essential for localization.
 * - Static Analysis: Use IDE plugins or tools like SonarLint/FindBugs that
 * perform static analysis on constant format strings.
 * <p>
 * Lesson:
 * - Format strings are "code in strings"; they lack compile-time safety.
 * - Always use explicit argument indexing for complex strings or localized content.
 * - Treat %b and %s with caution as they are "catch-all" specifiers that hide bugs.
 * <p>
 * Output:
 * - greetUser("John") -> Hello, John!
 * - formatError() -> IllegalFormatConversionException: d != java.lang.String
 * - silentError -> Point is: 10 (20 is ignored)
 */
public class FormatArgumentMistake {

    public static void main(String[] args) {
        // 1. Silent Failure: Ignored arguments
        int x = 10;
        int y = 20;
        System.out.println("--- Silent Failure (Extra Argument) ---");
        System.out.printf("Point is: %d%n", x, y); // y is ignored

        // 2. The %b and %s Swap (No Exception, but wrong data)
        System.out.println("\n--- The %b Trap ---");
        DayOfWeek dw = DayOfWeek.FRIDAY;
        boolean isWeekend = (dw == DayOfWeek.SATURDAY || dw == DayOfWeek.SUNDAY);
        // Correct order: %s, %b -> dw, isWeekend
        // Accidental swap:
        System.out.printf("Day: %s; isWeekend = %b%n", isWeekend, dw);

        // 3. Fix: Explicit Indexing
        System.out.println("\n--- Fix: Explicit Indexing ---");
        // %2$s = second arg as string; %1$tD = first arg as date
        System.out.printf("User: %2$s! Date: %1$tD%n", LocalDate.now(), "John");

        // 4. Testing separation
        System.out.println("\n--- Testing Separation ---");
        System.out.print(getGreeting("Alice"));

        // 5. Runtime Exception (Uncomment to see crash)
        // System.out.printf("Length is %d", "123");
    }

    /**
     * Separating construction from printing makes this easy to unit test.
     */
    public static String getGreeting(String userName) {
        return String.format("Hello, %s!%n", userName);
    }
}