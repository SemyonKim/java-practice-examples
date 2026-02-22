package puzzlers.bloch.exceptional.puzzle4;

/**
 * The Exhausting Recursive Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 45
 * <p>
 * Problem:
 * Does a finally block always execute even during a StackOverflowError
 * caused by infinite recursion? And what happens if the finally
 * block itself is recursive?
 * <p>
 * Example:
 * public class Workout {
 * public static void main(String[] args) {
 * workOut();
 * }
 * private static void workOut() {
 * try {
 * workOut();
 * } finally {
 * workOut();
 * }
 * }
 * }
 * <p>
 * Explanation:
 * - This code creates a "recursive explosion."
 * - When the first call to {@code workOut()} is made, it calls itself
 * repeatedly until the JVM's method invocation stack is full.
 * - At that point, the JVM throws a {@link StackOverflowError}.
 * - Java guarantees that a {@code finally} block will execute when
 * a {@code try} block exits, even if it exits via an Error.
 * - However, the {@code finally} block here calls {@code workOut()}
 * AGAIN. Since the stack is already full, this second call
 * immediately triggers another {@code StackOverflowError}.
 * <p>
 * The "Exhausting" Result:
 * - The program doesn't just crash once; it attempts to execute
 * thousands (or millions) of {@code finally} blocks as the stack
 * unwinds.
 * - Because each {@code finally} block consumes what little stack
 * space was just freed by the unwinding, the program spends
 * an enormous amount of time throwing and catching errors.
 * - On many JVMs, this results in a silent termination because the
 * JVM eventually runs out of stack space even to create the
 * error message for the console.
 * <p>
 * Step-by-step:
 * 1. {@code workOut()} is called. Stack depth increases.
 * 2. Stack depth reaches limit ($2^{16}$ to $2^{20}$ typically).
 * 3. {@code StackOverflowError} (SOE) is thrown.
 * 4. The current stack frame's {@code finally} block is triggered.
 * 5. {@code finally} calls {@code workOut()}, throwing a new SOE.
 * 6. The stack unwinds one level and repeats the process.
 * <p>
 * Lesson:
 * - {@code finally} blocks are reliable, but they aren't magic.
 * - If a {@code finally} block requires stack space (by calling a
 * method) and the stack is exhausted, the {@code finally}
 * block will fail too.
 * - Avoid complex logic or method calls inside {@code finally}
 * blocks when dealing with potentially unstable states.
 * <p>
 * Output:
 * // Usually nothing. The program hangs for a while and then terminates
 * // without a stack trace.
 */
public class Workout {
    public static void main(String[] args) {
        // This will likely terminate silently or after a long delay
        // workOut();
    }
}