package puzzlers.bloch.libraryadvanced.puzzle2;

/**
 * The Innermost Scope Name Resolution Trap
 * <p>
 * Inspired by: Java Puzzlers (Bloch & Gafter, 2005) - Puzzle 79
 * <p>
 * Problem:
 * When an anonymous inner class extends a superclass (like {@code Thread}),
 * the compiler searches the innermost scope for method names. If it finds
 * the name but the arguments don't match, it throws an error rather than
 * searching outer scopes.
 * <p>
 * Explanation:
 * - Java's name resolution algorithm is "greedy" for **names**, not signatures.
 * - In {@code run()}, the call to {@code sleep()} is checked against {@code Thread}'s
 * members because the anonymous class is a subclass of {@code Thread}.
 * - Even though {@code Thread.sleep(long)} is static and has a different
 * signature, the compiler stops at the first scope that contains the name "sleep".
 * <p>
 *
 * <p>
 * The Three Fixes:
 * 1. **Rename the Method:** Avoid collisions with "heavy" classes like {@code Thread}.
 * 2. **Qualified 'this':** Explicitly point to the outer scope using {@code Outer.this.method()}.
 * 3. **Favor Composition:** Use {@code Runnable} so the anonymous class doesn't
 * inherit {@code Thread}'s namespace.
 */
public class Pet {
    private final String name;
    private final String food;
    private final String sound;

    public Pet(String name, String food, String sound) {
        this.name = name;
        this.food = food;
        this.sound = sound;
    }

    public void eat() { System.out.println(name + " eats " + food); }
    public void play() { System.out.println(name + " plays and says " + sound); }
    public void sleep() { System.out.println(name + " is sleeping... Zzzzz"); }

    public void live() {
        // --- THE INCORRECT VERSION ---
        new Thread() {
            @Override
            public void run() {
                eat(); // Works: No 'eat' in Thread
                play(); // Works: No 'play' in Thread
                // sleep();
                /* ERROR: The compiler finds 'static void sleep(long)' in Thread.
                   It stops searching and fails because you provided no arguments. */
            }
        }.start();

        // --- FIX 1: Qualified This ---
        new Thread() {
            @Override
            public void run() {
                Pet.this.sleep(); // Specifically tells compiler to use Pet's scope
            }
        }.start();

        // --- FIX 2: Favor Composition (The Best Practice) ---
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Works! Runnable has no 'sleep' method, so Pet.sleep() is found.
                sleep();
            }
        }).start();
    }

    // --- FIX 3: Rename the method to avoid the collision entirely ---
    public void snooze() {
        System.out.println(name + " is snoozing...");
    }

    public static void main(String[] args) {
        Pet dog = new Pet("Buddy", "Kibble", "Woof!");
        dog.live();
    }
}