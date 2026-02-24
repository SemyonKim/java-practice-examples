package puzzlers.bloch.advanced.puzzle6;

/**
 * Twisted Pair: The Scope vs. Inheritance Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 92
 * <p>
 * Problem:
 * When calling {@code name()} inside the anonymous class, why does it
 * print "main" (the outer instance) instead of "reproduce" (the
 * anonymous instance), even though the anonymous class is a subclass
 * of {@code Twisted}?
 * <p>
 * Explanation:
 * - **Private Methods are not Inherited:** Because {@code name()} is
 * private, it is not "passed down" to the anonymous subclass. Therefore,
 * the anonymous instance does not have its own {@code name()} method.
 * - **Lexical Scoping wins:** When the compiler looks for {@code name()}
 * and doesn't find it in the anonymous class's inheritance hierarchy,
 * it looks at the enclosing (outer) scope.
 * - Since the anonymous class is nested inside {@code Twisted("main")},
 * it finds and calls the outer instance's {@code name()} method.
 * <p>
 * Step-by-step (The Mechanics):
 * 1. **Search Inheritance:** The compiler looks for {@code name()} in
 * the anonymous class. It is not there. It looks in the superclass
 * {@code Twisted}. Because the method is {@code private}, it is not
 * inherited. The search here fails.
 * 2. **Search Enclosing Scope:** The compiler then looks at the
 * enclosing {@code Twisted} instance (the one where {@code name} is "main").
 * 3. **Success (but wrong object):** It finds the private method in
 * the outer class and calls it.
 * <p>
 * The Fixes:
 * 1. **Make it Protected:** Changing {@code private String name()} to
 * {@code protected} would allow the anonymous class to inherit it.
 * The output would change to "reproduce".
 * 2. **Explicit Member Access:** If you wanted the field directly,
 * the field {@code name} is also private and not inherited, leading
 * to the same "Main" result.
 * <p>
 * Lesson:
 * - Method resolution follows a specific order: Inheritance first,
 * then Enclosing Scopes.
 * - Private members can create "holes" in the inheritance chain that
 * allow the compiler to fall through to the outer scope, leading to
 * extremely counter-intuitive behavior.
 * <p>
 * Output:
 * main
 */
public class Twisted {
    private final String name;

    Twisted(String name) {
        this.name = name;
    }

    private String name() {
        return name;
    }

    private void reproduce() {
        new Twisted("reproduce") {
            void printName() {
                // Resolution:
                // 1. Inherited name()? No (it's private).
                // 2. Enclosing name()? Yes!
                System.out.println(name());
            }
        }.printName();
    }

    public static void main(String[] args) {
        new Twisted("main").reproduce();
    }
}