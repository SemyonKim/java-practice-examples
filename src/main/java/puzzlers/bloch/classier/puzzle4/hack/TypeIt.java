package puzzlers.bloch.classier.puzzle4.hack;

import puzzlers.bloch.classier.puzzle4.click.CodeTalk;

/**
 * The Package-Private Pitfall
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 70
 * <p>
 * Problem:
 * Why does calling {@code clickIt.doIt()} print "Click" instead of "Hack",
 * even though {@code ClickIt} defines its own {@code printMessage} method?
 * <p>
 * Explanation:
 * - This puzzle highlights the interaction between package-private access
 * (default access) and method overriding.
 * - In {@code CodeTalk}, the method {@code printMessage()} has no access
 * modifier, making it **package-private**. It is only visible to classes
 * within the {@code click} package.
 * - In the {@code hack} package, {@code ClickIt} extends {@code CodeTalk}.
 * Because it is in a different package, it cannot see or inherit
 * {@code CodeTalk.printMessage()}.
 * - Therefore, the {@code printMessage()} method in {@code ClickIt} does
 * **not** override the one in {@code CodeTalk}. Instead, it is a
 * completely unrelated method that just happens to share the same name.
 * - When {@code doIt()} is called, it executes the code in the
 * {@code CodeTalk} class. Since the version of {@code printMessage()}
 * it knows about was never overridden, it calls its own local version.
 * <p>
 *
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Inheritance Check:** {@code ClickIt} inherits {@code doIt()} because
 * it is {@code public}. It does NOT inherit {@code printMessage()} because
 * it is package-private and {@code ClickIt} is in a different package.
 * 2. **Binding:** When {@code doIt()} calls {@code printMessage()}, the
 * compiler looks for an override. Since the subclass method isn't an
 * override (due to lack of visibility), it stays bound to the superclass
 * implementation.
 * 3. **Execution:** The JVM executes {@code CodeTalk.printMessage()},
 * resulting in the output "Click".
 * <p>
 * Lesson:
 * - You cannot override a method that you cannot see.
 * - If you intend to override a method in a subclass that might be in
 * a different package, ensure the superclass method is {@code protected}
 * or {@code public}.
 * - Use the {@code @Override} annotation! If you had placed
 * {@code @Override} above {@code ClickIt.printMessage()}, the compiler
 * would have flagged the error immediately.
 * <p>
 * Output:
 * Click
 */
public class TypeIt {
    private static class ClickIt extends CodeTalk {
        // This does NOT override CodeTalk.printMessage()
        void printMessage() {
            System.out.println("Hack");
        }
    }

    public static void main(String[] args) {
        ClickIt clickIt = new ClickIt();
        clickIt.doIt();
    }
}