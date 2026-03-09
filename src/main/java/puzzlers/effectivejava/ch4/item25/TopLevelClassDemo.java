package puzzlers.effectivejava.ch4.item25;

/**
 * <h2>Limit source files to a single top-level class</h2>
 *
 * <p>
 * <b>Core Principle:</b> Never define multiple top-level classes or interfaces in a single
 * source file. If you need multiple classes in one file, use static member classes.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Deterministic Builds:</b> Guarantees that the behavior of the program is
 * independent of the order in which source files are passed to the compiler.</li>
 * <li><b>Avoids Duplicate Definitions:</b> Prevents the accidental creation of
 * multiple definitions for the same class name across different files.</li>
 * <li><b>Encapsulation:</b> Using static member classes (if applicable) allows
 * you to reduce accessibility by making them {@code private}.</li>
 * <li><b>Maintainability:</b> Makes it much easier for developers to find the
 * source code for a class based on its name.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>File Count:</b> Increases the total number of files in a project,
 * though this is a standard and expected practice in Java.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item24 NestedClasses
 * @see puzzlers.effectivejava.ch4.item15 Accessibility
 */
public class TopLevelClassDemo {

    /**
     * If you are tempted to put multiple classes in one file,
     * make them static member classes instead of top-level classes.
     */
    private static class Utensil {
        static final String NAME = "pan";
    }

    private static class Dessert {
        static final String NAME = "cake";
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        // This is safe because Utensil and Dessert are nested and private.
        // There is no risk of another "Utensil.java" interfering with this specific logic.
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}

/*
 * ANTIPATTERN:
 *
 * // File: Utensil.java
 *
 * class Utensil { static final String NAME = "pan"; }
 * class Dessert { static final String NAME = "cake"; }
 *
 * If another file "Dessert.java" also defines 'class Utensil' and/or 'class Dessert',
 * the 'javac' command order determines which "Utensil" is used.
 * This is a nightmare to debug.
 */