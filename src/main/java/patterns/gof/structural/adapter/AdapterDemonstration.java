package patterns.gof.structural.adapter;

/**
 * ============================================================================
 * DESIGN PATTERN: Adapter
 * CATEGORY:       Structural
 * ALSO KNOWN AS:  Wrapper
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Convert the interface of a class into another interface clients expect.
 * Adapter lets classes work together that couldn't otherwise because of
 * incompatible interfaces.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A drawing editor requires all elements to implement a 'Shape' interface.
 * We want to include advanced text editing using a third-party 'TextView'
 * class, but 'TextView' doesn't implement 'Shape'. We create an Adapter
 * that translates 'Shape' method calls into 'TextView' method calls.
 * <p>
 * 3. APPLICABILITY
 * Use when you need to integrate a third-party library, legacy code, or an
 * unrelated system into your application without modifying their source code
 * or compromising your own domain interfaces.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - Target (Shape): The interface expected by the client.
 * - Adaptee (TextView): The existing, incompatible class.
 * - Adapter (TextShape): Implements Target, delegates to Adaptee.
 * - Client: Uses the Target interface.
 * <p>
 * 5. COLLABORATIONS
 * Client calls Adapter. Adapter translates the call and invokes Adaptee.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * Promotes loose coupling and Single Responsibility by isolating the translation
 * logic. However, it increases the overall complexity of the code by introducing
 * new interfaces and classes.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * Favor Object Adapters (Composition) over Class Adapters (Inheritance). Object
 * Adapters are more flexible and play nicer with Java's single inheritance rule.
 * ============================================================================
 */

public class AdapterDemonstration {

    // ========================================================================
    // EXTERNAL/LEGACY SYSTEM (THE ADAPTEE)
    // ========================================================================

    /**
     * Adaptee: A complex third-party or legacy class.
     * We cannot or should not modify this class.
     */
    static class TextView {
        public void getExtent() {
            System.out.println("TextView: Calculating text extent and boundaries.");
        }

        public boolean isEmpty() {
            return false; // Mock implementation
        }
    }

    // ========================================================================
    // DOMAIN INTERFACES
    // ========================================================================

    /**
     * Target: The interface our domain logic and clients expect to use.
     */
    interface Shape {
        void getBoundingBox();
        boolean isRenderable();
    }

    // ========================================================================
    // PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
    // ========================================================================

    /**
     * A demonstration of code before applying the pattern.
     * The client is forced to handle the incompatible type directly, leading to
     * messy `instanceof` checks and tight coupling to the external library.
     */
    static class NaiveDrawingEditor {
        public void renderShape(Object element) {
            if (element instanceof Shape) {
                ((Shape) element).getBoundingBox();
            } else if (element instanceof TextView) {
                // Client must know about the Adaptee and how to translate it manually
                System.out.println("Naive Editor: Translating on the fly...");
                ((TextView) element).getExtent();
            }
        }
    }

    // ========================================================================
    // PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
    // ========================================================================

    /**
     * Concrete Target: A standard implementation that fits naturally.
     */
    static class LineShape implements Shape {
        @Override
        public void getBoundingBox() {
            System.out.println("LineShape: Calculating bounding box for a line.");
        }

        @Override
        public boolean isRenderable() {
            return true;
        }
    }

    /**
     * Object Adapter: Composes the Adaptee and implements the Target.
     * This is the preferred approach in Modern Java.
     */
    static class TextShapeObjectAdapter implements Shape {
        private final TextView textView;

        public TextShapeObjectAdapter(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void getBoundingBox() {
            // Translating Target's request to Adaptee's interface
            textView.getExtent();
        }

        @Override
        public boolean isRenderable() {
            return !textView.isEmpty();
        }
    }

    /**
     * Class Adapter: Implements the Target and extends the Adaptee.
     * Less flexible in Java as it consumes the single inheritance slot.
     */
    static class TextShapeClassAdapter extends TextView implements Shape {
        @Override
        public void getBoundingBox() {
            // Inherited directly from TextView
            this.getExtent();
        }

        @Override
        public boolean isRenderable() {
            return !this.isEmpty();
        }
    }

    // ========================================================================
    // PHASE 3: EXECUTION (MAIN METHOD)
    // ========================================================================

    public static void main(String[] args) {
        System.out.println("--- Adapter Pattern: Naive Approach ---");
        NaiveDrawingEditor editor = new NaiveDrawingEditor();
        Shape line = new LineShape();
        TextView rawText = new TextView();

        editor.renderShape(line);
        editor.renderShape(rawText); // Messy type-checking hidden inside

        System.out.println("\n--- Adapter Pattern: Modern Object Adapter ---");
        // The client only knows about 'Shape'. It doesn't care if it's a Line or Text.
        Shape adaptedText = new TextShapeObjectAdapter(new TextView());

        // Clean, uniform polymorphism.
        Shape[] canvas = { new LineShape(), adaptedText };
        for (Shape s : canvas) {
            s.getBoundingBox();
        }

        System.out.println("\n--- Adapter Pattern: Class Adapter ---");
        Shape classAdaptedText = new TextShapeClassAdapter();
        classAdaptedText.getBoundingBox();
    }
}