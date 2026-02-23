package puzzlers.bloch.classy.puzzle8;

/**
 * The Local Variable Declaration Statement Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 55
 * <p>
 * Problem:
 * Why does a simple loop fail to compile when you try to declare
 * a variable inside it without using curly braces?
 * <p>
 * Explanation:
 * - According to the Java Language Specification (JLS 14.4), a local
 * variable declaration is not a "statement" in the same way an
 * assignment or a method call is.
 * - It is a **LocalVariableDeclarationStatement**, which is only
 * allowed directly inside a **Block** (a set of curly braces).
 * - The logic behind this rule is scope: a variable declared without
 * braces would be immediately inaccessible, as its scope would end
 * before it could even be used in the next iteration or outside
 * the loop.
 * <p>
 * The "Illegal" Result:
 * - The compiler throws an error: {@code 'variable declaration not allowed here'}.
 * - Java forces you to be explicit about the scope of your variables
 * to prevent logical errors where a variable is defined but
 * instantly "lost."
 * <p>
 * Step-by-step (The Fixes):
 * 1. **Add Braces:** By adding {@code { ... }}, you create a Block.
 * Declarations are perfectly legal inside a Block.
 * 2. **Omit the Declaration:** If you don't actually need to reference
 * the object via a variable, calling the constructor alone
 * (e.g., {@code new Creature();}) is a valid Statement.
 * <p>
 * Lesson:
 * - A local variable declaration cannot be used as the repeated
 * statement in a {@code for}, {@code while}, or {@code do} loop.
 * - Always use curly braces for loop bodies if you intend to
 * declare variables, even if the loop only contains one line.
 * <p>
 * Output:
 * // 200
 */
public class Creator {
    public static void main(String[] args) {

//        // Compilation fails: A local variable declaration
//        // can appear only as a statement directly within a block
//        for (int i = 0; i < 100; i++)
//            Creature creature = new Creature();

        // Fix: add curly braces to create a Block
        for (int i = 0; i < 100; i++) {
            Creature creature = new Creature();
        }

        // Alternative: Use an expression statement without a declaration
        for (int i = 0; i < 100; i++)
            new Creature();

        System.out.println(Creature.numCreated());
    }
}

class Creature {
    private static long numCreated = 0;

    public Creature() {
        numCreated++;
    }

    public static long numCreated() {
        return numCreated;
    }
}