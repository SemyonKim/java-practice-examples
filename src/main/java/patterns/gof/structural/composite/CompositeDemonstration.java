package patterns.gof.structural.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * DESIGN PATTERN: Composite
 * CATEGORY:       Structural
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Compose objects into tree structures to represent part-whole hierarchies.
 * Composite lets clients treat individual objects and compositions of objects
 * uniformly.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * Imagine a drawing application. You can draw lines, rectangles, and text.
 * You can also group these elements into a "Picture" and move them around
 * together. A Picture can contain other Pictures. Instead of the client writing
 * complex if-else logic to check if an object is a single shape or a group,
 * we unify them under a common interface.
 * <p>
 * 3. APPLICABILITY
 * Use when you want to represent hierarchies of objects, and you want clients
 * to interact with both individual objects (leaves) and compositions (branches)
 * without knowing the difference.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Component (Graphic): The unified interface for both leaf and composite nodes.
 * - Leaf (Line, Rectangle): Represents primitive objects with no children.
 * - Composite (Picture): Stores child components and delegates operations to them.
 * - Client: Manipulates the hierarchy uniformly via the Component interface.
 * <p>
 * 5. COLLABORATIONS
 * The Client calls operations on the Component. If it's a Leaf, it acts directly.
 * If it's a Composite, it forwards the call to its children, optionally
 * performing additional logic.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * - Pros: Simplifies client code (no instanceof checks); easily extensible with
 * new component types.
 * - Cons: Makes it harder to restrict the types of components added to a
 * composite (cannot easily use the type system; requires runtime checks).
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * - Java 8+ Default Methods: Provide default implementations for add/remove in
 * the interface that throw UnsupportedOperationException to balance transparency
 * and safety.
 * - Memory management is handled by Java's Garbage Collector, but logical cleanup
 * (e.g., removing parent references) may still be necessary to avoid leaks in
 * long-lived trees.
 */
public class CompositeDemonstration {

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * A demonstration of code before applying the pattern.
     * The client has to maintain separate lists and use conditional logic.
     */
    static class NaiveDrawingEditor {
        private final List<Object> lines = new ArrayList<>();
        private final List<Object> texts = new ArrayList<>();
        private final List<NaiveDrawingEditor> groups = new ArrayList<>(); // Nested groups

        public void addLine(Object line) { lines.add(line); }
        public void addText(Object text) { texts.add(text); }
        public void addGroup(NaiveDrawingEditor group) { groups.add(group); }

        public void drawEverything() {
            for (Object line : lines) {
                System.out.println("Drawing Line (Naive)");
            }
            for (Object text : texts) {
                System.out.println("Drawing Text (Naive)");
            }
            for (NaiveDrawingEditor group : groups) {
                group.drawEverything(); // Recursive call, but highly coupled
            }
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * The elegant, refactored code using the GoF structure.
     */

    // 1. Component
    public interface Graphic {
        void draw();

        // Default methods favor Transparency over pure compile-time Safety.
        // Leaves inherit this and fail-fast if misused.
        default void add(Graphic graphic) {
            throw new UnsupportedOperationException("Cannot add to a leaf node.");
        }

        default void remove(Graphic graphic) {
            throw new UnsupportedOperationException("Cannot remove from a leaf node.");
        }

        default Graphic getChild(int index) {
            throw new UnsupportedOperationException("Leaf nodes do not have children.");
        }
    }

    // 2. Leaf Components
    public static class Line implements Graphic {
        @Override
        public void draw() {
            System.out.println("  Drawing Line");
        }
    }

    public static class Rectangle implements Graphic {
        @Override
        public void draw() {
            System.out.println("  Drawing Rectangle");
        }
    }

    public static class Text implements Graphic {
        private final String content;

        public Text(String content) {
            this.content = content;
        }

        @Override
        public void draw() {
            System.out.println("  Drawing Text: '" + content + "'");
        }
    }

    // 3. Composite
    public static class Picture implements Graphic {
        private final List<Graphic> children = new ArrayList<>();
        private final String name;

        public Picture(String name) {
            this.name = name;
        }

        @Override
        public void draw() {
            System.out.println("Drawing Picture Group: [" + name + "]");
            // Delegate the operation to all children
            for (Graphic child : children) {
                child.draw();
            }
        }

        @Override
        public void add(Graphic graphic) {
            children.add(graphic);
        }

        @Override
        public void remove(Graphic graphic) {
            children.remove(graphic);
        }

        @Override
        public Graphic getChild(int index) {
            if (index >= 0 && index < children.size()) {
                return children.get(index);
            }
            throw new IndexOutOfBoundsException("No child at index " + index);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     * Runnable code demonstrating the pattern in action vs. the naive way.
     */
    public static void main(String[] args) {
        System.out.println("--- Composite Pattern: Naive Approach ---");
        NaiveDrawingEditor rootNaive = new NaiveDrawingEditor();
        rootNaive.addLine(new Object());
        rootNaive.addText(new Object());

        NaiveDrawingEditor groupNaive = new NaiveDrawingEditor();
        groupNaive.addLine(new Object());
        rootNaive.addGroup(groupNaive);

        rootNaive.drawEverything();

        System.out.println("\n--- Composite Pattern: Pattern Approach ---");
        // Create leaves
        Graphic line1 = new Line();
        Graphic rect1 = new Rectangle();
        Graphic text1 = new Text("Hello World");

        Graphic line2 = new Line();
        Graphic line3 = new Line();

        // Create compositions
        Graphic rootPicture = new Picture("Root Canvas");
        Graphic subPicture = new Picture("Sub-group (Logo)");

        // Compose the tree
        subPicture.add(line2);
        subPicture.add(line3);

        rootPicture.add(line1);
        rootPicture.add(rect1);
        rootPicture.add(text1);
        rootPicture.add(subPicture); // Adding a composite to a composite

        // Client interacts with everything uniformly
        // The client doesn't need to know 'rootPicture' contains nested elements.
        System.out.println("Client calls draw() on the root component:");
        rootPicture.draw();

        // Demonstrating safety check on a leaf
        System.out.println("\nAttempting to add an element to a Leaf node:");
        try {
            line1.add(new Rectangle());
        } catch (UnsupportedOperationException e) {
            System.out.println("  Caught exception: " + e.getMessage());
        }
    }
}