package patterns.gof.behavioral.templatemethod;

/**
 * ============================================================================
 * DESIGN PATTERN: Template Method
 * CATEGORY:       Behavioral
 * ALSO KNOWN AS:  Algorithm Skeleton
 * ============================================================================
 * <p>
 * 1. INTENT & CORE PROBLEM
 * Define the skeleton of an algorithm in an operation, deferring some steps 
 * to subclasses. Template Method lets subclasses redefine certain steps of 
 * an algorithm without changing the algorithm's structure.
 * <p>
 * 2. MOTIVATION & REAL-WORLD ANALOGY
 * A UI View framework needs to guarantee that subclasses can draw onto a 
 * screen only after a specific drawing state (e.g., focus, fonts, colors) 
 * is set up, and that the state is properly cleaned up afterward.
 * Analogy: Baking a cake. The recipe provides a strict set of steps 
 * (mix dry ingredients, mix wet, bake for 30 mins). You cannot change the 
 * order, but you can override specific steps (e.g., use chocolate chips 
 * instead of blueberries).
 * <p>
 * 3. APPLICABILITY
 * - To implement the invariant parts of an algorithm once and leave it up 
 * to subclasses to implement the varying behavior.
 * - To refactor and localize common behavior among subclasses to avoid 
 * code duplication.
 * - To control subclass extensions by offering specific "hook" operations.
 * <p>
 * 4. STRUCTURE & PARTICIPANTS
 * - AbstractClass: Defines abstract primitive operations and implements a 
 * template method defining the skeleton of the algorithm.
 * - ConcreteClass: Implements the primitive operations to carry out 
 * subclass-specific steps.
 * <p>
 * 5. COLLABORATIONS
 * ConcreteClass relies on AbstractClass to implement the invariant steps of 
 * the algorithm. AbstractClass handles the orchestration.
 * <p>
 * 6. CONSEQUENCES (TRADE-OFFS)
 * + Promotes strong code reuse (especially in libraries).
 * + Enforces the "Hollywood Principle": Don't call us, we'll call you.
 * + Protects core algorithm logic from accidental modification.
 * - Inherently uses inheritance, coupling the concrete implementations 
 * tightly to the abstract base class.
 * <p>
 * 7. IMPLEMENTATION HINTS & MODERN JAVA CONTEXT
 * In Java, the template method should typically be marked `final` to 
 * prevent overriding. Primitive operations should be `protected` to hide 
 * them from public API consumers but expose them to subclasses. 
 * Modern Java allows putting template methods directly in interfaces using 
 * `default` methods.
 * <p>
 * 8. KNOWN USES & JAVA API USAGE
 * - java.util.AbstractList, java.util.AbstractSet
 * - java.io.InputStream, java.io.OutputStream
 * - Spring Framework's JdbcTemplate and various Abstract* classes.
 * <p>
 * 9. RELATED PATTERNS
 * - Factory Method: Often called within template methods.
 * - Strategy: Varies the entire algorithm via composition, whereas 
 * Template Method varies parts of an algorithm via inheritance.
 * ============================================================================
 */
public class TemplateMethodDemonstration {

    // ========================================================================
    // MOCKED ENTITIES
    // ========================================================================

    /**
     * A mock graphics context to simulate screen rendering state.
     */
    static class GraphicsContext {
        public void applyConfiguration(String state) {
            System.out.println("[GraphicsContext] Applying state: " + state);
        }
        public void drawText(String text) {
            System.out.println("[GraphicsContext] Drawing: " + text);
        }
    }

    /**
     * ========================================================================
     * PHASE 1: THE NAIVE APPROACH (THE PROBLEM)
     * ========================================================================
     * Subclasses must manually remember to set up and tear down state.
     * It is very easy to forget to reset the focus, leading to bugs.
     */
    static class NaiveView {
        protected GraphicsContext gc = new GraphicsContext();

        public void render() {
            // Unenforced, naive execution. Every subclass has to repeat this structure.
            gc.applyConfiguration("Setting Focus");
            System.out.println("NaiveView rendering contents...");
            gc.applyConfiguration("Resetting Focus");
        }
    }

    static class BadCustomView extends NaiveView {
        @Override
        public void render() {
            // The subclass forgets to set up and cleanup the drawing state!
            System.out.println("BadCustomView rendering contents... (Oops, no focus set!)");
        }
    }

    /**
     * ========================================================================
     * PHASE 2: THE PATTERN APPROACH (THE SOLUTION)
     * ========================================================================
     * Implementing the NeXT AppKit example described in the GoF book.
     */

    /**
     * AbstractClass Participant: Enforces the drawing invariant.
     */
    abstract static class View {
        protected final GraphicsContext gc = new GraphicsContext();

        /**
         * The Template Method. Marked final so subclasses cannot break the sequence.
         */
        public final void display() {
            setFocus();
            doDisplay();
            resetFocus();
        }

        // Concrete invariant operations (Hidden from subclasses)
        private void setFocus() {
            gc.applyConfiguration("Focus Gained (Colors/Fonts Initialized)");
        }

        private void resetFocus() {
            gc.applyConfiguration("Focus Reset (Resources Released)");
            System.out.println("--------------------------------------------------");
        }

        /**
         * Primitive Hook Operation. 
         * Subclasses override it to add specific drawing behavior.
         * Default implementation does nothing.
         */
        protected void doDisplay() {
            // Default no-op
        }
    }

    /**
     * ConcreteClass Participant: Fleshes out the specific steps.
     */
    static class MyTextView extends View {
        private final String text;

        public MyTextView(String text) {
            this.text = text;
        }

        @Override
        protected void doDisplay() {
            gc.drawText("Text Box -> " + text);
        }
    }

    /**
     * ConcreteClass Participant: Demonstrating another variation.
     */
    static class MyImageView extends View {
        private final String imagePath;

        public MyImageView(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        protected void doDisplay() {
            gc.drawText("Image rendering from -> " + imagePath);
        }
    }

    /**
     * ========================================================================
     * PHASE 3: EXECUTION (MAIN METHOD)
     * ========================================================================
     */
    public static void main(String[] args) {
        System.out.println("--- Template Method: Naive Approach ---");
        BadCustomView badView = new BadCustomView();
        badView.render(); // Executes without required invariant state

        System.out.println("\n--- Template Method: Pattern Approach ---");
        // Clients only interact with the Template Method, never the primitive hooks
        View textView = new MyTextView("Hello, Design Patterns!");
        textView.display();

        View imageView = new MyImageView("/assets/logo.png");
        imageView.display();
    }
}